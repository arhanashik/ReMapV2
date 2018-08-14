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
        mEditor = mPreferences.edit();
        mEditor.putString(PrefProp.SUBJECT_ID, subjectId);
        mEditor.apply();
    }

    public void clearAll(){
        mEditor = mPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }
}
