package com.bodytel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.bodytel.remapv2.data.local.db.AppDatabase;

import com.bodytel.remapv2.data.local.dbstorage.DatabaseService;
import com.bodytel.util.helper.ScheduledJobHelper;
import com.bodytel.util.lib.network.firebase.FirebaseUtil;

public class ReMapApp extends Application{
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        AppDatabase.getInstance(getApplicationContext());

        DatabaseService.init(mContext);

        ScheduledJobHelper.on().init();

        //FirebaseUtil.on().init();
    }

    public static Context getContext(){
        return mContext;
    }
}
