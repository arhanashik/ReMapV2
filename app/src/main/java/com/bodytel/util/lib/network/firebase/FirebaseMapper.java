package com.bodytel.util.lib.network.firebase;

import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.fitdata.FitDataModel;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirebaseMapper {
    public static Map<String, Object> stepOrDistanceModelToMap(List<FitDataModel> fitDataModels){
        Map<String, Object> sensorData = new LinkedHashMap<>();

        sensorData.put(AppConst.CREATED_AT, System.currentTimeMillis());

        JSONArray array = new JSONArray();
        for (FitDataModel dataModel : fitDataModels){
            JSONObject object = new JSONObject();
            try {
                object.put("created", dataModel.getCreatedAt());
                object.put("start", dataModel.getStart());
                object.put("end", dataModel.getEnd());
                object.put("value", dataModel.getValue());

                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sensorData.put(AppConst.DATA, array.toString());

        return sensorData;
    }

    public static Map<String, Object> audioSampleModelToMap(AudioSampleModel sampleModel){
        Map<String, Object> surveyData = new HashMap<>();

        surveyData.put(AppConst.CREATED_AT, sampleModel.getCreatedAt());
        surveyData.put(AppConst.FILE_NAME, sampleModel.getFileName());
        surveyData.put(AppConst.DOWNLOAD_URL, sampleModel.getDownloadUrl());

        return surveyData;
    }

    public static List<AudioSampleModel> sampleModelListFromQuerySnapshot(QuerySnapshot querySnapshots){
        List<AudioSampleModel> dataModels = new ArrayList<>();

        for(DocumentSnapshot snapshot : querySnapshots){
            Map<String, Object> data = snapshot.getData();

            AudioSampleModel sampleModel = new AudioSampleModel();

            sampleModel.setDataId(snapshot.getId());
            sampleModel.setFileName(String.valueOf(data.get(AppConst.FILE_NAME)));
            sampleModel.setCreatedAt(Long.valueOf(String.valueOf(data.get(AppConst.CREATED_AT))));
            sampleModel.setDownloadUrl(String.valueOf(data.get(AppConst.DOWNLOAD_URL)));

            dataModels.add(sampleModel);
        }

        return dataModels;
    }

    public static Map<String, Object> bdiSurveyModelToMap(BdiSurveyResultModel resultModel){
        Map<String, Object> surveyData = new HashMap<>();

        surveyData.put(AppConst.CREATED_AT, resultModel.getCreatedAt());
        surveyData.put(AppConst.BDI_VERSION, resultModel.getSurveyVersion());
        surveyData.put(AppConst.ANSWERS, resultModel.getAnswers());

        return surveyData;
    }

    public static Map<String, Object> sensorDataToMap(List<AccelerometerDataModel> dataModels){
        Map<String, Object> sensorData = new LinkedHashMap<>();

        sensorData.put(AppConst.CREATED_AT, System.currentTimeMillis());

        JSONArray array = new JSONArray();
        for (AccelerometerDataModel dataModel : dataModels){
            JSONObject object = new JSONObject();
            try {
                object.put("timestamp", dataModel.getCreatedAt());
                object.put("xMean", dataModel.getXMean());
                object.put("yMean", dataModel.getYMean());
                object.put("zMean", dataModel.getZMean());

                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sensorData.put(AppConst.DATA, array.toString());

        return sensorData;
    }

    public static Map<String, Object> sleepSurveyModelToMap(SleepSurveyResultModel resultModel){
        Map<String, Object> surveyData = new HashMap<>();

        surveyData.put(AppConst.CREATED_AT, resultModel.getCreatedAt());
        surveyData.put(AppConst.SURVEY_VERSION, resultModel.getSurveyVersion());
        surveyData.put(AppConst.ANSWER, resultModel.getAnswer());

        return surveyData;
    }

    public static Map<String, Object> moodSurveyModelToMap(MoodSurveyResultModel resultModel){
        Map<String, Object> surveyData = new HashMap<>();

        surveyData.put(AppConst.CREATED_AT, resultModel.getCreatedAt());
        surveyData.put(AppConst.SURVEY_VERSION, resultModel.getSurveyVersion());
        surveyData.put(AppConst.ANSWER, resultModel.getAnswer());

        return surveyData;
    }
}
