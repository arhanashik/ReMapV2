package com.bodytel.remapv2.data.local.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bodytel.remapv2.data.local.db.AppDatabase;
import com.bodytel.remapv2.data.local.db.StepsDao;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;



/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 8/20/2018 at 2:51 PM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 8/20/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FitJobService extends JobService {
    private final String TAG = getClass().getName();
    private boolean jobCancelled = false;
    private StepsDao stepsDao;
    public static GoogleApiClient client = null;

    private final String STEPS = "steps";
    private final String DISTANCE = "distance";

    public FitJobService(){
        stepsDao = AppDatabase.on().getStepDao();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        readDataInBackground(params);
        return true;
    }

    private void readDataInBackground(JobParameters parameters){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }
                Log.i(TAG, "Job started.");
                readHistoryData();
                //insertAndReadData();
                jobFinished(parameters, false);
            }
        }).start();
    }


    /**
     * Inserts and reads data by chaining {@link Task} from {@link #insertData()} and {@link
     * #readHistoryData()}.
     */
    private void insertAndReadData() {
        insertData().continueWithTask(new Continuation<Void, Task<DataReadResponse>>() {
            @Override
            public Task<DataReadResponse> then(@NonNull Task<Void> task) throws Exception {
                return readHistoryData();
            }
        });
    }

    /**
     * Creates a {@link DataSet} and inserts it into user's Google Fit history.
     */
    private Task<Void> insertData() {
        /**
         *   Create a new data set and insertion request.
         */

        DataSet dataSet = insertFitnessData();

        // Then, invoke the History API to insert the data.
        Log.i(TAG, "Inserting the dataset in the History API.");

        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .insertData(dataSet).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point, the data has been inserted and can be read.
                                    Log.i(TAG, "Data insert was successful!");
                                } else {
                                    Log.e(TAG, "There was a problem inserting the dataset.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Creates and returns a {@link DataSet} of step count data for insertion using the History API.
     */
    private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request.");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .setStreamName(TAG + " - step count")
                        .setType(DataSource.TYPE_RAW)
                        .build();

        // Create a data set
        int stepCountDelta = 950;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint =  dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the data.
     */
    private Task<DataReadResponse> readHistoryData() {
        // Begin by creating the query.
        DataReadRequest readRequest = buildQueryFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                getDataFromResult(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }


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
            long startTime = dp.getStartTime(TimeUnit.MILLISECONDS);
            long endTime = dp.getEndTime(TimeUnit.MILLISECONDS);
            for (Field field : dp.getDataType().getFields()) {
                String dataType = field.getName();
                String value = "";
                if(STEPS.equals(dataType)){
                    value =  dp.getValue(field).toString();
                    Log.i(TAG, "Field: " + field.getName() +"  Start "+startTime+" End ="+endTime+ " Value: " + value);
                }else {
                    value = dp.getValue(field).toString();
                    Log.i(TAG, "Field: " + field.getName() + " Value: " + value);
                }


            }
        }
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "Job cancelled before complete");
        jobCancelled = true;
        return false;
    }
}
