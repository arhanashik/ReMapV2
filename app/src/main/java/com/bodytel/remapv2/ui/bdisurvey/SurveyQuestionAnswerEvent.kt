package com.bodytel.remapv2.ui.bdisurvey

interface SurveyQuestionAnswerEvent {
    fun onClickStart()
    fun onAnswer(question : String, answer : Int)
}