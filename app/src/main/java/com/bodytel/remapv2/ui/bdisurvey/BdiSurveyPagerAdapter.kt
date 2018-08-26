package com.bodytel.remapv2.ui.bdisurvey

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyItemModel

//private const val MAX_VALUE = 200

class BdiSurveyPagerAdapter(fragmentManager: FragmentManager, private val bdiSurveyItems: List<BdiSurveyItemModel>) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        if(position == 0) return BdiSurveyStartFragment.newInstance()
        else if(position == count-1) return BdiSurveyEndFragment.newInstance()

        return BdiSurveyFragment.newInstance(bdiSurveyItems[position])
        //return BdiSurveyFragment.newInstance(bdiSurveyItems[position % bdiSurveyItems.size])
    }

    override fun getCount(): Int {
        return bdiSurveyItems.size
        //return bdiSurveyItems.size * MAX_VALUE
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Step " + (bdiSurveyItems[position % bdiSurveyItems.size].sl + 1).toString() + " of " + bdiSurveyItems.size
    }
}