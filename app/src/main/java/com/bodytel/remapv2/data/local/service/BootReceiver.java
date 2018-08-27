package com.bodytel.remapv2.data.local.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BootReceiver","Broadcast receiver fired");

        Intent i = new Intent(context, ReMapService.class);
        i.setAction(ReMapService.START_FOREGROUND_ACTION);
        context.startService(i);
    }
}
