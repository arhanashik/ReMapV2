package com.bodytel.remapv2.ui.bdisurvey

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.bodytel.remapv2.data.local.surveyitem.SurveyItemModel

//private const val MAX_VALUE = 200

class BdiSurveyPagerAdapter(fragmentManager: FragmentManager, private val surveyItems: List<SurveyItemModel>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return BdiSurveyFragment.newInstance(surveyItems[position])
        //return BdiSurveyFragment.newInstance(surveyItems[position % surveyItems.size])
    }

    override fun getCount(): Int {
        return surveyItems.size
        //return surveyItems.size * MAX_VALUE
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Step " + surveyItems[position % surveyItems.size].sl.toString() + " of " + surveyItems.size
    }
}