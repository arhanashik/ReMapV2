package com.bodytel.remapv2.ui.bdisurvey

interface OnSurveyQuestionEvent {
    fun onAnswer(question : String, answer : Int)
}