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
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyItemModel

class BdiSurveyFragment : Fragment() {

    lateinit var tvQuestion:TextView
    lateinit var rgAnswers:RadioGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_bdi_survey, container, false)

        tvQuestion = view.findViewById(R.id.txt_question)
        rgAnswers = view.findViewById(R.id.rg_answers)

        val args = arguments
        if (args != null) {
            val surveyItemModel = args.get("surveyItemModel") as BdiSurveyItemModel

            tvQuestion.text = surveyItemModel.question
            for (i in surveyItemModel.options.indices) {
                val rb = RadioButton(context)
                rb.id = View.generateViewId()
                rb.setPadding(10, 30, 0, 30)
                val option = surveyItemModel.options.get(i)
                rb.textSize = 18f
                if(option.length>60) rb.textSize = 17f
                if(option.length>80) rb.textSize = 16f
                rb.text = option
                rgAnswers.addView(rb)
                if(BdiSurveyActivity.answers.get(surveyItemModel.question) == surveyItemModel.options.indexOf(option)) rb.isChecked = true

                rgAnswers.setOnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = group.findViewById(checkedId)
                    BdiSurveyActivity.onAnswered(surveyItemModel.question, surveyItemModel.options.indexOf(radio.text.toString()))
                }
            }
        }

        return view
    }

    companion object {
        fun newInstance(bdiSurveyItemModel: BdiSurveyItemModel): BdiSurveyFragment {

            val args = Bundle()
            args.putSerializable("bdiSurveyItemModel", bdiSurveyItemModel)

            val fragment = BdiSurveyFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
