package com.bodytel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.bodytel.remapv2.data.local.db.AppDatabase;

public class ReMapApp extends Application{
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        AppDatabase.getInstance(getApplicationContext());
    }

    public static Context getContext(){
        return mContext;
    }
}
