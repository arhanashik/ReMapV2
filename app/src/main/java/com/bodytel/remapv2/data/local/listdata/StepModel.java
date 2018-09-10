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
    private long startDate;
    @ColumnInfo(name = "end_time")
    private long endDate;
    @ColumnInfo(name = "steps")
    private int stepCount;

    public StepModel(int stepCount, long startDate, long endDate) {
        this.stepCount = stepCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
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
