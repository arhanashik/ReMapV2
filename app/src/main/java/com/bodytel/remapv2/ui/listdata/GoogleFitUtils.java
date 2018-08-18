package com.bodytel.remapv2.ui.listdata;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.ReMapApp;
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
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bodytel.remapv2.ui.listdata.GoogleFitDistanceActivity.REQUEST_PERMISSIONS_REQUEST_CODE;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

/**
 * Created by fcampos on 17/05/2018.
 */

public class GoogleFitUtils {

    private final static String LOG_TAG = GoogleFitUtils.class.getName();
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 200;
    private static final int SENSORS_REQUEST_CODE = 300;

    private static boolean checkPermissionsAccessFineLocation() {

        int permissionState = ActivityCompat.checkSelfPermission(ReMapApp.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    public static void checkSensorsPermission(Activity activity) {

        if (!checkPermissionsBodySensor()) {

            if (activity != null) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BODY_SENSORS}, SENSORS_REQUEST_CODE);
            }
        }
    }


    public static void checkAccessFineLocationPermission(Activity activity) {

        if (!checkPermissionsAccessFineLocation()) {

            if (activity != null) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            }
        }
    }

    // Sensor
    private static boolean checkPermissionsBodySensor() {
        int permissionState = ActivityCompat.checkSelfPermission(ReMapApp.getContext(), Manifest.permission.BODY_SENSORS);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    /*
   Build a GoogleApiClient that will authenticate the user and allow the application to connect to Fitness APIs.
    */
    public static GoogleApiClient buildFitnessClient(Activity activity) {

        Log.d(LOG_TAG, "buildFitnessClient called");
        GoogleApiClient client = null;
        if (GoogleFitUtils.checkPermissionsAccessFineLocation() && GoogleFitUtils.checkPermissionsBodySensor()) {

            client = new GoogleApiClient.Builder(activity)
                    .addApi(Fitness.RECORDING_API)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.SENSORS_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(@Nullable Bundle bundle) {
                                    Log.d(LOG_TAG, "buildFitnessClient connected");
                                    // Now you can make calls to the Fitness APIs
                                    // the HomeFragment will call the "subscribeDailySteps()"
                                }

                                @Override
                                public void onConnectionSuspended(int i) {

                                    Log.i(LOG_TAG, "buildFitnessClient connection suspended");
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.w(LOG_TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.w(LOG_TAG, "Connection lost.  Reason: Service Disconnected");
                                    }
                                }

                            }
                    )
                    .enableAutoManage((GoogleFitDistanceActivity) activity, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(LOG_TAG, "Google Play services failed. Cause: " + connectionResult.toString());

                        }
                    })
                    .build();

        }

        return client;
    }


    public static class VerifyDataTaskDistance extends AsyncTask<GoogleApiClient, Void, Void> {

        float total = 0;
        Context context;
        DistanceListener distanceListener;

        VerifyDataTaskDistance(Context context, DistanceListener listener) {
            this.context = context;
            this.distanceListener = listener;
        }

        protected Void doInBackground(GoogleApiClient... clients) {

            PendingResult<DataReadResult> result = Fitness.HistoryApi.readData(clients[0], queryFitnessData());

            DataReadResult totalResult = result.await(30, TimeUnit.SECONDS);

            List<Bucket> buckets = totalResult.getBuckets();
            Log.e(LOG_TAG, "Point value  size=" + buckets.size());
            DateFormat dateFormat = getTimeInstance();

            for (Bucket bucket : buckets) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        Log.i(LOG_TAG, "Data point:");
                        Log.i(LOG_TAG, "\tType: " + dp.getDataType().getName());
                        Log.i(LOG_TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.i(LOG_TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                        for (Field field : dp.getDataType().getFields()) {
                            Log.i(LOG_TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            // UI
            //updateDistanceGoogle(total, MainActivity.mainActivity.getSupportFragmentManager());

        }

    }


    /**
     * Returns a {@link DataReadRequest} for all step count changes in the past week.
     */
    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DateFormat dateFormat = getDateInstance();
        Log.i(LOG_TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(LOG_TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]

        return readRequest;
    }

    private static void readDistanceToday(Context context, GoogleApiClient client, DistanceListener listener) {
        new VerifyDataTaskDistance(context, listener).execute(client);
    }


    // ----------- Google Fit Daily DISTANCE -----------
    public static void subscribeDailyDistance(final Context context, GoogleApiClient client, DistanceListener listener) {


        Log.d(LOG_TAG, "subscribeDailyDistance was called");

        if (client != null) {

            // To create a subscription, invoke the Recording API.
            // As soon as the subscription is active, fitness data will start recording
            Fitness.RecordingApi.subscribe(client, DataType.TYPE_DISTANCE_DELTA)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {

                            if (status.isSuccess()) {

                                if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                    Log.d(LOG_TAG, "Existing subscription for activity detected.");

                                } else {
                                    Log.d(LOG_TAG, "Successfully subscribed");

                                }

                                readDistanceToday(context, client, listener);

                            } else {
                                Log.e(LOG_TAG, "There was a problem subscribing");
                            }

                        }
                    });

        }

    }


}