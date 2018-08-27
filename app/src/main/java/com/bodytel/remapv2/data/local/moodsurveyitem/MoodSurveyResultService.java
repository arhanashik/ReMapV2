package com.bodytel.remapv2.data.local.moodsurveyitem;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class MoodSurveyResultService {
    private final MoodSurveyResultModelDao mMoodSurveyResultModelDao;

    public MoodSurveyResultService(MoodSurveyResultModelDao moodSurveyResultModelDao) {
        mMoodSurveyResultModelDao = moodSurveyResultModelDao;
    }

    public MoodSurveyResultModel getMoodSurveyResultModel(long id) {
        return mMoodSurveyResultModelDao.getMoodSurveyResult(id);
    }

    public MoodSurveyResultModel getMoodSurveyResultModel(String dataId) {
        return mMoodSurveyResultModelDao.getMoodSurveyResult(dataId);
    }

    public long insert(MoodSurveyResultModel moodSurveyResultModel) {
        long id = mMoodSurveyResultModelDao.insert(moodSurveyResultModel);
        moodSurveyResultModel.setId(id);
        return id;
    }

    public int updateMoodSurveyResultModel(MoodSurveyResultModel moodSurveyResultModel) {
        return mMoodSurveyResultModelDao.updateAudioSample(moodSurveyResultModel);
    }

    public void deleteAllResults() {
        mMoodSurveyResultModelDao.deleteAll();
    }

    public void deleteMoodSurveyResult(MoodSurveyResultModel moodSurveyResultModel) {
        mMoodSurveyResultModelDao.delete(moodSurveyResultModel);
    }

    public List<MoodSurveyResultModel> getAllMoodSurveyResultModel() {
        return mMoodSurveyResultModelDao.getAll();
    }

    public LiveData<List<MoodSurveyResultModel>> getAllMoodSurveyResultModelLiveData() {
        return mMoodSurveyResultModelDao.getAllLive();
    }
}
