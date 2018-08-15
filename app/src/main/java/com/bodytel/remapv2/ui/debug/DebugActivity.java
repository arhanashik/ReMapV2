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
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyActivity;
import com.bodytel.remapv2.ui.listdata.ListDataActivity;

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
        Intent intent = new Intent(this, ListDataActivity.class);
        intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_DISTANCE);
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
        Intent intent = new Intent(this, ListDataActivity.class);
        intent.putExtra(AppConst.LIST_DATA_TYPE, AppConst.TYPE_ACCELEROMETER_DATA);
        startActivity(intent);
    }

    public void onClickNewSleepEntry(View view){

    }

    public void onClickNewMoodEntry(View view){

    }

    public void onClickScheduleNotification(View view){

    }
}
