package com.bodytel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class ReMapApp extends Application{
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
