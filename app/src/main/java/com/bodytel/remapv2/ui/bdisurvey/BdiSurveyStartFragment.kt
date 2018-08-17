package com.bodytel.remapv2.ui.bdisurvey

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bodytel.remapv2.R

class BdiSurveyStartFragment : Fragment() {

    lateinit var btnStart : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_survey_start, container, false)

        btnStart = view.findViewById(R.id.fragment_survey_start_btn_start)

        btnStart.setOnClickListener {
            BdiSurveyActivity.onClickStart()
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
