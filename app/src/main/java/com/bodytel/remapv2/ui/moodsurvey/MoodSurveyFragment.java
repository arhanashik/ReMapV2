package com.bodytel.remapv2.ui.moodsurvey;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bodytel.remapv2.R;

public class MoodSurveyFragment extends Fragment {

    private TextView selectedMoodValue;

    private OnMoodSurveyEvent mEvent;
    private int mood = -1;

    public void setMoodSurveyListener(OnMoodSurveyEvent event){
        mEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_survey, container, false);

        SeekBar seekBarMoodPicker = view.findViewById(R.id.fragment_mood_survey_sb_mood_selector);
        selectedMoodValue = view.findViewById(R.id.fragment_mood_survey_txt_selected_mood);

        seekBarMoodPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mood = progress;
                selectedMoodValue.setText(String.valueOf(mood));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        view.findViewById(R.id.fragment_mood_survey_btn_next).setOnClickListener(v -> {
            if(mood == -1) Toast.makeText(getContext(), "Please pick a answer first", Toast.LENGTH_SHORT).show();
            else {
                mEvent.onMoodSurveyAnswer(mood);
            }
        });

        return view;
    }
}
