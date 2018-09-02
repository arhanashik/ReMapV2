package com.bodytel.util.lib.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataService;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.util.helper.FileUtil;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.StoreSensorDataCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.work.Worker;

public class StoreSensorDataWorker extends Worker implements StoreSensorDataCallback{
    private static final String TAG = StoreSensorDataWorker.class.getSimpleName();

    private PrefGlobal prefGlobal;
    private AccelerometerDataService dataService;

    @NonNull
    @Override
    public Result doWork() {

        Context applicationContext = getApplicationContext();

        // Makes a notification when the work starts and slows down the work so that it's easier to
        // see each WorkRequest start, even on emulated devices
        WorkerUtils.makeStatusNotification("Storing sensor data", applicationContext);

        FileUtil.addNewLogOnSD();

        try {
            prefGlobal = PrefHelper.providePrefGlobal();

            new Thread(()->{

                dataService = DatabaseHelper.provideAccelerometerDataService();
                List<AccelerometerDataModel> dataModels = dataService.getAllAccelerometerDataModel();

                if(dataModels != null && !dataModels.isEmpty()){
                    NetworkApi
                            .on()
                            .storeSensorData(prefGlobal.getSubjectId(), dataModels, this);

                    dataService.deleteAllAccelerometerData();
                }
            }).start();

            // If there were no errors, return SUCCESS
            return Result.SUCCESS;
        } catch (Throwable throwable) {

            // If there were errors, return FAILURE
            Log.e(TAG, "Error storing sensor data", throwable);
            return Result.FAILURE;
        }
    }

    @Override
    public void onStoreSensorDataSuccessfully(@NotNull String dataId) {
        //Toast.makeText(getApplicationContext(), "ReMap Data stored: " + dataId, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "ReMap Data stored: " + dataId);
        new Thread(()->{
            //dataService.deleteAllAccelerometerData();
        }).start();
    }

    @Override
    public void onStoreSensorDataFailure(@NotNull String error) {
        //Toast.makeText(getApplicationContext(), "ReMap error: " + error, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "ReMap error: " + error);
    }
}
