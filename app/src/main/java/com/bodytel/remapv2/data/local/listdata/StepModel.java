package com.bodytel.remapv2.data.local.listdata;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 8/16/2018 at 6:15 PM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 8/16/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.bodytel.remapv2.data.local.db.TableName;

@Entity(tableName = TableName.STEPS)
public class StepModel {

    @PrimaryKey
    @ColumnInfo(name = "start_time")
    private long startData;
    @ColumnInfo(name = "end_time")
    private long endTate;
    @ColumnInfo(name = "steps")
    private int stepCount;

    public StepModel(int stepCount, long startData, long endTate) {
        this.stepCount = stepCount;
        this.startData = startData;
        this.endTate = endTate;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public long getStartData() {
        return startData;
    }

    public void setStartData(long startData) {
        this.startData = startData;
    }

    public long getEndTate() {
        return endTate;
    }

    public void setEndTate(long endTate) {
        this.endTate = endTate;
    }
}
