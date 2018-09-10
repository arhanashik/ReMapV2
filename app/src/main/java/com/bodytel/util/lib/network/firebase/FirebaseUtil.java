package com.bodytel.util.lib.network.firebase;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bodytel.ReMapApp;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.accelerometerdatamodel.AccelerometerDataModel;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.fitdata.FitDataModel;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal;
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel;
import com.bodytel.util.lib.network.callback.DownloadAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.GetAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.StoreAudioSampleCallback;
import com.bodytel.util.lib.network.callback.StoreBdiSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreMoodSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreSensorDataCallback;
import com.bodytel.util.lib.network.callback.StoreSleepSurveyCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseUtil {
    private static final String TAG = FirebaseUtil.class.getSimpleName();

    private static FirebaseUtil firebaseUtil = null;

    private static PrefGlobal prefGlobal;

    private static DocumentReference documentReference;
    private static StorageReference storageReference, fileDb;

    private FirebaseUtil(){

    }

    public static FirebaseUtil on(){
        if(firebaseUtil == null) firebaseUtil = new FirebaseUtil();

        return firebaseUtil;
    }

    public void init(){
        prefGlobal = PrefHelper.providePrefGlobal();

        String subjectId = prefGlobal.getSubjectId();

        if(documentReference == null && !TextUtils.isEmpty(subjectId)){
            initFirestoreDocumentInstance(subjectId);
        }

        if(storageReference == null && !TextUtils.isEmpty(subjectId)){
            initFirebaseStorageReference(subjectId);
        }
    }

    private void initFirestoreDocumentInstance(String document){
        documentReference = FirebaseFirestore
                .getInstance()
                .collection(AppConst.ROOT)
                .document(document);
    }

    private void initFirebaseStorageReference(String child){
        storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child(child);
    }

    public void storeStepsOrDistanceData(String dataType, List<FitDataModel> fitDataModels,
                                         StoreSensorDataCallback callBack){
        try {
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            String collection = AppConst.COLLECTION_STEPS_DATA;
            if(!dataType.equals(AppConst.STEPS)) collection = AppConst.COLLECTION_DISTANCES_DATA;

            documentReference.collection(collection)
                    .add(FirebaseMapper.stepOrDistanceModelToMap(fitDataModels))
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        callBack.onStoreSensorDataSuccessfully(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);

                        callBack.onStoreSensorDataFailure(e.getMessage());
                    });

        }catch (Exception e){
            e.printStackTrace();
            callBack.onStoreSensorDataFailure(e.getMessage());
        }
    }

    public void storeAudioSample(String subjectId, String filePath, String fileName, StoreAudioSampleCallback callback){
        if(storageReference == null) initFirebaseStorageReference(prefGlobal.getSubjectId());

        Uri fileUri = Uri.fromFile(new File(filePath));
        fileDb = storageReference.child(fileName);

        fileDb.putFile(fileUri)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "Upload is " + progress + "% done");

                    callback.onStoreAudioSampleProgress(progress);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.getMessage());

                    callback.onStoreAudioSampleFailure(e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    callback.onStoreAudioSampleSuccessfully(fileDb.getDownloadUrl().toString());

                    AudioSampleModel sampleModel = new AudioSampleModel(
                            0,
                            "",
                            subjectId,
                            System.currentTimeMillis(),
                            fileName,
                            fileDb.getDownloadUrl().toString()
                    );
                    storeAudioSampleReference(sampleModel, callback);
                });
    }

    private void storeAudioSampleReference(AudioSampleModel sampleModel, StoreAudioSampleCallback callback){
        try {
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            Map<String, Object> data = FirebaseMapper.audioSampleModelToMap(sampleModel);
            documentReference.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA)
                    .add(data)
                    .addOnCompleteListener(task -> {
                        sampleModel.setDataId(task.getResult().getId());

                        callback.onStoreAudioSampleReferenceSuccessfully(sampleModel);
                    })
                    .addOnFailureListener(e -> callback.onStoreAudioSampleFailure(e.getMessage()));

        }catch (Exception e){
            e.printStackTrace();

            callback.onStoreAudioSampleFailure(e.getMessage());
        }
    }

    public void getAudioSampleList(GetAudioSampleCallBack callBack){
        try{
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            documentReference.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA)
                    .get()
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){
                            List<AudioSampleModel> dataModels = FirebaseMapper
                                    .sampleModelListFromQuerySnapshot(task.getResult());

                            callBack.onLoadAudioSampleSuccessfully(dataModels);
                        }
                    })
                    .addOnFailureListener(e -> callBack.onLoadAudioSampleFailure(e.getMessage()));
        }catch (Exception e){
            e.printStackTrace();

            callBack.onLoadAudioSampleFailure(e.getMessage());
        }
    }

    public void downloadAudioSample(String fileName, DownloadAudioSampleCallBack callBack){
        if(storageReference == null) initFirebaseStorageReference(prefGlobal.getSubjectId());

        fileDb = storageReference.child(fileName);

        final File outputDir = Objects.requireNonNull(ReMapApp.getContext().getExternalCacheDir());

        File localFile = new File(outputDir, fileName);

        if(localFile.exists() && localFile.length() > 0) {
            callBack.onDownloadAudioSampleSuccessfully(localFile.getAbsolutePath(),
                    "File exists in local directory: " + localFile.length() + " byte");
        } else {
            fileDb.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot ->
                            callBack.onDownloadAudioSampleSuccessfully(localFile.getAbsolutePath(),
                                    "File downloaded successfully: " + localFile.length() + " byte"))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        callBack.onDownloadAudioSampleFailed(e.getMessage());
                    });
        }
    }

    public void storeBdiSurveyResult(BdiSurveyResultModel resultModel, StoreBdiSurveyCallback callBack){
        try {
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            documentReference.collection(AppConst.COLLECTION_BDI_SURVEY_DATA)
                    .add(FirebaseMapper.bdiSurveyModelToMap(resultModel))
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        resultModel.setDataId(documentReference.getId());
                        callBack.onStoreBdiSurveyDataSuccessfully(resultModel);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);

                        callBack.onStoreBdiSurveyDataFailure(e.getMessage());
                    });

        }catch (Exception e){
            e.printStackTrace();
            callBack.onStoreBdiSurveyDataFailure(e.getMessage());
        }
    }

    public void storeSensorData(List<AccelerometerDataModel> dataModels, StoreSensorDataCallback callback){
        try {
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            documentReference.collection(AppConst.COLLECTION_SENSOR_DATA)
                    .add(FirebaseMapper.sensorDataToMap(dataModels))
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        callback.onStoreSensorDataSuccessfully(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);

                        callback.onStoreSensorDataFailure(e.getMessage());
                    });

        }catch (Exception e){
            e.printStackTrace();
            callback.onStoreSensorDataFailure(e.getMessage());
        }
    }

    public void storeSleepSurveyResult(SleepSurveyResultModel resultModel, StoreSleepSurveyCallback callback){
        try {
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            documentReference.collection(AppConst.COLLECTION_SLEEP_SURVEY_DATA)
                    .add(FirebaseMapper.sleepSurveyModelToMap(resultModel))
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        resultModel.setDataId(documentReference.getId());
                        callback.onStoreSleepSurveySuccessfully(resultModel);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);

                        callback.onStoreSleepSurveyFailure(e.getMessage());
                    });

        }catch (Exception e){
            e.printStackTrace();
            callback.onStoreSleepSurveyFailure(e.getMessage());
        }
    }

    public void storeMoodSurveyResult(MoodSurveyResultModel resultModel, StoreMoodSurveyCallback callback){
        try {
            if(documentReference == null) initFirestoreDocumentInstance(prefGlobal.getSubjectId());

            documentReference.collection(AppConst.COLLECTION_MOOD_SURVEY_DATA)
                    .add(FirebaseMapper.moodSurveyModelToMap(resultModel))
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        resultModel.setDataId(documentReference.getId());
                        callback.onStoreMoodSurveySuccessfully(resultModel);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding document", e);

                        callback.onStoreMoodSurveyFailure(e.getMessage());
                    });

        }catch (Exception e){
            e.printStackTrace();
            callback.onStoreMoodSurveyFailure(e.getMessage());
        }
    }
}
