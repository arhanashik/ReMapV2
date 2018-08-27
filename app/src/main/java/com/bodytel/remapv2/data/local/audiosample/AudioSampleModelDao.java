package com.bodytel.remapv2.data.local.audiosample;

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
public interface AudioSampleModelDao {
    @Query("SELECT * FROM " + TableNames.TBL_AUDIO_SAMPLE)
    LiveData<List<AudioSampleModel>> getAllLive();

    @Query("SELECT * FROM " + TableNames.TBL_AUDIO_SAMPLE)
    List<AudioSampleModel> getAll();

    @Query("SELECT * FROM " + TableNames.TBL_AUDIO_SAMPLE + " WHERE " + ColumnNames.ID + " = :id")
    AudioSampleModel getAudioSample(long id);

    @Query("SELECT * FROM " + TableNames.TBL_AUDIO_SAMPLE + " WHERE " + ColumnNames.DATA_ID + " = :dataId")
    AudioSampleModel getAudioSample(String dataId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AudioSampleModel audioSampleModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(AudioSampleModel...audioSampleModels);

    @Update
    int updateAudioSample(AudioSampleModel audioSampleModel);

    @Delete
    void delete(AudioSampleModel audioSampleModel);

    @Query("DELETE FROM " + TableNames.TBL_AUDIO_SAMPLE)
    void deleteAll();
}
