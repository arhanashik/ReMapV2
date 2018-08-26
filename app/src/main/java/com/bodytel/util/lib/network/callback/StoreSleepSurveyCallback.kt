package com.bodytel.util.lib.network.callback

import com.bodytel.remapv2.data.local.sleepsurveyitem.SleepSurveyResultModel

interface StoreSleepSurveyCallback {
    fun onStoreSleepSurveySuccessfully(resultModel: SleepSurveyResultModel)
    fun onStoreSleepSurveyFailure(error: String)
}