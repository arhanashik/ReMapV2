package com.bodytel.remapv2.data.local.sleepsurveyitem;

public class SleepSurveyResultModel {
    private int id;
    private String dataId;
    private String subjectId;
    private Long createdAt;
    private int surveyVersion;
    private int answer;
    private boolean isSync;

    public SleepSurveyResultModel(int id, String dataId, String subjectId, Long createdAt, int surveyVersion, int answer, boolean isSync) {
        this.id = id;
        this.dataId = dataId;
        this.subjectId = subjectId;
        this.createdAt = createdAt;
        this.surveyVersion = surveyVersion;
        this.answer = answer;
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
