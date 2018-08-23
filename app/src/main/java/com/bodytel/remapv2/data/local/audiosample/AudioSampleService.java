package com.bodytel.remapv2.data.local.audiosample;

import android.arch.lifecycle.LiveData;

import java.util.List;

public class AudioSampleService {
    private final AudioSampleModelDao mAudioSampleModelDao;

    public AudioSampleService(AudioSampleModelDao audioSampleModelDao) {
        mAudioSampleModelDao = audioSampleModelDao;
    }

    public AudioSampleModel getAudioSample(long id) {
        return mAudioSampleModelDao.getAudioSample(id);
    }

    public AudioSampleModel getAudioSample(String dataId) {
        return mAudioSampleModelDao.getAudioSample(dataId);
    }

    public long insert(AudioSampleModel audioSampleModel) {
        long id = mAudioSampleModelDao.insert(audioSampleModel);
        audioSampleModel.setId(id);
        return id;
    }

    public int updateAudioSample(AudioSampleModel audioSampleModel) {
        return mAudioSampleModelDao.updateAudioSample(audioSampleModel);
    }

    public void deleteAllSample() {
        mAudioSampleModelDao.deleteAll();
    }

    public void deleteAudioSample(AudioSampleModel audioSampleModel) {
        mAudioSampleModelDao.delete(audioSampleModel);
    }

    public List<AudioSampleModel> getAllAudioSampleModel() {
        return mAudioSampleModelDao.getAll();
    }

    public LiveData<List<AudioSampleModel>> getAllSampleLiveData() {
        return mAudioSampleModelDao.getAllLive();
    }
}
