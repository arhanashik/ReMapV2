package com.bodytel.remapv2.data.local.bdisurveyitem

import java.io.Serializable

data class BdiSurveyItemModel (val sl: Int, val question: String, val options: List<String>, val answer: String): Serializable