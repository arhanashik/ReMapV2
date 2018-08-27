package com.bodytel.remapv2.data.local.bdisurveyitem;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

import java.util.List;

@Entity(tableName = TableNames.TBL_BDI_SURVEY)
public class BdiSurveyResultModel {
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
    private String surveyVersion;
    @ColumnInfo(name = ColumnNames.ANSWERS)
    private List<Integer> answers;
    @ColumnInfo(name = ColumnNames.IS_SYNC)
    private boolean isSync;

    @Ignore
    public BdiSurveyResultModel() {
    }

    public BdiSurveyResultModel(long id, String dataId, String subjectId, Long createdAt, String surveyVersion, List<Integer> answers, boolean isSync) {
        this.id = id;
        this.dataId = dataId;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
        this.surveyVersion = surveyVersion;
        this.answers = answers;
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

    public void setDataId(String id) {
        this.dataId = id;
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

    public String getSurveyVersion() {
        return surveyVersion;
    }

    public void setSurveyVersion(String surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
