package com.bodytel.remapv2.data.local.sleepsurveyitem;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

@Entity(tableName = TableNames.TBL_SLEEP_SURVEY)
public class SleepSurveyResultModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ColumnNames.ID)
    private long id;
    @ColumnInfo(name = ColumnNames.DATA_ID)
    private String dataId;
    @ColumnInfo(name = ColumnNames.SUBJECT_ID)
    private String subjectId;
    @ColumnInfo(name = ColumnNames.CREATED_AT)
    private Long createdAt;
    @ColumnInfo(name = ColumnNames.SURVEY_VERSION)
    private int surveyVersion;
    @ColumnInfo(name = ColumnNames.ANSWER)
    private int answer;
    @ColumnInfo(name = ColumnNames.IS_SYNC)
    private boolean isSync;

    public SleepSurveyResultModel(long id, String dataId, String subjectId, Long createdAt, int surveyVersion, int answer, boolean isSync) {
        this.id = id;
        this.dataId = dataId;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
        this.surveyVersion = surveyVersion;
        this.answer = answer;
        this.isSync = isSync;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public int getSurveyVersion() {
        return surveyVersion;
    }

    public void setSurveyVersion(int surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
