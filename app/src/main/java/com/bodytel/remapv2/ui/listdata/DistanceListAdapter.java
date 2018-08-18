package com.bodytel.remapv2.ui.listdata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.listdata.DistanceModel;
import com.bodytel.remapv2.ui.base.BaseAdapter;
import com.bodytel.remapv2.ui.base.BaseViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DistanceListAdapter extends BaseAdapter<DistanceModel> {
    @Override
    public boolean isEqual(DistanceModel left, DistanceModel right) {
        return false;
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DistanceHolder(inflater.inflate(R.layout.item_list_data, parent, false));
    }

    private class DistanceHolder extends BaseViewHolder<DistanceModel>{
        private TextView distance, dateValue;
        public DistanceHolder(View view) {
            super(view);
            distance = itemView.findViewById(R.id.fit_count);
            dateValue = itemView.findViewById(R.id.fit_time);
        }

        @Override
        public void bind(DistanceModel item) {
            distance.setText(String.valueOf(item.getDistance()/1000));
            String datevalue = getDate(item.getStartDate())+", "+getOnlyTime(item.getStartDate());

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
