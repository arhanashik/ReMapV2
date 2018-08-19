package com.bodytel.remapv2.ui.bdisurvey

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.bodytel.remapv2.R
import com.bodytel.remapv2.data.local.AppConst
import com.bodytel.remapv2.data.local.sharedpref.PrefGlobal
import com.bodytel.remapv2.data.local.sharedpref.PrefHelper
import com.bodytel.remapv2.data.local.bdisurveyitem.BdiSurveyItemModel
import com.bodytel.remapv2.ui.debug.DebugActivity
import com.bodytel.util.helper.SurveyHelper
import com.nshmura.recyclertablayout.RecyclerTabLayout
import kotlinx.android.synthetic.main.activity_bdi_survey.*
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.toast
import java.util.*

class BdiSurveyActivity : AppCompatActivity() {

    private var TAG = BdiSurveyActivity:: class.java.simpleName

    private lateinit var btnEndSurvey: TextView

    private lateinit var db : FirebaseFirestore
    private lateinit var globalData : PrefGlobal

    companion object {
        private lateinit var viewPager: ViewPager
        private lateinit var recyclerTabLayout: RecyclerTabLayout
        private lateinit var pagerAdapterBdi: BdiSurveyPagerAdapter
        lateinit var menuItemCancel : MenuItem

        var bdiSurveyQuestions: List<BdiSurveyItemModel> = ArrayList()
        var answers: LinkedHashMap<String, Int> = LinkedHashMap()
        var remaining = 0

        fun onClickStart(){
            viewPager.setCurrentItem(1, true)
        }

        fun onAnswered(question: String, answer: Int) {
            val isOld = answers.containsKey(question)
            answers.put(question, answer)

            Handler().postDelayed({
                if(!isOld) viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }, 250L)

            remaining = (bdiSurveyQuestions.size - 2) - answers.size
            if(remaining == 0){
                menuItemCancel.title = "Done"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bdi_survey)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
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

        btnEndSurvey.setOnClickListener {
            closeSurvey(remaining)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_survey, menu)

        menuItemCancel = menu!!.getItem(0)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_cancel ->{
                closeSurvey(remaining)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun closeSurvey(remaining: Int){
        var message = "Are you surely want to finish?"
        if(remaining > 0) message += " You haven't finished $remaining steps."

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Stop Survey")
                .setMessage(message)
                .setPositiveButton("YES"){dialog, which ->
                    if(remaining > 0) {
                        toast("Data isn't saved")
                    }
                    else {
                        val answersArr = answers.toSortedMap().values.toList()
                        if(answersArr.isEmpty()) toast("nothing to save")
                        else {
                            toast("Please wait while storing data")
                            storeDataToFirestore(answersArr)
                        }
                    }
                }
                .setNegativeButton(""){dialog, which ->
                    dialog.dismiss()
                }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun storeDataToFirestore(answers: List<Int>){
        val surveyData = HashMap<String, Any>()
        surveyData.put(AppConst.SUBJECT_ID, globalData.subjectId)
        surveyData.put(AppConst.CREATED_AT, System.currentTimeMillis())
        surveyData.put(AppConst.BDI_VERSION, globalData.bdiVersion)
        surveyData.put(AppConst.ANSWERS, answers)

        try {
            db.collection(AppConst.COLLECTION_BDI_SURVEY_DATA)
                    .add(surveyData)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id)
                        toast("data stored " + documentReference.id)

                        val intent = Intent(this, DebugActivity:: class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        toast(e.message.toString() + ". Please try again.")
                    }
        }catch (e : Exception){
            e.printStackTrace()
            toast(e.message.toString() + ". Please try again.")
        }
    }
}
