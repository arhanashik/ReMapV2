package com.bodytel.remapv2.data.local.audiosample;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

@Entity(tableName = TableNames.TBL_AUDIO_SAMPLE)
public class AudioSampleModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ColumnNames.ID)
    private long id;
    @ColumnInfo(name = ColumnNames.DATA_ID)
    private String dataId;
    @ColumnInfo(name = ColumnNames.SUBJECT_ID)
    private String subjectId;
    @ColumnInfo(name = ColumnNames.CREATED_AT)
    private long createdAt;
    @ColumnInfo(name = ColumnNames.FILE_NAME)
    private String fileName;
    @ColumnInfo(name = ColumnNames.DOWNLOAD_URL)
    private String downloadUrl;

    @Ignore
    public AudioSampleModel() {
    }

    public AudioSampleModel(long id, String dataId, String subjectId, long createdAt, String fileName, String downloadUrl) {
        this.id = id;
        this.dataId = dataId;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
