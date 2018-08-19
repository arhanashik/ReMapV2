package com.bodytel.remapv2.data.local.audiosample;

public class AudioSampleModel {
    private String id;
    private String fileName;
    private long createdAt;
    private String downloadUrl;

    public AudioSampleModel() {
    }

    public AudioSampleModel(String id, String fileName, long createdAt, String downloadUrl) {
        this.id = id;
        this.fileName = fileName;
        this.createdAt = createdAt;
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
