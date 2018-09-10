package com.bodytel.remapv2.data.local.fitdata;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.bodytel.remapv2.data.local.dbstorage.ColumnNames;
import com.bodytel.remapv2.data.local.dbstorage.TableNames;

@Entity(tableName = TableNames.TBL_FIT_DATA)
public class FitDataModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ColumnNames.ID)
    private long id;
    @ColumnInfo(name = ColumnNames.CREATED_AT)
    private long createdAt;
    @ColumnInfo(name = ColumnNames.TYPE)
    private String type;
    @ColumnInfo(name = ColumnNames.START)
    private long start;
    @ColumnInfo(name = ColumnNames.END)
    private long end;
    @ColumnInfo(name = ColumnNames.VALUE)
    private String value;

    public FitDataModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
