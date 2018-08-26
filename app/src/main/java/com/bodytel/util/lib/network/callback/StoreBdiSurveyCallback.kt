package com.bodytel.util.lib.network.callback

import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel

interface StoreBdiSurveyCallback {
    fun onStoreBdiSurveyDataSuccessfully(resultModel: BdiSurveyResultModel)
    fun onStoreBdiSurveyDataFailure(error: String)
}