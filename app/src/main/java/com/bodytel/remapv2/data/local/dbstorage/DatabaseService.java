package com.bodytel.remapv2.data.local.dbstorage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModelDao;

@Database(entities = {
        AudioSampleModel.class
        },
        version = 1, exportSchema = false)
public abstract class DatabaseService extends RoomDatabase {
    private static volatile DatabaseService sInstance;

    // Get a database instance
    public static synchronized DatabaseService on() {
        return sInstance;
    }

    public static synchronized DatabaseService init(Context context) {

        if (sInstance == null) {
            synchronized (DatabaseService.class) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        DatabaseService.class, context.getString(R.string.db_name)).build();
            }
        }

        return sInstance;
    }

    public abstract AudioSampleModelDao audioSampleModelDao();
}