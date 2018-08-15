package com.bodytel.remapv2.data.local.surveyitem

import java.io.Serializable

data class SurveyItemModel (val sl: Int, val question: String, val options: List<String>, val answer: String): Serializable