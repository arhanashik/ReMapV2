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

import java.util.List;

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
        private TextView txtData, txtDate;
        ListDataViewHolder(View itemView) {
            super(itemView);
            txtData = itemView.findViewById(R.id.item_list_data_txt_data);
            txtDate = itemView.findViewById(R.id.item_list_data_txt_date);
        }
        @Override
        public void bind(StepModel item) {
            txtData.setText(item.getStartData());
            txtDate.setText(item.getEndTate());
        }

        @Override
        public void onClick(View v) {

        }
    }
}
