package com.bodytel.remapv2.data.local.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.bodytel.remapv2.ui.welcome.WelcomeActivity;

public class BootReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BootReceiver","Broadcast receiver fired");

        Intent i;

        if(Build.VERSION.SDK_INT >= 21){
            i = new Intent(context, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(i);

        }else {
            i = new Intent(context, ReMapService.class);
            i.setAction(ReMapService.START_FOREGROUND_ACTION);
            context.startService(i);
        }
    }
}
