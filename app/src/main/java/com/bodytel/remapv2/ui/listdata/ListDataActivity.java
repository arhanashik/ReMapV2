package com.bodytel.remapv2.ui.listdata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.listdata.StepModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
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

public class ListDataActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private RecyclerView mRvListData;
    private ListDataAdapter mAdapter;
    private final int REQUEST_OAUTH_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvListData = findViewById(R.id.activity_list_data_recycler_view);

        int listDataType = getIntent().getIntExtra(AppConst.LIST_DATA_TYPE, 0);

        initView(listDataType);

        //Fitness init
        initFitnessApiAndCheckPermission();
    }


    private void initView(int listDataType) {
        /*List<ListDataModel> dataModels = new ArrayList<>();

        if (listDataType == AppConst.TYPE_STEPS) {
            dataModels.add(new ListDataModel(1, "8.0", "01.08.18, 08:38"));
            dataModels.add(new ListDataModel(2, "13.0", "01.08.18, 08:36"));
            dataModels.add(new ListDataModel(3, "40.0", "31.08.18, 23:12"));
            dataModels.add(new ListDataModel(4, "28.0", "31.08.18, 22:59"));
            dataModels.add(new ListDataModel(5, "18.0", "31.08.18, 22:01"));
            dataModels.add(new ListDataModel(6, "37.0", "31.08.18, 19:28"));
            dataModels.add(new ListDataModel(7, "21.0", "31.08.18, 18:48"));
            dataModels.add(new ListDataModel(8, "20.0", "31.08.18, 18:31"));
            dataModels.add(new ListDataModel(9, "39.0", "31.08.18, 18:25"));
            dataModels.add(new ListDataModel(10, "42.0", "31.08.18, 18:19"));
        }*/

        mAdapter = new ListDataAdapter();
        mRvListData.setHasFixedSize(true);
        mRvListData.setLayoutManager(new LinearLayoutManager(this));
        mRvListData.setAdapter(mAdapter);
    }

    private void initFitnessApiAndCheckPermission() {
        FitnessOptions options = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), options)) {
            GoogleSignIn.requestPermissions(this, REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this), options);
        } else {
            insertAndReadData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                insertAndReadData();
            }
        }
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
        DataReadRequest readRequest = queryFitnessData();

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
                                printData(dataReadResponse);
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
    public DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
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
                        .bucketByTime(1, TimeUnit.DAYS)
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
    public void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    ShowDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                ShowDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void ShowDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {

            String startDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            String value = "";
            List<Field> fields = dp.getDataType().getFields();
            for (Field item : fields) {
                if ("steps".equals(item.getName())) {
                    value = "" + dp.getValue(item);
                }
            }

            StepModel stepModel = new StepModel(value, startDate, endDate);
            mAdapter.addItem(stepModel);


            /*Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }*/
        }
    }


}
