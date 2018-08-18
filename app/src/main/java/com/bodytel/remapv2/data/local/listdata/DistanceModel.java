package com.bodytel.remapv2.data.local.listdata;

public class DistanceModel {
    private float distance;
    private long startDate;
    private long endDate;

    public DistanceModel(float distance, long startDate, long endDate) {
        this.distance = distance;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
