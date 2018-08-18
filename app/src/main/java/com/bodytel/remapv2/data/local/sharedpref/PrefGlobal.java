package com.bodytel.remapv2.data.local.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefGlobal {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    PrefGlobal(Context context){
        mPreferences = context.getSharedPreferences(PrefProp.PREF_GLOBAL, Context.MODE_PRIVATE);
    }

    public String getSubjectId(){
        return mPreferences.getString(PrefProp.SUBJECT_ID, "");
    }

    public void setSubjectId(String subjectId){
        mPreferences.edit()
                .putString(PrefProp.SUBJECT_ID, subjectId)
                .apply();
    }

    public String getBdiVersion(){
        return mPreferences.getString(PrefProp.BDI_VERSION, "");
    }

    public void setBdiVersion(String bdiVersion){
        mPreferences.edit()
                .putString(PrefProp.BDI_VERSION, bdiVersion)
                .apply();
    }

    public void clearAll(){
        mPreferences.edit()
                .clear()
                .apply();
    }
}
