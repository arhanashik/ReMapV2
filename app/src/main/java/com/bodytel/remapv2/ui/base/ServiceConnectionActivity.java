package com.bodytel.remapv2.ui.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.service.ReMapService;
import com.bodytel.util.lib.worker.FitDataCollectorWorker;
import com.bodytel.util.lib.worker.StoreDistanceDataWorker;
import com.bodytel.util.lib.worker.StoreSensorDataWorker;
import com.bodytel.util.lib.worker.StoreStepsDataWorker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public abstract class ServiceConnectionActivity extends AppCompatActivity {
    private final int REQUEST_OAUTH_REQUEST_CODE = 1;
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

        if(checkPermission()){
            initFitnessApiAndCheckPermission();
        }else {
            requestPermissions();
        }
    }

    private void initFitnessApiAndCheckPermission() {
        FitnessOptions options = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_DISTANCE_DELTA,  FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), options)) {
            GoogleSignIn.requestPermissions(this, REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this), options);
        } else {
            //insertAndReadData();
            /**
             * Start fitness data read here
             *
             *
             */
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == AppConst.REQUEST_CODE_STORAGE_PERMISSION){
            if(grantResults.length > 0){
                for(int res : grantResults){
                    if(res != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                        break;
                    }
                }
                initFitnessApiAndCheckPermission();
            }else {
                Toast.makeText(this, "Permission needed", Toast.LENGTH_SHORT).show();
                requestPermissions();
            }
        }
    }

    private boolean checkPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                AppConst.REQUEST_CODE_STORAGE_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                //insertAndReadData();
                //insertAndReadData();
                /**
                 * Start fitness data read here
                 *
                 *
                 */
            }
        }
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
}
