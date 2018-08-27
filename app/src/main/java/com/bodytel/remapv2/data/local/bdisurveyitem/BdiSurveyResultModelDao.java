package com.bodytel.remapv2.data.local.bdisurveyitem;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

import java.util.List;

@Dao
public interface BdiSurveyResultModelDao {
    @Query("SELECT * FROM " + TableNames.TBL_BDI_SURVEY)
    LiveData<List<BdiSurveyResultModel>> getAllLive();

    @Query("SELECT * FROM " + TableNames.TBL_BDI_SURVEY)
    List<BdiSurveyResultModel> getAll();

    @Query("SELECT * FROM " + TableNames.TBL_BDI_SURVEY + " WHERE " + ColumnNames.ID + " = :id")
    BdiSurveyResultModel getBdiSurveyResult(long id);

    @Query("SELECT * FROM " + TableNames.TBL_BDI_SURVEY + " WHERE " + ColumnNames.DATA_ID + " = :dataId")
    BdiSurveyResultModel getBdiSurveyResult(String dataId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BdiSurveyResultModel bdiSurveyResultModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(BdiSurveyResultModel... bdiSurveyResultModels);

    @Update
    int updateAudioSample(BdiSurveyResultModel bdiSurveyResultModel);

    @Delete
    void delete(BdiSurveyResultModel bdiSurveyResultModel);

    @Query("DELETE FROM " + TableNames.TBL_BDI_SURVEY)
    void deleteAll();
}
