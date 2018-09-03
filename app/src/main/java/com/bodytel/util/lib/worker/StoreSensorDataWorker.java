package com.bodytel.util.lib.worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.ReMapApp;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataService;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.util.helper.FileUtil;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.StoreSensorDataCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Worker;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;
public class StoreSensorDataWorker extends Worker implements StoreSensorDataCallback{
    private static final String TAG = StoreSensorDataWorker.class.getSimpleName();

    private PrefGlobal prefGlobal;
    private AccelerometerDataService dataService;
    private final String STEPS = "steps";
    private final String DISTANCE = "distance";

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
                    NetworkApi.on().storeSensorData(prefGlobal.getSubjectId(), dataModels, this);
                    dataService.deleteAllAccelerometerData();
                }

                /**
                 *
                 * Data read start
                 *
                 *
                 */
                readHistoryData();

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




    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the data.
     */
    private Task<DataReadResponse> readHistoryData() {
        // Begin by creating the query.
        DataReadRequest readRequest = buildQueryFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(ReMapApp.getContext(), GoogleSignIn.getLastSignedInAccount(ReMapApp.getContext()))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                getDataFromResult(dataReadResponse);
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }


    /**
     *
     * Fitness query builder
     *
     * Steps and distance data read query builder
     *
     * @return
     */
    public DataReadRequest buildQueryFitnessData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                        .bucketByTime(1, TimeUnit.HOURS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

        return readRequest;
    }

    public void getDataFromResult(DataReadResponse dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    parseDataFromDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                parseDataFromDataSet(dataSet);
            }
        }

    }

    private void parseDataFromDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            /**
             *
             * Start end date
             *
             */
            long startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
            long endTime = dp.getEndTime(TimeUnit.MILLISECONDS);
            for (Field field : dp.getDataType().getFields()) {
                String dataType = field.getName();
                String value = "";
                if(STEPS.equals(dataType)){
                    /**
                     *
                     * Steps value
                     *
                     */
                    value =  dp.getValue(field).toString();
                    Log.i(TAG, "Field: " + field.getName() +"  Start "+startTime+" End ="+endTime+ " Value: " + value);
                }else {
                    /**
                     *
                     * Distance data
                     *
                     */
                    value = dp.getValue(field).toString();
                    Log.i(TAG, "Field: " + field.getName() + " Value: " + value);
                }

            }
        }
    }

}
