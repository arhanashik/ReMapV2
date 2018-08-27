package com.bodytel.remapv2.data.local.bdisurveyitem;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class BdiSurveyResultService {
    private final BdiSurveyResultModelDao mBdiSurveyResultModelDao;

    public BdiSurveyResultService(BdiSurveyResultModelDao bdiSurveyResultModelDao) {
        mBdiSurveyResultModelDao = bdiSurveyResultModelDao;
    }

    public BdiSurveyResultModel getBdiSurveyResultModel(long id) {
        return mBdiSurveyResultModelDao.getBdiSurveyResult(id);
    }

    public BdiSurveyResultModel getBdiSurveyResultModel(String dataId) {
        return mBdiSurveyResultModelDao.getBdiSurveyResult(dataId);
    }

    public long insert(BdiSurveyResultModel bdiSurveyResultModel) {
        long id = mBdiSurveyResultModelDao.insert(bdiSurveyResultModel);
        bdiSurveyResultModel.setId(id);
        return id;
    }

    public int updateBdiSurveyResultModel(BdiSurveyResultModel bdiSurveyResultModel) {
        return mBdiSurveyResultModelDao.updateAudioSample(bdiSurveyResultModel);
    }

    public void deleteAllSample() {
        mBdiSurveyResultModelDao.deleteAll();
    }

    public void deleteAudioSample(BdiSurveyResultModel bdiSurveyResultModel) {
        mBdiSurveyResultModelDao.delete(bdiSurveyResultModel);
    }

    public List<BdiSurveyResultModel> getAllBdiSurveyResultModel() {
        return mBdiSurveyResultModelDao.getAll();
    }

    public LiveData<List<BdiSurveyResultModel>> getAllBdiSurveyResultModelLiveData() {
        return mBdiSurveyResultModelDao.getAllLive();
    }
}
