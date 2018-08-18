package com.bodytel.remapv2.ui.listdata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.listdata.SensorModel;
import com.bodytel.remapv2.ui.base.BaseAdapter;
import com.bodytel.remapv2.ui.base.BaseViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SensorDataAdapter extends BaseAdapter<SensorModel> {
    @Override
    public boolean isEqual(SensorModel left, SensorModel right) {
        return false;
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DataHolder(inflater.inflate(R.layout.item_list_data, parent, false));
    }

    private class DataHolder extends BaseViewHolder<SensorModel>{
        private TextView timeStampTv, sensorDataTv;
        public DataHolder(View view) {
            super(view);
            timeStampTv = itemView.findViewById(R.id.fit_count);
            sensorDataTv = itemView.findViewById(R.id.fit_time);
        }

        @Override
        public void bind(SensorModel item) {
            timeStampTv.setText(""+item.getTimeStemp());
            sensorDataTv.setText(item.getValue());
        }

        @Override
        public void onClick(View v) {

        }



    }
}
