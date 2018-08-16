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

public class StepModel {
    private String stepCount;
    private String startData;
    private String endTate;

    public StepModel(String stepCount, String startData, String endTate) {
        this.stepCount = stepCount;
        this.startData = startData;
        this.endTate = endTate;
    }

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }

    public String getStartData() {
        return startData;
    }

    public void setStartData(String startData) {
        this.startData = startData;
    }

    public String getEndTate() {
        return endTate;
    }

    public void setEndTate(String endTate) {
        this.endTate = endTate;
    }
}
