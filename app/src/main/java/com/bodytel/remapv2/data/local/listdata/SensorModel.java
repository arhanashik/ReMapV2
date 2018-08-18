package com.bodytel.remapv2.data.local.listdata;

public class SensorModel {
    private long timeStemp;
    private String value;

    public SensorModel(long timeStemp, String value) {
        this.timeStemp = timeStemp;
        this.value = value;
    }

    public long getTimeStemp() {
        return timeStemp;
    }

    public void setTimeStemp(long timeStemp) {
        this.timeStemp = timeStemp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
