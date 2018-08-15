package com.bodytel.util.helper

import android.content.Context
import android.util.Log
import com.bodytel.remapv2.data.local.surveyitem.SurveyItemModel
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object QuizHelper {

  const val KEY_ARRAY = "quizzes"
  const val KEY_SL = "sl"
  const val KEY_TITLE = "title"
  const val KEY_OPTIONS = "options"
  const val KEY_ANSWER = "answer"

  fun getQuizzesFromJson(fileName: String, context: Context): List<SurveyItemModel> {
        val quizzes = ArrayList<SurveyItemModel>()

        try {

            // Load the JSONArray from the file
            val jsonString = loadJsonFromFile(fileName, context)
            val json = JSONObject(jsonString)
            val jsonQuizzes = json.getJSONArray(KEY_ARRAY)

            for (index in 0 until jsonQuizzes.length()) {
                val obj = jsonQuizzes.getJSONObject(index) as JSONObject
                val sl = obj.getInt(KEY_SL)
                val title = jsonQuizzes.getJSONObject(index).getString(KEY_TITLE)

                val optionsArr = obj.getJSONArray(KEY_OPTIONS)
                val options = ArrayList<String>()
                for (i in 0 until optionsArr.length()) {
                    options.add(optionsArr.getJSONObject(i).getString((i+1).toString()))
                }

                quizzes.add(SurveyItemModel(sl, title, options, ""))
            }
        } catch (e: JSONException) {
            Log.d(QuizHelper.javaClass.simpleName, e.toString())
            return quizzes
        }

    return quizzes
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