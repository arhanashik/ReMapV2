package com.bodytel.remapv2.ui.audiosample;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.util.lib.network.NetworkApi;
import com.bodytel.util.lib.network.callback.DownloadAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.GetAudioSampleCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AudioSampleActivity extends AppCompatActivity implements AudioSampleClickEvent,
        GetAudioSampleCallBack, DownloadAudioSampleCallBack {
    private RecyclerView mRvAudioSamples;
    private AudioSampleAdapter mAdapter;

    private final int REQUEST_OAUTH_REQUEST_CODE = 1;

    private PrefGlobal prefGlobal;

    private String filePath = null;
    private MediaPlayer mPlayer = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_samples);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        prefGlobal = PrefHelper.providePrefGlobal();

        mRvAudioSamples = findViewById(R.id.activity_audio_samples_recycler_view);

        initView();

        showProgress(true);
        NetworkApi.on().getAudioSample(prefGlobal.getSubjectId(), this);
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
        showProgress(true);
        NetworkApi.on().downloadAudioSample(sampleModel.getDownloadUrl(), sampleModel.getFileName(), this);
    }

    @Override
    public void onLoadAudioSampleSuccessfully(@NotNull List<? extends AudioSampleModel> sampleModelList) {
        for(AudioSampleModel sampleModel : sampleModelList){
            mAdapter.addItem(sampleModel);
        }

        showProgress(false);
    }

    @Override
    public void onLoadAudioSampleFailure(@NotNull String error) {
        showProgress(false);

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadAudioSampleSuccessfully(@NotNull String filePath, @NotNull String message) {
        showProgress(false);

        this.filePath = filePath;
        onPlay(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadAudioSampleFailed(@NotNull String error) {
        showProgress(false);

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mAdapter = new AudioSampleAdapter();
        mAdapter.setListener(this);

        mRvAudioSamples.setHasFixedSize(true);
        mRvAudioSamples.setLayoutManager(new LinearLayoutManager(this));
        mRvAudioSamples.setAdapter(mAdapter);
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
            String LOG_TAG = "AudioRecordTest";
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void showProgress(boolean show){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading data...");
        }

        if(show) {
            if (!progressDialog.isShowing()) progressDialog.show();
        }else{
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}
