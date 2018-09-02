package com.bodytel.remapv2.ui.sleepsurvey;

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

public class SleepSurveyFragment extends Fragment {

    //private RecyclerView rvNumPicker;
    private TextView tvSelectedSleep;

    private OnSleepSurveyEvent mEvent;
    private int hourOfSleep = -1;

    public void setSleepSurveyListener(OnSleepSurveyEvent event){
        mEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_survey, container, false);

        //rvNumPicker = view.findViewById(R.id.fragment_sleep_survey_rv_num_picker);
        SeekBar seekBarSleepSelector = view.findViewById(R.id.fragment_sleep_survey_sb_sleep_selector);
        tvSelectedSleep = view.findViewById(R.id.fragment_sleep_survey_txt_selected_sleep);

        seekBarSleepSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hourOfSleep = progress;
                tvSelectedSleep.setText(String.valueOf(hourOfSleep));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        view.findViewById(R.id.fragment_sleep_survey_btn_next).setOnClickListener(v -> {
            if(hourOfSleep == -1) Toast.makeText(getContext(), "Please pick a answer first", Toast.LENGTH_SHORT).show();
            else {
                mEvent.onSleepSurveyAnswer(hourOfSleep);
            }
        });

        //initView();

        return view;
    }

    /*boolean defaultScroll = true;
    private void initView(){
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(getContext(), PickerLayoutManager.HORIZONTAL, false);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(0.8f);

        PickerAdapter pickerAdapter = new PickerAdapter(getContext(), getData(13), rvNumPicker);


        rvNumPicker.setLayoutManager(pickerLayoutManager);
        rvNumPicker.setAdapter(pickerAdapter);
        rvNumPicker.scrollToPosition(7);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvNumPicker);

        pickerLayoutManager.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
            @Override
            public void selectedView(View view) {
                if(defaultScroll) {
                    defaultScroll = false;
                    return;
                }

                hourOfSleep = Integer.valueOf(((TextView) view).getText().toString());
            }
        });
    }

    public List<String> getData(int count) {
        List<String> data = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            data.add(String.valueOf(i));
        }
        return data;
    }*/
}
