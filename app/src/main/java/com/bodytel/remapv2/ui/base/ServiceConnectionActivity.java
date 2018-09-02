package com.bodytel.remapv2.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.service.ReMapService;
import com.bodytel.util.lib.worker.StoreSensorDataWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public abstract class ServiceConnectionActivity extends AppCompatActivity {

    private WorkManager mWorkManager;

    /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            keepServiceRunningInForeground();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWorkManager = WorkManager.getInstance();

        sendDataToFirestore();
    }

    @Override
    protected void onStart() {
        super.onStart();
        keepServiceRunningInForeground();
        serviceBindAndStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        keepServiceRunningInBackground();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    private void serviceBindAndStart(){
        Intent intent = new Intent(this, ReMapService.class);
        bindService(intent, mConnection,Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void keepServiceRunningInBackground(){
        Message msg = Message.obtain(null, ReMapService.RUN_BACKGROUND, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void keepServiceRunningInForeground(){
        Message msg = Message.obtain(null, ReMapService.RUN_FOREGROUND, 0, 0);
        try {
            if(mService != null)
                mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    void sendDataToFirestore() {

        // Create charging constraint
        Constraints constraints = new Constraints.Builder()
                //.setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Add WorkRequest to save the image to the filesystem
//        OneTimeWorkRequest storeData = new OneTimeWorkRequest.Builder(StoreSensorDataWorker.class)
//                .setConstraints(constraints)
//                .addTag(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE)
//                .build();

        // Add WorkRequest to Cleanup temporary images
//        WorkContinuation continuation = mWorkManager
//                .beginUniqueWork(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE,
//                        ExistingWorkPolicy.REPLACE, storeData);

        //continuation = continuation.then(save);

        // Actually start the work
        //continuation.enqueue();

        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest
                .Builder(StoreSensorDataWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(AppConst.JOB_TAG_SEND_DATA_TO_REMOTE)
                .build();
        mWorkManager.enqueue(periodicWork);
    }
}
