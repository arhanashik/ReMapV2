package com.bodytel.remapv2.data.local.dbstorage;

import com.bodytel.remapv2.data.local.audiosample.AudioSampleService;

public class DatabaseHelper {
    public static AudioSampleService provideAudioSampleService() {
        return new AudioSampleService(DatabaseService.on().audioSampleModelDao());
    }
}