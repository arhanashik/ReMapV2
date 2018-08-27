package com.bodytel.remapv2.ui.audiosample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.ui.base.BaseAdapter;
import com.bodytel.remapv2.ui.base.BaseViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AudioSampleAdapter extends BaseAdapter<AudioSampleModel>{

    private AudioSampleClickEvent mListener;

    public void setListener(AudioSampleClickEvent listener) {
        this.mListener = listener;
    }

    @Override
    public boolean isEqual(AudioSampleModel left, AudioSampleModel right) {
        return left.getFileName().equals(right.getFileName());
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AudioSampleViewHolder(inflater.inflate(R.layout.item_audio_sample, parent, false));
    }

    class AudioSampleViewHolder extends BaseViewHolder<AudioSampleModel>{
        private TextView txtTitle, txtDate;
        private ImageView btnPlay;

        private AudioSampleModel mItem;

        AudioSampleViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_audio_sample_txt_title);
            txtDate = itemView.findViewById(R.id.item_audio_sample_txt_date);
            btnPlay = itemView.findViewById(R.id.item_audio_sample_btn_play);

            setClickListener(btnPlay);
        }

        @Override
        public void bind(AudioSampleModel item) {
            mItem = item;

            txtTitle.setText(item.getFileName());
            String datevalue = getDate(item.getCreatedAt())+", "+getOnlyTime(item.getCreatedAt());
            txtDate.setText(datevalue);
        }

        @Override
        public void onClick(View v) {
            if(v == btnPlay){
                if(mListener != null) mListener.onClickPlayPause(mItem);
            }
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
