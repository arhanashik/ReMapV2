package com.bodytel.remapv2.data.local.dbstorage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModelDao;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModelDao;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModelDao;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModelDao;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModelDao;

@Database(entities = {
        AudioSampleModel.class,
        BdiSurveyResultModel.class,
        AccelerometerDataModel.class,
        SleepSurveyResultModel.class,
        MoodSurveyResultModel.class
        },
        version = 1, exportSchema = false)
public abstract class DatabaseService extends RoomDatabase {
    private static volatile DatabaseService sInstance;

    // Get a database instance
    public static synchronized DatabaseService on() {
        return sInstance;
    }

    public static synchronized void init(Context context) {

        if (sInstance == null) {
            synchronized (DatabaseService.class) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        DatabaseService.class, context.getString(R.string.db_name)).build();
            }
        }

    }

    public abstract AudioSampleModelDao audioSampleModelDao();

    public abstract BdiSurveyResultModelDao bdiSurveyResultModelDao();

    public abstract AccelerometerDataModelDao accelerometerDataModelDao();

    public abstract SleepSurveyResultModelDao sleepSurveyResultModelDao();

    public abstract MoodSurveyResultModelDao moodSurveyResultModelDao();
}