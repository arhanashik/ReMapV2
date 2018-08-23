package com.bodytel.remapv2.ui.audiosample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleService;
import com.bodytel.remapv2.data.local.dbstorage.DatabaseHelper;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyEndFragment;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.StoreAudioSampleCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class NewAudioSampleActivity extends AppCompatActivity implements OnNewAudioSampleEvent, StoreAudioSampleCallback{

    private boolean isRecordingDone;
    private MenuItem menuItem;

    private PrefGlobal prefGlobal;
    private AudioSampleService audioSampleService;

    private ProgressDialog progressDialog;

    private String fileName = null, filePath = null;
    private MediaRecorder mRecorder = null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_audio_sample);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        prefGlobal = PrefHelper.providePrefGlobal();
        audioSampleService = DatabaseHelper.provideAudioSampleService();

        NewAudioSampleInfoFragment fragment = new NewAudioSampleInfoFragment();
        fragment.setListener(this);
        replaceFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        menuItem = menu.getItem(0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_cancel){
            if(!isRecordingDone) {
                Toast.makeText(this, "Sample collection canceled", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConst.REQUEST_CODE_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoRecordingFragment();
                } else {
                    Toast.makeText(NewAudioSampleActivity.this,
                            "Without permission Audio recording is not possible",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @Override
    public void onPrepareNewAudioRecording() {
        if(hasRecordPermission()) {
            gotoRecordingFragment();
        }else {
            requestRecordPermission();
        }
    }

    @Override
    public void onRequestNewAudioRecording() {
        //start recording
        fileName = prefGlobal.getSubjectId() + "_" + System.currentTimeMillis() + ".m4a";
        filePath = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath();
        filePath += "/" + fileName;
        onRecord(true);
    }

    @Override
    public void onAudioRecordingDone(long recordingTimeInMils) {
        onRecord(false);
        //onPlay(true);
        showProgress(true);
        NetworkApi.on().storeAudioSample(prefGlobal.getSubjectId(), filePath, fileName, this);
    }

    @Override
    public void onStoreAudioSampleProgress(double progress) {
        Log.d("Uploading audio sample", "Upload is " + progress + "% done");
    }

    @Override
    public void onStoreAudioSampleSuccessfully(@NotNull String downloadLink) {
        Toast.makeText(this, "Sample saved.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStoreAudioSampleReferenceSuccessfully(@NotNull AudioSampleModel resultModel) {
        showProgress(false);

        new Thread(()->{
            audioSampleService.insert(resultModel);
        }).start();
        isRecordingDone = true;
        menuItem.setTitle(getString(R.string.label_done));
        replaceFragment(new BdiSurveyEndFragment());
    }

    @Override
    public void onStoreAudioSampleFailure(@NotNull String error) {
        showProgress(false);

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void gotoRecordingFragment(){
        NewAudioSampleFragment fragment = new NewAudioSampleFragment();
        fragment.setListener(this);
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_new_audio_sample_container, fragment)
                .commit();
    }

    private boolean hasRecordPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                AppConst.REQUEST_CODE_RECORD_AUDIO);

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            String LOG_TAG = "AudioRecordTest";
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void showProgress(boolean show){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Storing data...");
        }

        if(show) {
            if (!progressDialog.isShowing()) progressDialog.show();
        }else{
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}
