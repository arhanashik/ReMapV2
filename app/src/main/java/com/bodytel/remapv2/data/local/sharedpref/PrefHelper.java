package com.bodytel.remapv2.data.local.sharedpref;

import com.bodytel.ReMapApp;

public class PrefHelper {
    public static PrefGlobal providePrefGlobal(){
        return new PrefGlobal(ReMapApp.getContext());
    }
}
