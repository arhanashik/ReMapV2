package com.bodytel.remapv2.ui.listdata;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.ReMapApp;
import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.listdata.DistanceModel;
import com.bodytel.remapv2.data.local.listdata.StepModel;
import com.bodytel.util.helper.PermissionUtil;
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
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class GoogleFitDistanceActivity extends AppCompatActivity {
    public static GoogleApiClient client = null;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private final String LOG_TAG = "GoogleFitUtils";
    RecyclerView recyclerView;
    DistanceListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        // Get the Google Fit API
        checkPermissionAndBuildClient();
    }

    private void initView() {
        recyclerView = findViewById(R.id.activity_list_data_recycler_view);
        adapter = new DistanceListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void checkPermissionAndBuildClient() {
        if (PermissionUtil.getInstance().isPermitted(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS)) {
            buildFitnessClient();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(LOG_TAG, "User interaction was cancelled");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                checkPermissionAndBuildClient();
            } else {
                // Permission denied
                Toast.makeText(ReMapApp.getContext(), "Permission denied for Google FIT", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void buildFitnessClient() {
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
                                Log.d(LOG_TAG, "buildFitnessClient connected");
                                // Now you can make calls to the Fitness APIs
                                // the HomeFragment will call the "subscribeDailySteps()"
                                subscribeDailyDistance();
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
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(LOG_TAG, "Google Play services failed. Cause: " + connectionResult.toString());

                    }
                })
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
                                Log.d(LOG_TAG, "Existing subscription for activity detected.");

                            } else {
                                Log.d(LOG_TAG, "Successfully subscribed");

                            }
                            new VerifyDataTaskDistance().execute(client);

                        } else {
                            Log.e(LOG_TAG, "There was a problem subscribing");
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
                Log.i(LOG_TAG, "Number of returned buckets of DataSets is: " + totalResult.getBuckets().size());
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
                Log.i(LOG_TAG, "Number of returned DataSets is: " + totalResult.getDataSets().size());
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
            adapter.addItem(modelList);

        }

    }


    /**
     * Returns a {@link DataReadRequest} for all step count changes in the past week.
     */
    public DataReadRequest queryFitnessData() {
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
                        .bucketByTime(1, TimeUnit.HOURS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]

        return readRequest;
    }
}
