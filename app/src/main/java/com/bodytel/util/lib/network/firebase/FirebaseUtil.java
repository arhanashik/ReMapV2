package com.bodytel.util.lib.network.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.bodytel.ReMapApp;
import com.bodytel.remapv2.R;
import com.bodytel.remapv2.data.local.AppConst;
import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel;
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel;
import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel;
import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel;
import com.bodytel.remapv2.ui.audiosample.NewAudioSampleActivity;
import com.bodytel.remapv2.ui.bdisurvey.BdiSurveyEndFragment;
import com.bodytel.util.lib.network.callback.DownloadAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.GetAudioSampleCallBack;
import com.bodytel.util.lib.network.callback.StoreAudioSampleCallback;
import com.bodytel.util.lib.network.callback.StoreBdiSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreMoodSurveyCallback;
import com.bodytel.util.lib.network.callback.StoreSleepSurveyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseUtil {
    private static final String TAG = FirebaseUtil.class.getSimpleName();

    private static FirebaseUtil firebaseUtil = null;

    private FirebaseFirestore firestoreDb;
    private StorageReference storageReference, fileDb;

    private FirebaseUtil(){

    }

    public void init(){
        firestoreDb = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseUtil on(){
        if(firebaseUtil == null) firebaseUtil = new FirebaseUtil();

        return firebaseUtil;
    }

    public void storeAudioSample(String subjectId, String filePath, String fileName, StoreAudioSampleCallback callback){
        Uri fileUri = Uri.fromFile(new File(filePath));
        fileDb = storageReference.child("audio/" + fileName);

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

        Map<String, Object> data = FirebaseMapper.audioSampleModelToMap(sampleModel);

        try {
            firestoreDb.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA)
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

    public void getAudioSampleList(String subjectId, GetAudioSampleCallBack callBack){
        try{
            firestoreDb.collection(AppConst.COLLECTION_AUDIO_SAMPLE_DATA)
                    .get()
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){
                            List<AudioSampleModel> dataModels = FirebaseMapper
                                    .sampleModelListFromQuerySnapshot(subjectId, task.getResult());

                            callBack.onLoadAudioSampleSuccessfully(dataModels);
                        }
                    })
                    .addOnFailureListener(e -> callBack.onLoadAudioSampleFailure(e.getMessage()));
        }catch (Exception e){
            e.printStackTrace();

            callBack.onLoadAudioSampleFailure(e.getMessage());
        }
    }

    public void downloadAudioSample(String downloadUrl, String fileName, DownloadAudioSampleCallBack callBack){
        fileDb = storageReference.child("audio/" + fileName);

        final String filePath = Objects.requireNonNull(ReMapApp.getContext().getExternalCacheDir())
                .getAbsolutePath() +"/" + fileName;
        File tempFile;

        try {
            //File localFile = new File(filePath);
            tempFile = File.createTempFile(fileName.substring(0, fileName.indexOf(".")), "m4a");
            if(tempFile.exists()) {
                callBack.onDownloadAudioSampleSuccessfully(filePath, "File exists in local directory");
            }
            else {
                fileDb.getFile(tempFile).addOnSuccessListener(taskSnapshot -> {
                    callBack.onDownloadAudioSampleSuccessfully(filePath, "File downloaded successfully");

                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    callBack.onDownloadAudioSampleFailed(e.getMessage());
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            callBack.onDownloadAudioSampleFailed(e.getMessage());
        }
    }

    public void storeBdiSurveyResult(BdiSurveyResultModel resultModel, StoreBdiSurveyCallback callBack){
        try {
            firestoreDb.collection(AppConst.COLLECTION_BDI_SURVEY_DATA)
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

    public void storeSleepSurveyResult(SleepSurveyResultModel resultModel, StoreSleepSurveyCallback callback){
        try {
            firestoreDb.collection(AppConst.COLLECTION_SLEEP_SURVEY_DATA)
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
            firestoreDb.collection(AppConst.COLLECTION_SLEEP_SURVEY_DATA)
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
