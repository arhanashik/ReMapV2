package com.bodytel.remapv2.data.local.fitdata;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

import java.util.List;

@Dao
public interface FitDataModelDao {
    @Query("SELECT * FROM " + TableNames.TBL_FIT_DATA)
    LiveData<List<FitDataModel>> getAllLive();

    @Query("SELECT * FROM " + TableNames.TBL_FIT_DATA)
    List<FitDataModel> getAll();

    @Query("SELECT * FROM " + TableNames.TBL_FIT_DATA + " WHERE " + ColumnNames.TYPE + " = :type")
    List<FitDataModel> getDataListOfType(String type);

    @Query("SELECT * FROM " + TableNames.TBL_FIT_DATA + " WHERE " + ColumnNames.ID + " = :id")
    FitDataModel getFitDataModel(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FitDataModel fitDataModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(FitDataModel... fitDataModels);

    @Update
    int updateFitData(FitDataModel fitDataModel);

    @Delete
    void delete(FitDataModel fitDataModel);

    @Query("DELETE FROM " + TableNames.TBL_FIT_DATA)
    void deleteAll();

    @Query("DELETE FROM " + TableNames.TBL_FIT_DATA + " WHERE " + ColumnNames.TYPE + " = :type")
    void deleteDataOfType(String type);
}
