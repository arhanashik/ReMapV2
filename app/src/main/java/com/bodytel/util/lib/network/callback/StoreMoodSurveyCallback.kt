package com.bodytel.util.lib.network.callback

import com.bodytel.remapv2.data.local.moodsurveyitem.MoodSurveyResultModel

interface StoreMoodSurveyCallback {
    fun onStoreMoodSurveySuccessfully(resultModel: MoodSurveyResultModel)
    fun onStoreMoodSurveyFailure(error: String)
}