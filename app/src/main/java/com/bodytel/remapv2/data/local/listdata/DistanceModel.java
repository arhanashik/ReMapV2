package com.bodytel.remapv2.data.local.listdata;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.bodytel.remapv2.data.local.db.TableName;

@Entity(tableName = TableName.DISTANCE)
public class DistanceModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "start_time")
    private long startDate;

    @ColumnInfo(name = "end_time")
    private long endDate;

    @ColumnInfo(name = "distance")
    private float distance;

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

    @Override
    public String toString() {
        return "Dis ="+distance+" Start ="+startDate+" End ="+endDate;
    }
}
