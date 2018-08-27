package com.bodytel.remapv2.ui.bdisurvey

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.bodytel.remapv2.R
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyItemModel
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyResultModel
import com.bodytel.remapv2.ui.debug.DebugActivity
import com.bodytel.util.helper.SurveyHelper
import com.bodytel.util.lib.network.NetworkApi
import com.bodytel.util.lib.network.callback.StoreBdiSurveyCallback
import com.nshmura.recyclertablayout.RecyclerTabLayout
import kotlinx.android.synthetic.main.activity_bdi_survey.*
import org.jetbrains.anko.toast
import java.util.*

class StoreBdiSurveyActivity : AppCompatActivity(), SurveyQuestionAnswerEvent, StoreBdiSurveyCallback{

    private lateinit var viewPager: ViewPager
    private lateinit var recyclerTabLayout: RecyclerTabLayout
    private lateinit var pagerAdapterBdi: BdiSurveyPagerAdapter
    private lateinit var btnEndSurvey: TextView

    private lateinit var menuItemCancel : MenuItem
    private var loader : ProgressDialog? = null

    private lateinit var globalData : PrefGlobal

    private var dataStored : Boolean = false

    companion object {
        var bdiSurveyQuestions: List<BdiSurveyItemModel> = ArrayList()
        var answers: LinkedHashMap<String, Int> = LinkedHashMap()
        var remaining = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bdi_survey)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        globalData = PrefHelper.providePrefGlobal()

        recyclerTabLayout = findViewById(R.id.recycler_tab_layout)
        viewPager = findViewById(R.id.view_pager)
        btnEndSurvey = findViewById(R.id.btn_end_quiz)

        answers.clear()
        bdiSurveyQuestions = SurveyHelper.getSurveyQuestionsFromJson("survey_1.json", this)
        remaining = bdiSurveyQuestions.size - 2
        pagerAdapterBdi = BdiSurveyPagerAdapter(supportFragmentManager, bdiSurveyQuestions)
        viewPager.adapter = pagerAdapterBdi
        recyclerTabLayout.setUpWithViewPager(viewPager)
        //viewPager.currentItem = pagerAdapterBdi.count / 2

//        btnEndSurvey.setOnClickListener {
//            closeSurvey(remaining)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_survey, menu)

        menuItemCancel = menu!!.getItem(0)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_cancel ->{
                if(!dataStored){
                    closeSurvey(remaining)
                }else {
                    val intent = Intent(this, DebugActivity:: class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClickStart() {
        viewPager.setCurrentItem(1, true)
    }

    override fun onAnswer(question: String, answer: Int) {
        val isOld = answers.containsKey(question)
        answers[question] = answer

        remaining = (bdiSurveyQuestions.size - 2) - answers.size
        if(! dataStored){
            if(remaining == 0){
                menuItemCancel.title = "Done"
                closeSurvey(remaining)

            }else {
                if(viewPager.currentItem != bdiSurveyQuestions.size - 2){
                    Handler().postDelayed({
                        if(!isOld) viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                    }, 250L)
                }
            }
        }
    }

    override fun onStoreBdiSurveyDataSuccessfully(resultModel: BdiSurveyResultModel) {
        showProgress(false)

        toast("data stored " + resultModel.dataId)

        dataStored = true
        viewPager.setCurrentItem(pagerAdapterBdi.count - 1, true)
    }

    override fun onStoreBdiSurveyDataFailure(message: String) {
        runOnUiThread { showProgress(false) }

        toast(message)
    }

    private fun closeSurvey(remaining: Int){
        var message = "Are you surely want to finish?"
        if(remaining > 0) message += " You haven't finished $remaining steps."
        else message = "Store survey data?"

        val builder = AlertDialog.Builder(this)
        builder.setTitle("BDI Survey")
                .setMessage(message)
                .setPositiveButton("YES"){ _, _ ->
                    if(remaining > 0) {
                        val intent = Intent(this, DebugActivity:: class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        val answersArr = answers.toSortedMap().values.toList()
                        if(answersArr.isEmpty()) toast("nothing to save")
                        else {
                            showProgress(true)
                            storeDataToFirestore(answersArr)
                        }
                    }
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun storeDataToFirestore(answers: List<Int>){
        showProgress(true)

        val surveyResultModel= BdiSurveyResultModel(
                0,
                "",
                globalData.subjectId,
                System.currentTimeMillis(),
                globalData.bdiVersion,
                answers,
                false)

        NetworkApi.on().storeBdiSurveyData(surveyResultModel, this)
    }

    private fun showProgress(show : Boolean){
        if(loader == null) {
            loader = ProgressDialog(this)
            loader!!.setMessage("Storing data...")
        }

        if(show) {
            if(!loader!!.isShowing) loader!!.show()
        }
        else {
            if(loader!!.isShowing) loader!!.dismiss()
        }
    }
}
