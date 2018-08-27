package com.bodytel.remapv2.data.local.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bodytel.remapv2.data.local.db.AppDatabase;
import com.bodytel.remapv2.data.local.db.StepsDao;
import com.bodytel.remapv2.data.local.listdata.DistanceModel;
import com.bodytel.remapv2.data.local.listdata.StepModel;
import com.bodytel.remapv2.ui.listdata.GoogleFitDistanceActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.ArrayList;
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

                insertAndReadData();
                //buildFitnessClient();
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
        DataReadRequest readRequest = queryStepsData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
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

    /**
     * Returns a {@link DataReadRequest} for all step count changes in the past week.
     */
    public DataReadRequest queryStepsData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
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
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.HOURS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]

        return readRequest;
    }

    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would dump
     * all the data. In this sample, logging also prints to the device screen, so we can see what the
     * query returns, but your app should not log fitness information as a privacy consideration. A
     * better option would be to dump the data you receive to a local data directory to avoid exposing
     * it to other applications.
     */
    public void getDataFromResult(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void showDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {

            long startDate = dp.getStartTime(TimeUnit.MILLISECONDS);
            long endDate = dp.getEndTime(TimeUnit.MILLISECONDS);
            int value = 0;
            List<Field> fields = dp.getDataType().getFields();
            for (Field item : fields) {
                if ("steps".equals(item.getName())) {
                    value =dp.getValue(item).asInt();
                }
            }

            StepModel stepModel = new StepModel(value, startDate, endDate);
            //mAdapter.addItem(stepModel);
            new Thread(()->stepsDao.insert(stepModel)).start();

        }
    }


    /***************************Distance**********************/

/*

    public void buildFitnessClient() {
        Log.d(TAG, "buildFitnessClient called");

        client = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                Log.d(TAG, "buildFitnessClient connected");
                                // Now you can make calls to the Fitness APIs
                                // the HomeFragment will call the "subscribeDailySteps()"
                                subscribeDailyDistance();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                                Log.i(TAG, "buildFitnessClient connection suspended");
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.w(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.w(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }

                        }
                )
                */
/*.enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Google Play services failed. Cause: " + connectionResult.toString());

                    }
                })*//*

                .build();
    }


    public void subscribeDailyDistance() {
        // To create a subscription, invoke the Recording API.
        // As soon as the subscription is active, fitness data will start recording
        Fitness.RecordingApi.subscribe(client, DataType.TYPE_DISTANCE_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                        if (status.isSuccess()) {

                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.d(TAG, "Existing subscription for activity detected.");

                            } else {
                                Log.d(TAG, "Successfully subscribed");

                            }
                            new VerifyDataTaskDistance().execute(client);

                        } else {
                            Log.e(TAG, "There was a problem subscribing");
                        }

                    }
                });


    }

    private class VerifyDataTaskDistance extends AsyncTask<GoogleApiClient, Void, Void> {

        List<DistanceModel> modelList = new ArrayList<>();

        protected Void doInBackground(GoogleApiClient... clients) {

            PendingResult<DataReadResult> result = Fitness.HistoryApi.readData(clients[0], queryFitnessData());

            DataReadResult totalResult = result.await(30, TimeUnit.SECONDS);

            if (totalResult.getBuckets().size() > 0) {
                Log.i(TAG, "Number distance bucket: " + totalResult.getBuckets().size());
                for (Bucket bucket : totalResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        DistanceModel distanceModel = showDataSet(dataSet);
                        if (distanceModel != null) {
                            modelList.add(distanceModel);
                        }
                    }
                }
            } else if (totalResult.getDataSets().size() > 0) {
                Log.i(TAG, "Number of distance dataset: " + totalResult.getDataSets().size());
                for (DataSet dataSet : totalResult.getDataSets()) {
                    DistanceModel distanceModel = showDataSet(dataSet);
                    if (distanceModel != null) {
                        modelList.add(distanceModel);
                    }
                }
            }

            return null;
        }

        private DistanceModel showDataSet(DataSet dataSet) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                long startDate = dp.getStartTime(TimeUnit.MILLISECONDS);
                long endDate = dp.getEndTime(TimeUnit.MILLISECONDS);
                float value = 0.0f;
                List<Field> fields = dp.getDataType().getFields();
                for (Field item : fields) {
                    value = dp.getValue(item).asFloat();
                }
                return new DistanceModel(value, startDate, endDate);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            //adapter.addItem(modelList);

            for(DistanceModel item: modelList){
                Log.i(TAG,"Distance data  ="+item.toString());
            }

        }

    }


    */
/**
     * Returns a {@link DataReadRequest} for all step count changes in the past week.
     *//*

    public DataReadRequest queryFitnessData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                        .bucketByTime(1, TimeUnit.HOURS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        return readRequest;
    }
*/

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "Job cancelled before complete");
        jobCancelled = true;
        return false;
    }
}
