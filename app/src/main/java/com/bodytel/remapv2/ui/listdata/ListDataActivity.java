package com.bodytel.remapv2.ui.listdata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.listdata.ListDataModel;

import java.util.ArrayList;
import java.util.List;

public class ListDataActivity extends AppCompatActivity {

    private RecyclerView mRvListData;
    private ListDataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRvListData = findViewById(R.id.activity_list_data_recycler_view);

        int listDataType = getIntent().getIntExtra(AppConst.LIST_DATA_TYPE, 0);

        initView(listDataType);
    }

    private void initView(int listDataType) {
        List<ListDataModel> dataModels = new ArrayList<>();

        if(listDataType == AppConst.TYPE_STEPS){
            dataModels.add(new ListDataModel(1, "8.0", "01.08.18, 08:38"));
            dataModels.add(new ListDataModel(2, "13.0", "01.08.18, 08:36"));
            dataModels.add(new ListDataModel(3, "40.0", "31.08.18, 23:12"));
            dataModels.add(new ListDataModel(4, "28.0", "31.08.18, 22:59"));
            dataModels.add(new ListDataModel(5, "18.0", "31.08.18, 22:01"));
            dataModels.add(new ListDataModel(6, "37.0", "31.08.18, 19:28"));
            dataModels.add(new ListDataModel(7, "21.0", "31.08.18, 18:48"));
            dataModels.add(new ListDataModel(8, "20.0", "31.08.18, 18:31"));
            dataModels.add(new ListDataModel(9, "39.0", "31.08.18, 18:25"));
            dataModels.add(new ListDataModel(10, "42.0", "31.08.18, 18:19"));
        }

        mAdapter = new ListDataAdapter(dataModels);
        mRvListData.setHasFixedSize(true);
        mRvListData.setLayoutManager(new LinearLayoutManager(this));
        mRvListData.setAdapter(mAdapter);
    }

}
