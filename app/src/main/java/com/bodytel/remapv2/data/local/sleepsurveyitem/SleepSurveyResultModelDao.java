package com.bodytel.remapv2.data.local.sleepsurveyitem;

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
public interface SleepSurveyResultModelDao {
    @Query("SELECT * FROM " + TableNames.TBL_SLEEP_SURVEY)
    LiveData<List<SleepSurveyResultModel>> getAllLive();

    @Query("SELECT * FROM " + TableNames.TBL_SLEEP_SURVEY)
    List<SleepSurveyResultModel> getAll();

    @Query("SELECT * FROM " + TableNames.TBL_SLEEP_SURVEY + " WHERE " + ColumnNames.ID + " = :id")
    SleepSurveyResultModel getSleepSurveyResult(long id);

    @Query("SELECT * FROM " + TableNames.TBL_SLEEP_SURVEY + " WHERE " + ColumnNames.DATA_ID + " = :dataId")
    SleepSurveyResultModel getSleepSurveyResult(String dataId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SleepSurveyResultModel sleepSurveyResultModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SleepSurveyResultModel... sleepSurveyResultModels);

    @Update
    int updateAudioSample(SleepSurveyResultModel sleepSurveyResultModel);

    @Delete
    void delete(SleepSurveyResultModel sleepSurveyResultModel);

    @Query("DELETE FROM " + TableNames.TBL_SLEEP_SURVEY)
    void deleteAll();
}
