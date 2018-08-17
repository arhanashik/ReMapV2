package com.bodytel.remapv2.ui.listdata;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.listdata.ListDataModel;
import com.bodytel.remapv2.data.local.listdata.StepModel;
import com.bodytel.remapv2.ui.base.BaseAdapter;
import com.bodytel.remapv2.ui.base.BaseViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ListDataAdapter extends BaseAdapter<StepModel>{

    @Override
    public boolean isEqual(StepModel left, StepModel right) {
        return false;
    }
    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ListDataViewHolder(inflater.inflate(R.layout.item_list_data, parent, false));
    }


    class ListDataViewHolder extends BaseViewHolder<StepModel>{
        private TextView stepCount, dateValue;
        ListDataViewHolder(View itemView) {
            super(itemView);
            stepCount = itemView.findViewById(R.id.fit_count);
            dateValue = itemView.findViewById(R.id.fit_time);
        }
        @Override
        public void bind(StepModel item) {
            stepCount.setText(item.getStepCount());

            String datevalue = getDate(item.getEndTate())+", "+getOnlyTime(item.getEndTate());

            dateValue.setText(datevalue);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private String getOnlyTime(long milliSeconds){
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    private String getDate(long milliSeconds) {
        Date date = new Date(milliSeconds);
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

}
