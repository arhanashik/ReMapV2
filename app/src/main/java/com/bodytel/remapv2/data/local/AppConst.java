package com.bodytel.remapv2.data.local;

public interface AppConst {
    String LIST_DATA_TYPE = "list_data_type";
    int TYPE_STEPS = 1;
    int TYPE_DISTANCE = 2;
    int TYPE_AUDIO_SAMPLE = 3;
    int TYPE_ACCELEROMETER_DATA = 4;

    String COLLECTION_AUDIO_SAMPLE_DATA = "audio_sample_data";
    String COLLECTION_BDI_SURVEY_DATA = "bdi_survey_data";
    String COLLECTION_SENSOR_DATA = "sensor_data";
    String COLLECTION_SLEEP_SURVEY_DATA = "sleep_survey_data";
    String COLLECTION_MOOD_SURVEY_DATA = "mood_survey_data";

    String SUBJECT_ID = "subjectId";
    String CREATED_AT = "createdAt";
    String BDI_VERSION = "bdiVersion";
    String SURVEY_VERSION = "surveyVersion";
    String ANSWERS = "answers";
    String ANSWER = "answer";
    String FILE_NAME = "file_name";
    String DOWNLOAD_URL = "download_url";
    String DATA = "data";

    int REQUEST_CODE_RECORD_AUDIO = 11;

    String JOB_TAG_SEND_DATA_TO_REMOTE = "com.bodytel.remapv2-job";
}
