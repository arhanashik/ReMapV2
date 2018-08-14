package com.bodytel.remapv2.ui.listdata;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.listdata.ListDataModel;

import java.util.List;

public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ListDataViewHolder>{
    private List<ListDataModel> mDataModels;

    ListDataAdapter(List<ListDataModel> dataModels){
        mDataModels = dataModels;
    }

    @NonNull
    @Override
    public ListDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new ListDataViewHolder(inflater.inflate(R.layout.item_list_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListDataViewHolder holder, int position) {
        holder.bind(mDataModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataModels.size();
    }

    class ListDataViewHolder extends RecyclerView.ViewHolder{
        TextView txtData, txtDate;

        ListDataViewHolder(View itemView) {
            super(itemView);

            txtData = itemView.findViewById(R.id.item_list_data_txt_data);
            txtDate = itemView.findViewById(R.id.item_list_data_txt_date);
        }

        void bind(ListDataModel dataModel){
            txtData.setText(dataModel.getData());
            txtDate.setText(dataModel.getDate());
        }
    }
}
