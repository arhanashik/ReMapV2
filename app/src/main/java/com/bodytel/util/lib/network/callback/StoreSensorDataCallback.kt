package com.bodytel.util.lib.network.callback

import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel

interface StoreSensorDataCallback {
    fun onStoreSensorDataSuccessfully(dataId: String)
    fun onStoreSensorDataFailure(error: String)
}