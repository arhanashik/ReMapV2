package com.bodytel.remapv2.ui.audiosample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bodytel.remapv2.R;

import java.util.Objects;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class NewAudioSampleFragment extends Fragment {

    private OnNewAudioSampleEvent mEvent;

    private TextView tvTitle, tvProgressLabel;
    private CircularProgressIndicator cpIndicator;
    private Button btnDone;

    private long startTimeMills, stopTimeMills;
    private boolean continueRecording = true;

    public void setListener(OnNewAudioSampleEvent event){
        mEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_audio_sample, container, false);

        tvTitle = view.findViewById(R.id.fragment_new_audio_sample_txt_title);
        tvProgressLabel = view.findViewById(R.id.fragment_new_audio_sample_txt_label_progress);
        cpIndicator = view.findViewById(R.id.fragment_new_audio_sample_circular_progress);
        btnDone = view.findViewById(R.id.fragment_new_audio_sample_btn_done);

        btnDone.setOnClickListener(v -> {
           continueRecording = false;
           stopTimeMills = System.currentTimeMillis();
           mEvent.onAudioRecordingDone(stopTimeMills-startTimeMills);
        });

        cpIndicator.setMaxProgress(3);
        startCountDown();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        continueRecording = false;
    }

    int count = 3;
    Thread t;
    private void startCountDown(){
        cpIndicator.setCurrentProgress(count);

        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (count != -1) {
                        Thread.sleep(1000);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            Log.d("Counter thread", "alive");

                            if(count == 0){
                                tvTitle.setText(getString(R.string.content_new_audio_sample_3));
                                tvProgressLabel.setVisibility(View.GONE);
                                btnDone.setVisibility(View.VISIBLE);
                                mEvent.onRequestNewAudioRecording();
                                startRecordTimeCounter();
                            }

                            count--;
                            cpIndicator.setCurrentProgress(count);
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    int sec = 0, min = 0, progress = 0, maxProgress = 180;
    private void startRecordTimeCounter(){
        startTimeMills = System.currentTimeMillis();

        cpIndicator.setProgress(progress, maxProgress);
        cpIndicator.setProgressTextAdapter(TIME_TEXT_ADAPTER);

        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (continueRecording && progress < maxProgress) {
                        Thread.sleep(1000);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            Log.d("Recorder thread", "alive");

                            cpIndicator.setCurrentProgress(progress);
                            progress++;
                            sec++;

                            if(sec == 60) {
                                sec = 0;
                                min++;
                            }

                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    private CircularProgressIndicator.ProgressTextAdapter TIME_TEXT_ADAPTER = time -> {

        String minStr = "0" + min;
        String secStr = sec + "";
        if(sec < 10) secStr = "0" + secStr;

        return minStr + " : " + secStr;
    };
}
