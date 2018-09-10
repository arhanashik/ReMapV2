package com.bodytel.util.lib.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.remapv2.data.local.fitdata.FitDataModel;
import com.bodytel.remapv2.data.local.fitdata.FitDataModelService;
import com.bodytel.util.helper.FileUtil;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.StoreSensorDataCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.work.Worker;

public class StoreDistanceDataWorker extends Worker{
    private static final String TAG = StoreDistanceDataWorker.class.getSimpleName();

    private FitDataModelService dataService;

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        WorkerUtils.makeStatusNotification("Worker thread for storing data", applicationContext);

        try {
            new Thread(()->{

                dataService = DatabaseHelper.provideFitDataModelService();
                List<FitDataModel> dataModels = dataService.getDataListOfType(AppConst.DISTANCE);

                if(dataModels != null && !dataModels.isEmpty()){
                    NetworkApi.on().storeStepsOrDistanceData(AppConst.DISTANCE, dataModels, new StoreSensorDataCallback() {
                        @Override
                        public void onStoreSensorDataSuccessfully(@NotNull String dataId) {
                            Log.e(TAG, "ReMap Distance Data stored: " + dataId);
                        }

                        @Override
                        public void onStoreSensorDataFailure(@NotNull String error) {
                            Log.e(TAG, "ReMap error: " + error);
                        }
                    });
                    dataService.deleteDataOfType(AppConst.DISTANCE);

                    FileUtil.addNewLogOnSD("Total distance data sent " + dataModels.size() + " items");
                }

            }).start();

            return Result.SUCCESS;
        } catch (Throwable throwable) {
            Log.e(TAG, "Error storing distance data", throwable);
            FileUtil.addNewLogOnSD(throwable.getMessage());
            return Result.RETRY;
        }
    }
}
