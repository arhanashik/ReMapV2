package com.bodytel.remapv2.data.local.sleepsurveyitem;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class SleepSurveyResultService {
    private final SleepSurveyResultModelDao mSleepSurveyResultModelDao;

    public SleepSurveyResultService(SleepSurveyResultModelDao sleepSurveyResultModelDao) {
        mSleepSurveyResultModelDao = sleepSurveyResultModelDao;
    }

    public SleepSurveyResultModel getSleepSurveyResultModel(long id) {
        return mSleepSurveyResultModelDao.getSleepSurveyResult(id);
    }

    public SleepSurveyResultModel getSleepSurveyResultModel(String dataId) {
        return mSleepSurveyResultModelDao.getSleepSurveyResult(dataId);
    }

    public long insert(SleepSurveyResultModel sleepSurveyResultModel) {
        long id = mSleepSurveyResultModelDao.insert(sleepSurveyResultModel);
        sleepSurveyResultModel.setId(id);
        return id;
    }

    public int updateSleepSurveyResultModel(SleepSurveyResultModel sleepSurveyResultModel) {
        return mSleepSurveyResultModelDao.updateAudioSample(sleepSurveyResultModel);
    }

    public void deleteAllResults() {
        mSleepSurveyResultModelDao.deleteAll();
    }

    public void deleteSleepSurveyResult(SleepSurveyResultModel sleepSurveyResultModel) {
        mSleepSurveyResultModelDao.delete(sleepSurveyResultModel);
    }

    public List<SleepSurveyResultModel> getAllSleepSurveyResultModel() {
        return mSleepSurveyResultModelDao.getAll();
    }

    public LiveData<List<SleepSurveyResultModel>> getAllSleepSurveyResultModelLiveData() {
        return mSleepSurveyResultModelDao.getAllLive();
    }
}
