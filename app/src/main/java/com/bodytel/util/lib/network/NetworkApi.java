package com.bodytel.util.lib.network;

import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel;
import com.bodytel.util.lib.network.callback.DownloadAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.GetAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.StoreAudioSampleCallback;
import com.bodytel.util.lib.network.callback.StoreBdiSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreMoodSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreSensorDataCallback;
import com.bodytel.util.lib.network.callback.StoreSleepSurveyCallback;
import com.bodytel.util.lib.network.firebase.FirebaseUtil;

import java.util.List;

public class NetworkApi {
    private static NetworkApi networkApi = null;

    public static NetworkApi on(){
        if(networkApi == null) networkApi = new NetworkApi();

        return networkApi;
    }

    public void storeAudioSample(String subjectId, String filePath, String fileName, StoreAudioSampleCallback callback){
        FirebaseUtil.on().storeAudioSample(subjectId, filePath, fileName, callback);
    }

    public void getAudioSample(GetAudioSampleCallBack callback){
        FirebaseUtil.on().getAudioSampleList(callback);
    }

    public void downloadAudioSample(String fileName, DownloadAudioSampleCallBack callback){
        FirebaseUtil.on().downloadAudioSample(fileName, callback);
    }

    public void storeBdiSurveyData(BdiSurveyResultModel resultModel, StoreBdiSurveyCallback callBack){
        FirebaseUtil.on().storeBdiSurveyResult(resultModel, callBack);
    }

    public void storeSensorData(List<AccelerometerDataModel> dataModels, StoreSensorDataCallback callback){
        FirebaseUtil.on().storeSensorData(dataModels, callback);
    }

    public void storeSleepSurveyData(SleepSurveyResultModel resultModel, StoreSleepSurveyCallback callback){
        FirebaseUtil.on().storeSleepSurveyResult(resultModel, callback);
    }

    public void storeMoodSurveyData(MoodSurveyResultModel resultModel, StoreMoodSurveyCallback callback){
        FirebaseUtil.on().storeMoodSurveyResult(resultModel, callback);
    }
}
