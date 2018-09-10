package com.bodytel.remapv2.data.local.dbstorage;

import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataService;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleService;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultService;
import com.bodytel.remapv2.data.local.fitdata.FitDataModelService;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultService;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultService;

public class DatabaseHelper {
    public static FitDataModelService provideFitDataModelService() {
        return new FitDataModelService(DatabaseService.on().fitDataModelDao());
    }

    public static AudioSampleService provideAudioSampleService() {
        return new AudioSampleService(DatabaseService.on().audioSampleModelDao());
    }

    public static BdiSurveyResultService provideBdiSurveyResultService(){
        return new BdiSurveyResultService((DatabaseService.on().bdiSurveyResultModelDao()));
    }

    public static AccelerometerDataService provideAccelerometerDataService(){
        return new AccelerometerDataService(DatabaseService.on().accelerometerDataModelDao());
    }

    public static SleepSurveyResultService provideSleepSurveyResultService(){
        return new SleepSurveyResultService(DatabaseService.on().sleepSurveyResultModelDao());
    }

    public static MoodSurveyResultService provideMoodSurveyResultService(){
        return new MoodSurveyResultService(DatabaseService.on().moodSurveyResultModelDao());
    }
}