package com.bodytel.remapv2.ui.bdisurvey

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.bodytel.remapv2.R
import com.bodytel.remapv2.data.local.surveyitem.SurveyItemModel

class BdiSurveyFragment : Fragment() {

    lateinit var tvQuestion:TextView
    lateinit var rgAnswers:RadioGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        tvQuestion = view.findViewById(R.id.txt_question)
        rgAnswers = view.findViewById(R.id.rg_answers)

        val args = arguments
        if (args != null) {
            val quiz = args.get("quiz") as SurveyItemModel

            tvQuestion.text = quiz.question
            for (i in quiz.options.indices) {
                val rb = RadioButton(context)
                rb.id = View.generateViewId()
                rb.setPadding(10, 30, 0, 30)
                val option = quiz.options.get(i)
                rb.textSize = 18f
                if(option.length>60) rb.textSize = 17f
                if(option.length>80) rb.textSize = 16f
                rb.text = option
                rgAnswers.addView(rb)
                if(BdiSurveyActivity.answers.containsValue(option)) rb.isChecked = true

                rgAnswers.setOnCheckedChangeListener({
                    group, checkedId ->
                    val radio: RadioButton = group.findViewById(checkedId)
                    BdiSurveyActivity.onAnswered(quiz.question, radio.text.toString())
                })
            }
        }

        return view
    }

    companion object {
        fun newInstance(quiz: SurveyItemModel): BdiSurveyFragment {

            val args = Bundle()
            args.putSerializable("quiz", quiz)

            val fragment = BdiSurveyFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
