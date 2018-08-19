package com.bodytel.remapv2.ui.audiosample;

public interface OnNewAudioSampleEvent {
    void onPrepareNewAudioRecording();
    void onRequestNewAudioRecording();
    void onAudioRecordingDone(long recordingTimeInMils);
}
