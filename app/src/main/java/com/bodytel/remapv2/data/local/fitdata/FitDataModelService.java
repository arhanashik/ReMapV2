package com.bodytel.remapv2.data.local.fitdata;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class FitDataModelService {
    private final FitDataModelDao mFitDataModelDao;

    public FitDataModelService(FitDataModelDao fitDataModelDao) {
        mFitDataModelDao = fitDataModelDao;
    }

    public FitDataModel getFitDataModel(long id) {
        return mFitDataModelDao.getFitDataModel(id);
    }

    public long insert(FitDataModel fitDataModel) {
        long id = mFitDataModelDao.insert(fitDataModel);
        fitDataModel.setId(id);
        return id;
    }

    public int updateFitDataModel(FitDataModel fitDataModel) {
        return mFitDataModelDao.updateFitData(fitDataModel);
    }

    public void deleteAllFitData() {
        mFitDataModelDao.deleteAll();
    }

    public void deleteDataOfType(String type){
        mFitDataModelDao.deleteDataOfType(type);
    }

    public void deleteFitData(FitDataModel fitDataModel) {
        mFitDataModelDao.delete(fitDataModel);
    }

    public List<FitDataModel> getAllFitDataModel() {
        return mFitDataModelDao.getAll();
    }

    public List<FitDataModel> getDataListOfType(String type) {
        return mFitDataModelDao.getDataListOfType(type);
    }

    public LiveData<List<FitDataModel>> getAllFiDataModelLiveData() {
        return mFitDataModelDao.getAllLive();
    }
}
