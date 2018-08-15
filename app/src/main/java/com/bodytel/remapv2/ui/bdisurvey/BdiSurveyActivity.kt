package com.bodytel.remapv2.ui.bdisurvey

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.bodytel.remapv2.R
import com.bodytel.remapv2.data.local.surveyitem.SurveyItemModel
import com.bodytel.remapv2.ui.debug.DebugActivity
import com.bodytel.util.helper.QuizHelper
import com.nshmura.recyclertablayout.RecyclerTabLayout
import kotlinx.android.synthetic.main.activity_survey.*

class BdiSurveyActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapterBdi: BdiSurveyPagerAdapter
    private lateinit var recyclerTabLayout: RecyclerTabLayout
    private lateinit var btnEndQuiz: TextView

    private var quizzes: List<SurveyItemModel> = ArrayList()

    companion object {
        var answers: HashMap<String, String> = HashMap()

        fun onAnswered(question: String, answer: String) {
            answers.put(question, answer)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerTabLayout = findViewById(R.id.recycler_tab_layout)
        viewPager = findViewById(R.id.view_pager)
        btnEndQuiz = findViewById(R.id.btn_end_quiz)

        answers.clear()
        quizzes = QuizHelper.getQuizzesFromJson("survey_1.json", this)
        pagerAdapterBdi = BdiSurveyPagerAdapter(supportFragmentManager, quizzes)
        viewPager.adapter = pagerAdapterBdi
        recyclerTabLayout.setUpWithViewPager(viewPager)
        //viewPager.currentItem = pagerAdapterBdi.count / 2

        btnEndQuiz.setOnClickListener {
            closeQuiz(quizzes.size - answers.size)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_quiz, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_skip ->{
                closeQuiz(quizzes.size - answers.size)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun closeQuiz(remaining: Int){
        var message = "Are you surely want to finish?"
        if(remaining > 0) message += " You haven't answered $remaining quizzes."

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Stop Quiz")
                .setMessage(message)
                .setPositiveButton("YES"){dialog, which ->
                    val intent = Intent(this, DebugActivity:: class.java)
                    intent.putExtra("args", answers)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton(""){dialog, which ->
                    dialog.dismiss()
                }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
