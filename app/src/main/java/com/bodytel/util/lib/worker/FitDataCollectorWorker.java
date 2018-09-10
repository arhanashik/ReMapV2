package com.bodytel.util.lib.worker;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bodytel.ReMapApp;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.remapv2.data.local.fitdata.FitDataModel;
import com.bodytel.remapv2.data.local.fitdata.FitDataModelService;
import com.bodytel.util.helper.FileUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Worker;

import static java.text.DateFormat.getDateInstance;

public class FitDataCollectorWorker extends Worker {
    private static final String TAG = FitDataCollectorWorker.class.getSimpleName();

    private FitDataModelService fitDataModelService;

    @NonNull
    @Override
    public Result doWork() {
        try {
            FileUtil.addNewLogOnSD("Fit data worker triggered");

            // Begin by creating the query.
            DataReadRequest readRequest = buildQueryFitnessData();

            // Invoke the History API to fetch the data with the query
            Fitness.getHistoryClient(ReMapApp.getContext(), GoogleSignIn.getLastSignedInAccount(ReMapApp.getContext()))
                    .readData(readRequest)
                    .addOnSuccessListener(this::getDataFromResult)
                    .addOnFailureListener(e -> Log.e(TAG, "There was a problem reading the data.", e));

            // If there were no errors, return SUCCESS
            return Result.SUCCESS;
        } catch (Throwable throwable) {
            // If there were errors, return FAILURE
            Log.e(TAG, "Error storing fit data", throwable);
            FileUtil.addNewLogOnSD(throwable.getMessage());
            return Result.RETRY;
        }
    }

    private DataReadRequest buildQueryFitnessData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        return new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
    }

    private void getDataFromResult(DataReadResponse dataReadResult) {

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

        if(fitDataModelService == null) {
            fitDataModelService = DatabaseHelper.provideFitDataModelService();
        }

        for (DataPoint dp : dataSet.getDataPoints()) {
            long startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
            long endTime = dp.getEndTime(TimeUnit.MILLISECONDS);

            FitDataModel model = new FitDataModel();
            model.setCreatedAt(System.currentTimeMillis());
            model.setStart(startTime);
            model.setEnd(endTime);

            for (Field field : dp.getDataType().getFields()) {
                String dataType = field.getName();
                String value = dp.getValue(field).toString();

                model.setType(dataType);
                model.setValue(value);

                if(dataType.equals(AppConst.STEPS)){
                    Log.i(TAG, "Field: " + field.getName() +"  Start "+startTime+" End ="+endTime+ " Value: " + value);
                }else {
                    Log.i(TAG, "Field: " + field.getName() +"  Start "+startTime+" End ="+endTime+ " Value: " + value);
                }
            }

            fitDataModelService.insert(model);
        }
    }
}
