package com.bodytel.util.lib.network;

import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel;
import com.bodytel.util.lib.network.callback.DownloadAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.GetAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.StoreAudioSampleCallback;
import com.bodytel.util.lib.network.callback.StoreBdiSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreMoodSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreSleepSurveyCallback;
import com.bodytel.util.lib.network.firebase.FirebaseUtil;

public class NetworkApi {
    private static NetworkApi networkApi = null;

    public static NetworkApi on(){
        if(networkApi == null) networkApi = new NetworkApi();

        return networkApi;
    }

    public void storeAudioSample(String subjectId, String filePath, String fileName, StoreAudioSampleCallback callback){
        FirebaseUtil.on().storeAudioSample(subjectId, filePath, fileName, callback);
    }

    public void getAudioSample(String subjectId, GetAudioSampleCallBack callback){
        FirebaseUtil.on().getAudioSampleList(subjectId, callback);
    }

    public void downloadAudioSample(String downloadUrl, String fileName, DownloadAudioSampleCallBack callback){
        FirebaseUtil.on().downloadAudioSample(downloadUrl, fileName, callback);
    }

    public void storeBdiSurveyData(BdiSurveyResultModel resultModel, StoreBdiSurveyCallback callBack){
        FirebaseUtil.on().storeBdiSurveyResult(resultModel, callBack);
    }

    public void storeSleepSurveyData(SleepSurveyResultModel resultModel, StoreSleepSurveyCallback callback){
        FirebaseUtil.on().storeSleepSurveyResult(resultModel, callback);
    }

    public void storeMoodSurveyData(MoodSurveyResultModel resultModel, StoreMoodSurveyCallback callback){
        FirebaseUtil.on().storeMoodSurveyResult(resultModel, callback);
    }
}
