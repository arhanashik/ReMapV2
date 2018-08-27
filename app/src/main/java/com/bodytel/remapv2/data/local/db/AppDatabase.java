package com.bodytel.remapv2.data.local.db;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 8/20/2018 at 2:39 PM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 8/20/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.bodytel.remapv2.data.local.listdata.DistanceModel;
import com.bodytel.remapv2.data.local.listdata.StepModel;

@Database(entities = {StepModel.class, DistanceModel.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StepsDao getStepDao();
    public abstract DistanceDao getDistanceDao();

    private static AppDatabase sInstance;

    public static AppDatabase on(){
        return sInstance;
    }

    @VisibleForTesting
    public static final String DATABASE_NAME = "basic-sample-db";

    public static AppDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (AppDatabase.class){
                sInstance = buildDatabase(context);
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context context){
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
    }
}
