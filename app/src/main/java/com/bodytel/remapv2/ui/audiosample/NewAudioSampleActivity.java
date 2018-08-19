package com.bodytel.remapv2.ui.audiosample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
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
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyEndFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class NewAudioSampleActivity extends AppCompatActivity implements OnNewAudioSampleEvent{

    private boolean isRecordingDone;
    private MenuItem menuItem;

    private FirebaseFirestore db;
    private StorageReference storageReference, fileDb;
    private PrefGlobal prefGlobal;
    private Uri fileUri = null;

    private ProgressDialog progressDialog;

    private final String LOG_TAG = "AudioRecordTest";
    private String fileName = null, filePath = null;
    private MediaRecorder mRecorder = null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_audio_sample);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        prefGlobal = PrefHelper.providePrefGlobal();

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
        storeNewAudioSample(filePath, fileName);
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

    private void storeNewAudioSample(String filePath, String fileName){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while storing audio...");
        progressDialog.show();

        fileUri = Uri.fromFile(new File(filePath));
        fileDb = storageReference.child("audio/" + fileName);

        fileDb.putFile(fileUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(LOG_TAG, "Upload is " + progress + "% done");
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.d(LOG_TAG, e.getMessage());
                    Toast.makeText(NewAudioSampleActivity.this, e.getMessage() + ". Please try again.",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    storeAudioSampleReference(fileName, fileDb.getDownloadUrl().toString());
                });
    }

    private void storeAudioSampleReference(String fileName, String reference){
        progressDialog.setMessage("Storing reference...");
        progressDialog.show();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(AppConst.SUBJECT_ID, prefGlobal.getSubjectId());
        data.put(AppConst.CREATED_AT, System.currentTimeMillis());
        data.put(AppConst.FILE_NAME, fileName);
        data.put(AppConst.DOWNLOAD_URL, reference);

        db.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA)
                .add(data)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    menuItem.setTitle(getString(R.string.label_done));
                    isRecordingDone = true;
                    replaceFragment(new BdiSurveyEndFragment());
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();

                    Toast.makeText(NewAudioSampleActivity.this, e.getMessage() + ". Please try again.",
                            Toast.LENGTH_SHORT).show();
                });
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

}
