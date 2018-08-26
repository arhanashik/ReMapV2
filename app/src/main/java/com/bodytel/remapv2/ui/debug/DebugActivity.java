package com.bodytel.remapv2.ui.debug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.audiosample.AudioSampleActivity;
import com.bodytel.remapv2.ui.bdisurvey.StoreBdiSurveyActivity;
import com.bodytel.remapv2.ui.listdata.GoogleFitDistanceActivity;
import com.bodytel.remapv2.ui.listdata.ListDataActivity;
import com.bodytel.remapv2.ui.listdata.SensorActivity;
import com.bodytel.remapv2.ui.moodsurvey.MoodSurveyActivity;
import com.bodytel.remapv2.ui.audiosample.NewAudioSampleActivity;
import com.bodytel.remapv2.ui.sleepsurvey.SleepSurveyActivity;

public class DebugActivity extends AppCompatActivity {

    private TextView txtSubjectId;

    private PrefGlobal prefGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSubjectId = findViewById(R.id.activity_debug_txt_subject_id);

        prefGlobal = PrefHelper.providePrefGlobal();
        txtSubjectId.setText(prefGlobal.getSubjectId());
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
        startActivity(new Intent(this, NewAudioSampleActivity.class));
    }

    public void onClickAudioSamples(View view){
        startActivity(new Intent(this, AudioSampleActivity.class));
    }

    public void onClickNewBdiSurvey(View view){
        startActivity(new Intent(this, StoreBdiSurveyActivity.class));
    }

    public void onClickAccelerometerData(View view){
        Intent intent = new Intent(this, SensorActivity.class);
        //intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_ACCELEROMETER_DATA);
        startActivity(intent);
    }

    public void onClickNewSleepEntry(View view){
        startActivity(new Intent(this, SleepSurveyActivity.class));
    }

    public void onClickNewMoodEntry(View view){
        startActivity(new Intent(this, MoodSurveyActivity.class));
    }

    public void onClickScheduleNotification(View view){

    }
}
