package com.bodytel.remapv2.ui.bdisurvey

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bodytel.remapv2.R

class BdiSurveyEndFragment : Fragment() {

    lateinit var txtTitle : TextView
    lateinit var txtSubTitle : TextView
    lateinit var icDone : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_survey_end, container, false)

        txtTitle = view.findViewById(R.id.fragment_survey_end_txt_title)
        txtSubTitle = view.findViewById(R.id.fragment_survey_end_txt_sub_title)
        icDone = view.findViewById(R.id.fragment_survey_end_ic)

//        if(BdiSurveyActivity.remaining > 0){
//            txtTitle.text = resources.getText(R.string.label_activity_not_completed)
//            txtSubTitle.text = resources.getText(R.string.label_hint_to_complete_activity)
//            icDone.setImageResource(R.drawable.ic_close_circle)
//        }else{
//            txtTitle.text = resources.getText(R.string.label_activity_completed)
//            txtSubTitle.text = resources.getText(R.string.label_thanks)
//            icDone.setImageResource(R.drawable.ic_check_circle)
//        }

        return view
    }

    companion object {
        fun newInstance(): BdiSurveyEndFragment {
            val fragment = BdiSurveyEndFragment()
            return fragment
        }
    }
}
