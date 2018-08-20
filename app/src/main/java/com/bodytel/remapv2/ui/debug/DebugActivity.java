package com.bodytel.remapv2.ui.debug;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.service.FitJobService;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyActivity;
import com.bodytel.remapv2.ui.listdata.GoogleFitDistanceActivity;
import com.bodytel.remapv2.ui.listdata.ListDataActivity;
import com.bodytel.remapv2.ui.listdata.SensorActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

public class DebugActivity extends AppCompatActivity {

    private TextView txtSubjectId;

    private PrefGlobal prefGlobal;
    private final int REQUEST_OAUTH_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSubjectId = findViewById(R.id.activity_debug_txt_subject_id);

        prefGlobal = PrefHelper.providePrefGlobal();
        txtSubjectId.setText(prefGlobal.getSubjectId());

        initFitnessApiAndCheckPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_debug, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_debug_action_done){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSteps(View view){
        Intent intent = new Intent(this, ListDataActivity.class);
        intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_STEPS);
        startActivity(intent);
    }

    public void onClickDistance(View view){
        Intent intent = new Intent(this, GoogleFitDistanceActivity.class);
        //intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_DISTANCE);
        startActivity(intent);
    }

    public void onClickNewAudioSample(View view){

    }

    public void onClickAudioSamples(View view){
        Intent intent = new Intent(this, ListDataActivity.class);
        intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_AUDIO_SAMPLE);
        startActivity(intent);
    }

    public void onClickNewBdiSurvey(View view){
        startActivity(new Intent(this, BdiSurveyActivity.class));
    }

    public void onClickAccelerometerData(View view){
        Intent intent = new Intent(this, SensorActivity.class);
        //intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_ACCELEROMETER_DATA);
        startActivity(intent);
    }

    public void onClickNewSleepEntry(View view){

    }

    public void onClickNewMoodEntry(View view){

    }

    public void onClickScheduleNotification(View view){

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
            startFitnessService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                startFitnessService();
            }
        }
    }

    private void startFitnessService(){
        if(Build.VERSION.SDK_INT >=21 ) {
            ComponentName componentName = new ComponentName(this, FitJobService.class);

            JobInfo jobInfo = new JobInfo.Builder(123, componentName)
                    .setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .setPeriodic(1 * 60 * 1000)
                    .build();

            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            int result = jobScheduler.schedule(jobInfo);

            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.e("MyJobservice", "Success");
            } else {
                Log.e("MyJobservice", "Job failed");
            }
        }
    }
}
