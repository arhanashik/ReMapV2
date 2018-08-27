package com.bodytel.remapv2.data.local.accelerometerdatamodel;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;

import java.util.List;

@Dao
public interface AccelerometerDataModelDao {
    @Query("SELECT * FROM " + TableNames.TBL_ACCELEROMETER_DATA)
    LiveData<List<AccelerometerDataModel>> getAllLive();

    @Query("SELECT * FROM " + TableNames.TBL_ACCELEROMETER_DATA)
    List<AccelerometerDataModel> getAll();

    @Query("SELECT * FROM " + TableNames.TBL_ACCELEROMETER_DATA + " WHERE " + ColumnNames.ID + " = :id")
    AccelerometerDataModel getAccelerometerDataModel(long id);

    @Query("SELECT * FROM " + TableNames.TBL_ACCELEROMETER_DATA + " WHERE " + ColumnNames.DATA_ID + " = :dataId")
    AccelerometerDataModel getAccelerometerDataModel(String dataId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AccelerometerDataModel accelerometerDataModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(AccelerometerDataModel... accelerometerDataModels);

    @Update
    int updateAcceleroMeterDataModel(AccelerometerDataModel accelerometerDataModels);

    @Delete
    void delete(AccelerometerDataModel accelerometerDataModels);

    @Query("DELETE FROM " + TableNames.TBL_ACCELEROMETER_DATA)
    void deleteAll();
}
