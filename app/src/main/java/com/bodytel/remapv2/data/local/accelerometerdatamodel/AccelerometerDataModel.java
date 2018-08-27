package com.bodytel.remapv2.data.local.accelerometerdatamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

@Entity(tableName = TableNames.TBL_ACCELEROMETER_DATA)
public class AccelerometerDataModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ColumnNames.ID)
    private long id;
    @ColumnInfo(name = ColumnNames.CREATED_AT)
    private Long createdAt;
    @ColumnInfo(name = ColumnNames.X_MEAN)
    private float xMean;
    @ColumnInfo(name = ColumnNames.Y_MEAN)
    private float yMean;
    @ColumnInfo(name = ColumnNames.Z_MEAN)
    private float zMean;
    @ColumnInfo(name = ColumnNames.IS_SYNC)
    private boolean isSync;

    public AccelerometerDataModel(long id, Long createdAt, float xMean, float yMean, float zMean, boolean isSync) {
        this.id = id;
        this.createdAt = createdAt;
        this.xMean = xMean;
        this.yMean = yMean;
        this.zMean = zMean;
        this.isSync = isSync;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public float getXMean() {
        return xMean;
    }

    public void setXMean(float xMean) {
        this.xMean = xMean;
    }

    public float getYMean() {
        return yMean;
    }

    public void setYMean(float yMean) {
        this.yMean = yMean;
    }

    public float getZMean() {
        return zMean;
    }

    public void setZMean(float zMean) {
        this.zMean = zMean;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
