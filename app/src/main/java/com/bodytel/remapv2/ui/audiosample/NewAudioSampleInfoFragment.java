package com.bodytel.remapv2.ui.audiosample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bodytel.remapv2.R;

public class NewAudioSampleInfoFragment extends Fragment {

    private OnNewAudioSampleEvent mEvent;
    private boolean isInfoShowDone = false;

    public void setListener(OnNewAudioSampleEvent event){
        mEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_audio_sample_info, container, false);

        final TextView txtDescription = view.findViewById(R.id.fragment_new_audio_sample_info_txt_description);
        final Button btnNext = view.findViewById(R.id.fragment_new_audio_sample_info_btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInfoShowDone) mEvent.onPrepareNewAudioRecording();
                else {
                    txtDescription.setText(getString(R.string.content_new_audio_sample_2));
                    btnNext.setText(getString(R.string.label_get_started));
                    isInfoShowDone = true;
                }
            }
        });

        return view;
    }
}
