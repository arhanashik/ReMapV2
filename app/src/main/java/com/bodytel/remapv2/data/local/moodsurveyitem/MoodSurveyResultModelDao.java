package com.bodytel.remapv2.data.local.moodsurveyitem;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

import java.util.List;

@Dao
public interface MoodSurveyResultModelDao {
    @Query("SELECT * FROM " + TableNames.TBL_MOOD_SURVEY)
    LiveData<List<MoodSurveyResultModel>> getAllLive();

    @Query("SELECT * FROM " + TableNames.TBL_MOOD_SURVEY)
    List<MoodSurveyResultModel> getAll();

    @Query("SELECT * FROM " + TableNames.TBL_MOOD_SURVEY + " WHERE " + ColumnNames.ID + " = :id")
    MoodSurveyResultModel getMoodSurveyResult(long id);

    @Query("SELECT * FROM " + TableNames.TBL_MOOD_SURVEY + " WHERE " + ColumnNames.DATA_ID + " = :dataId")
    MoodSurveyResultModel getMoodSurveyResult(String dataId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MoodSurveyResultModel moodSurveyResultModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MoodSurveyResultModel... moodSurveyResultModels);

    @Update
    int updateAudioSample(MoodSurveyResultModel moodSurveyResultModel);

    @Delete
    void delete(MoodSurveyResultModel moodSurveyResultModel);

    @Query("DELETE FROM " + TableNames.TBL_MOOD_SURVEY)
    void deleteAll();
}
