package com.bodytel.remapv2.ui.sleepsurvey;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.util.lib.horizontalpickerlib.PickerLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class SleepSurveyFragment extends Fragment {

    private RecyclerView rvNumPicker;

    private OnSleepSurveyEvent mEvent;
    private int hourOfSleep = -1;

    public void setSleepSurveyListener(OnSleepSurveyEvent event){
        mEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_survey, container, false);

        rvNumPicker = view.findViewById(R.id.fragment_sleep_survey_rv_num_picker);

        view.findViewById(R.id.fragment_sleep_survey_btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hourOfSleep == -1) Toast.makeText(getContext(), "Please pick a answer first", Toast.LENGTH_SHORT).show();
                else {
                    mEvent.onSleepSurveyAnswer(hourOfSleep);
                }
            }
        });

        initView();

        return view;
    }

    boolean defaultScroll = true;
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
    }
}
