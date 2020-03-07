package com.examle.survey_with_kt

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.finish_survey.*
import kotlinx.android.synthetic.main.question.*
import kotlinx.android.synthetic.main.summary.*
import kotlinx.android.synthetic.main.welcome.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

//MainActivity , okay
class MainActivity : AppCompatActivity() {

    lateinit var jsonSurvey: org.json.JSONObject;
    var strJsonSurvey =
        """
    {"survey": {"id": "12344134", "len": "2","questions": [{"type":"text","question":"What do you think of the professors?"},{"type": "multiple","question": "How well do the professors teach at this university?","options": [{ "1": "Extremely well" }, { "2": "Very well" }]},{"type": "single","question": "How effective is the teaching outside yur major at the univesrity?","options": [{ "1": "Extremetly effective" },{ "2": "Very effective" },{ "3": "Somewhat effective" },{ "4": "Not so effective" },{ "5": "Not at all effective" }]}]}}
        
    """
    lateinit var jsonResult: org.json.JSONObject;

    //array of questions
    var arQuestion = emptyArray<baseQuestion>();
    var nSurveyId = 0;
    //number of questions
    var nPageCnt = 0;
    //-1: welcome,0~nPageCnt-1:question,nPageCnt:summary,nPageCnt+1:finish
    var nPageId = -1;

    //return whether the button should be corresponded
    private fun okToGoNext(): Boolean {
        when (nPageId) {
            //Welcome
            -1 -> return CheckWelcomeAccept.isChecked();
            //Summary or Finish
            arQuestion.count(), arQuestion.count() + 1 -> return true;
            //Quesiton
            else -> {
                when (arQuestion[nPageId].strType) {
                    "single" -> {
                        for (i in 0..RgpQuestion.childCount - 1) {
                            val radio = RgpQuestion.getChildAt(i) as RadioButton
                            if (radio.isChecked()) return true;
                        }
                    }
                    "multiple" -> {
                        for (i in 0..RgpQuestion.childCount - 1) {
                            val radio = RgpQuestion.getChildAt(i) as CheckBox
                            if (radio.isChecked()) return true;
                        }
                    }
                    "text" -> {
                        return EditQuestion.getText().toString().isNotEmpty();
                    }
                }
            }
        }
        return false;
    }

    //Load arQuestion from survey.json or strJsonSurvey
    fun initQuestion() {
        val dir = getExternalFilesDir("SurveyResult")!!.absoluteFile
        val file = File(dir, "survey.json")
        //Load from survey.json if it exists
        if (file.exists()) {
            var fin = FileInputStream(file);
            strJsonSurvey = fin.readBytes().toString(Charsets.UTF_8);
            fin.close();
        }
        jsonSurvey = org.json.JSONObject(strJsonSurvey)
        //Load from json
        nPageCnt = (jsonSurvey.get("survey") as JSONObject).getString("len").toInt()
        nSurveyId = (jsonSurvey.get("survey") as JSONObject).getString("id").toInt()
        val jsonQuesionArray = jsonSurvey.getJSONObject("survey").getJSONArray("questions");
        for (i in 0..jsonQuesionArray.length() - 1) {
            arQuestion += JsonToQuestion(jsonQuesionArray.getJSONObject(i))
        }
    }

    //Dynamically generate the layout view with a question in arQuestion
    fun showQuestion() {
        RgpQuestion.removeAllViews()
        EditQuestion.setVisibility(View.INVISIBLE)
        val strType = arQuestion[nPageId].strType
        TxtQuestionTitle.setText("Question " + (nPageId + 1).toString())
        TxtQuesionDescription.setText(arQuestion[nPageId].strQuestionDescription)

        if (strType == "single") {
            val question = arQuestion[nPageId] as singleOptionQuestion;
            for (i in 0..question.strOptions.count() - 1) {
                val radio = RadioButton(this)
                RgpQuestion.addView(radio)
                radio.setText(question.strOptions[i])
            }
        } else if (strType == "multiple") {
            val question = arQuestion[nPageId] as multipleOptionQuestion;
            for (i in 0..question.strOptions.count() - 1) {
                val check = CheckBox(this)
                RgpQuestion.addView(check)
                check.setText(question.strOptions[i])
            }
        } else if (strType == "text") {
            EditQuestion.setVisibility(View.VISIBLE)
        }
    }

    //Function for BtnWelcome,BtnQuestion,BtnSummary,BtnFinish
    fun btnNext() {
        if (!okToGoNext()) return;
        //To deal with things in this page
        when (nPageId) {
            //TODO
            //nPageId=nPageId is just a stuff
            //It may be replaced later
            //It's here because I don't know how to do it
            -1, arQuestion.count(), arQuestion.count() + 1 -> nPageId = nPageId;
            else -> {
                //Record Answers to Question in arQuestion
                when (arQuestion[nPageId].strType) {
                    "single" -> {
                        var question = arQuestion[nPageId] as singleOptionQuestion;
                        for (i in 0..RgpQuestion.childCount - 1) {
                            val radio = RgpQuestion.getChildAt(i) as RadioButton
                            question.bOptions += radio.isChecked();
                        }
                    }
                    "multiple" -> {
                        var question = arQuestion[nPageId] as multipleOptionQuestion;
                        for (i in 0..RgpQuestion.childCount - 1) {
                            val check = RgpQuestion.getChildAt(i) as CheckBox
                            question.bOptions += check.isChecked();
                        }
                    }
                    "text" -> {
                        var question = arQuestion[nPageId] as textQuestion;
                        question.strAnswer = EditQuestion.getText().toString();
                    }
                }
            }
        }
        ++nPageId;
        //deal with things in the new page
        when (nPageId) {
            arQuestion.count() -> {
                setContentView(R.layout.summary)
                btnSummaryNext.setOnClickListener { btnNext(); }
                // make summary
                var strSummaryRecords = ""
                for (i in 0..arQuestion.count() - 1)
                    strSummaryRecords += arQuestion[i].makeReport(i + 1)
                TxtSummaryRecords.setText(strSummaryRecords);
            }
            arQuestion.count() + 1 -> {
                setContentView(R.layout.finish_survey)
                BtnFinish.setOnClickListener { btnNext() }
            }
            arQuestion.count() + 2 -> {
                // generate json object
                jsonResult = jsonSurvey
                var jsonRecords = JSONArray();
                for (i in 0..arQuestion.count() - 1)
                    jsonRecords.put(arQuestion[i].makeJson())
                jsonResult.getJSONObject("survey").put("result", jsonRecords);

                val dir = getExternalFilesDir("SurveyResult")!!.absoluteFile
                val file = File(dir, "results.json")

                //if results.json doesn't exist ,create and initialize it.
                if (!file.exists()) {
                    file.createNewFile();
                    val fout = FileOutputStream(file, false)
                    fout.write("[]".toByteArray())
                    fout.close()
                }


                val fin = FileInputStream(file)
                val jsonTotalResult = JSONArray(fin.readBytes().toString(Charsets.UTF_8))
                fin.close()
                val fout = FileOutputStream(file, false)
                jsonTotalResult.put(jsonResult)
                fout.write(jsonTotalResult.toString(4).toByteArray())
                fout.close()

                //Exit Point


                android.os.Process.killProcess(android.os.Process.myPid());
//                finish()
//                onDestroy()
            }
            else -> {
                if (nPageId == 0) {
                    setContentView(R.layout.question)
                    BtnQuestionNext.setOnClickListener { btnNext() }
                }
                showQuestion()
            };
        }
    }

    //onCreate, okay
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)
        initQuestion()
        BtnWelcomeStart.setOnClickListener { btnNext() }
    }

}
