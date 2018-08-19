package com.bodytel.remapv2.ui.audiosample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.listdata.StepModel;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.ui.listdata.ListDataAdapter;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class AudioSampleActivity extends AppCompatActivity implements AudioSampleClickEvent {
    private final String TAG = getClass().getName();
    private RecyclerView mRvAudioSamples;
    private AudioSampleAdapter mAdapter;

    private final int REQUEST_OAUTH_REQUEST_CODE = 1;

    private FirebaseFirestore db;
    private StorageReference storageReference, fileDb;
    private PrefGlobal prefGlobal;

    private final String LOG_TAG = "AudioRecordTest";
    private String fileName = null, filePath = null;
    private File tempFile = null;
    private MediaPlayer mPlayer = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_samples);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        prefGlobal = PrefHelper.providePrefGlobal();

        mRvAudioSamples = findViewById(R.id.activity_audio_samples_recycler_view);

        initView();

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onClickPlayPause(AudioSampleModel sampleModel) {
        fileName = sampleModel.getFileName();
        filePath = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath();
        filePath += "/" + fileName;

        try {
            //File localFile = new File(filePath);
            tempFile = File.createTempFile(fileName.substring(0, fileName.indexOf(".")), "m4a");
            if(tempFile.exists()) onPlay(true);
            else downloadFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mAdapter = new AudioSampleAdapter();
        mAdapter.setListener(this);

        mRvAudioSamples.setHasFixedSize(true);
        mRvAudioSamples.setLayoutManager(new LinearLayoutManager(this));
        mRvAudioSamples.setAdapter(mAdapter);
    }

    private void loadData(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        String subjectId = prefGlobal.getSubjectId();

        List<AudioSampleModel> dataModels = new ArrayList<>();

        //CollectionReference audioSample = db.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA);
        //Query query = audioSample.whereEqualTo(AppConst.SUBJECT_ID, prefGlobal.getSubjectId());

        db.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()){
                            Map<String, Object> data = document.getData();

                            if(data.get(AppConst.SUBJECT_ID).equals(subjectId)){
                                AudioSampleModel sampleModel = new AudioSampleModel();

                                sampleModel.setId(document.getId());
                                sampleModel.setFileName(String.valueOf(data.get(AppConst.FILE_NAME)));
                                sampleModel.setCreatedAt(Long.valueOf(String.valueOf(data.get(AppConst.CREATED_AT))));
                                sampleModel.setDownloadUrl(String.valueOf(data.get(AppConst.DOWNLOAD_URL)));

                                dataModels.add(sampleModel);
                            }
                        }

                        mAdapter.addItems(dataModels);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);

    }

    private void downloadFile(String fileName) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading data...");
        progressDialog.show();

        fileDb = storageReference.child("audio/" + fileName);

        fileDb.getFile(tempFile).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            onPlay(true);
        }).addOnFailureListener(exception -> {
            progressDialog.dismiss();
            exception.printStackTrace();
            Toast.makeText(AudioSampleActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

//    private void initFitnessApiAndCheckPermission() {
//        FitnessOptions options = FitnessOptions.builder()
//                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
//                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
//                .build();
//
//        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), options)) {
//            GoogleSignIn.requestPermissions(this, REQUEST_OAUTH_REQUEST_CODE,
//                    GoogleSignIn.getLastSignedInAccount(this), options);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
//
//            }
//        }
//    }
}
