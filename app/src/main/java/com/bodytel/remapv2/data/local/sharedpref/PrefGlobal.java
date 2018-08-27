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

    public int getLastSleepSurveyVersion(){
        return mPreferences.getInt(PrefProp.LAST_SLEEP_SURVEY_VERSION, 0);
    }

    public void setLastSleepSurveyVersion(int sleepSurveyVersion){
        mPreferences.edit()
                .putInt(PrefProp.LAST_SLEEP_SURVEY_VERSION, sleepSurveyVersion)
                .apply();
    }

    public int getLastMoodSurveyVersion(){
        return mPreferences.getInt(PrefProp.LAST_MOOD_SURVEY_VERSION, 0);
    }

    public void setLastMoodSurveyVersion(int moodSurveyVersion){
        mPreferences.edit()
                .putInt(PrefProp.LAST_MOOD_SURVEY_VERSION, moodSurveyVersion)
                .apply();
    }

    public void clearAll(){
        mPreferences.edit()
                .clear()
                .apply();
    }
}
