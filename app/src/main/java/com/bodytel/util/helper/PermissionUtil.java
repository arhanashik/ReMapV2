package com.bodytel.util.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    private static PermissionUtil invokePermission;
    public static final int PERMISSIONS_REQUEST= 1;

    public static synchronized PermissionUtil getInstance() {
        if (invokePermission == null) {
            invokePermission = new PermissionUtil();
        }
        return invokePermission;
    }

    public boolean isPermitted(Context context, String... args) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> finalArgs = new ArrayList<>();
        for (String arg : args) {
            if(context.checkSelfPermission(arg) != PackageManager.PERMISSION_GRANTED) {
                finalArgs.add(arg);
            }
        }

        if (finalArgs.isEmpty()) {
            return true;
        }

        ((Activity) context).requestPermissions(finalArgs.toArray(new String[finalArgs.size()]),
                PERMISSIONS_REQUEST);

        return false;
    }
}