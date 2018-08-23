package com.bodytel.remapv2.ui.bdisurvey

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bodytel.remapv2.R

class BdiSurveyStartFragment : Fragment() {

    private lateinit var eventListener : SurveyQuestionAnswerEvent

    lateinit var btnStart : Button

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            eventListener = context as SurveyQuestionAnswerEvent
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement SurveyQuestionAnswerEvent")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_survey_start, container, false)

        btnStart = view.findViewById(R.id.fragment_survey_start_btn_start)

        btnStart.setOnClickListener {
            eventListener.onClickStart()
        }
        return view
    }

    companion object {
        fun newInstance(): BdiSurveyStartFragment {
            val fragment = BdiSurveyStartFragment()
            return fragment
        }
    }
}
