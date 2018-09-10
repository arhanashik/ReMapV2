package com.bodytel.util.lib.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataService;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.util.helper.FileUtil;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.StoreSensorDataCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.work.Worker;

public class StoreSensorDataWorker extends Worker{
    private static final String TAG = StoreSensorDataWorker.class.getSimpleName();

    private AccelerometerDataService dataService;

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        WorkerUtils.makeStatusNotification("Worker thread for storing data", applicationContext);

        try {
            new Thread(()->{

                dataService = DatabaseHelper.provideAccelerometerDataService();
                List<AccelerometerDataModel> dataModels = dataService.getAllAccelerometerDataModel();

                if(dataModels != null && !dataModels.isEmpty()){
                    NetworkApi.on().storeSensorData(dataModels, new StoreSensorDataCallback() {
                        @Override
                        public void onStoreSensorDataSuccessfully(@NotNull String dataId) {
                            Log.e(TAG, "ReMap Accelerometer Data stored: " + dataId);
                        }

                        @Override
                        public void onStoreSensorDataFailure(@NotNull String error) {
                            Log.e(TAG, "ReMap error: " + error);
                        }
                    });
                    dataService.deleteAllAccelerometerData();
                    FileUtil.addNewLogOnSD("Total accelerometer data sent " + dataModels.size() + " items");
                }

            }).start();

            return Result.SUCCESS;
        } catch (Throwable throwable) {
            Log.e(TAG, "Error storing accelerometer data", throwable);
            FileUtil.addNewLogOnSD(throwable.getMessage());
            return Result.RETRY;
        }
    }
}
