package com.bodytel.remapv2.data.local.bdisurveyitem;

import java.util.List;

public class BdiSurveyResultModel {
    private int id;
    private String dataId;
    private String subjectId;
    private Long createdAt;
    private String surveyVersion;
    private List<Integer> answers;
    private boolean isSync;

    public BdiSurveyResultModel() {
    }

    public BdiSurveyResultModel(int id, String dataId, String subjectId, Long createdAt, String surveyVersion, List<Integer> answers, boolean isSync) {
        this.id = id;
        this.dataId = dataId;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
        this.surveyVersion = surveyVersion;
        this.answers = answers;
        this.isSync = isSync;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
