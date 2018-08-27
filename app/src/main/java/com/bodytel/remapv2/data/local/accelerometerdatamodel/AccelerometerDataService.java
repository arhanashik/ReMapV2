package com.bodytel.remapv2.data.local.accelerometerdatamodel;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class AccelerometerDataService {
    private final AccelerometerDataModelDao mAccelerometerDataModelDao;

    public AccelerometerDataService(AccelerometerDataModelDao accelerometerDataModelDao) {
        mAccelerometerDataModelDao = accelerometerDataModelDao;
    }

    public AccelerometerDataModel getAccelerometerDataModel(long id) {
        return mAccelerometerDataModelDao.getAccelerometerDataModel(id);
    }

    public AccelerometerDataModel getAccelerometerDataModel(String dataId) {
        return mAccelerometerDataModelDao.getAccelerometerDataModel(dataId);
    }

    public long insert(AccelerometerDataModel accelerometerDataModel) {
        long id = mAccelerometerDataModelDao.insert(accelerometerDataModel);
        accelerometerDataModel.setId(id);
        return id;
    }

    public int updateMoodSurveyResultModel(AccelerometerDataModel accelerometerDataModel) {
        return mAccelerometerDataModelDao.updateAcceleroMeterDataModel(accelerometerDataModel);
    }

    public void deleteAllAccelerometerData() {
        mAccelerometerDataModelDao.deleteAll();
    }

    public void deleteAccelerometer(AccelerometerDataModel accelerometerDataModel) {
        mAccelerometerDataModelDao.delete(accelerometerDataModel);
    }

    public List<AccelerometerDataModel> getAllAccelerometerDataModel() {
        return mAccelerometerDataModelDao.getAll();
    }

    public LiveData<List<AccelerometerDataModel>> getAllAccelerometerDataModelLiveData() {
        return mAccelerometerDataModelDao.getAllLive();
    }
}
