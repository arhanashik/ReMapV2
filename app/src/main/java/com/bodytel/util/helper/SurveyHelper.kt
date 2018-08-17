package com.bodytel.util.helper

import android.content.Context
import android.util.Log
import com.bodytel.remapv2.data.local.surveyitem.SurveyItemModel
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object SurveyHelper {

  const val KEY_ARRAY = "surveyQuestions"
  const val KEY_SL = "sl"
  const val KEY_TITLE = "title"
  const val KEY_OPTIONS = "options"
  const val KEY_ANSWER = "answer"

  fun getSurveyQuestionsFromJson(fileName: String, context: Context): List<SurveyItemModel> {
        val surveyQuestionsList = ArrayList<SurveyItemModel>()

        try {

            // Load the JSONArray from the file
            val jsonString = loadJsonFromFile(fileName, context)
            val json = JSONObject(jsonString)
            val jsonSurveyQuestions = json.getJSONArray(KEY_ARRAY)

            //this item is dummy and is added for showing the start screen
            surveyQuestionsList.add(SurveyItemModel(0, "Let's start", ArrayList<String>(), ""))

            for (index in 0 until jsonSurveyQuestions.length()) {
                val obj = jsonSurveyQuestions.getJSONObject(index) as JSONObject
                val sl = obj.getInt(KEY_SL)
                val title = jsonSurveyQuestions.getJSONObject(index).getString(KEY_TITLE)

                val optionsArr = obj.getJSONArray(KEY_OPTIONS)
                val options = ArrayList<String>()
                for (i in 0 until optionsArr.length()) {
                    options.add(optionsArr.getJSONObject(i).getString((i+1).toString()))
                }

                surveyQuestionsList.add(SurveyItemModel(sl, title, options, ""))
            }

            //this item is dummy and is added for showing the end screen
            surveyQuestionsList.add(SurveyItemModel(surveyQuestionsList.size, "Let's End", ArrayList<String>(), ""))

        } catch (e: JSONException) {
            Log.d(SurveyHelper.javaClass.simpleName, e.toString())
            return surveyQuestionsList
        }

    return surveyQuestionsList
  }

  private fun loadJsonFromFile(filename: String, context: Context): String {
        var json = ""

        try {
              val input = context.assets.open(filename)
              val size = input.available()
              val buffer = ByteArray(size)
              input.read(buffer)
              input.close()
              json = buffer.toString(Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
  }
}