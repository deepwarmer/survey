package com.examle.survey_with_kt
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.question.*
import kotlinx.android.synthetic.main.question.TxtQuestionTitle
import kotlinx.android.synthetic.main.summary.*
import kotlinx.android.synthetic.main.welcome.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var jsonSurvey:org.json.JSONObject;
    var strJsonSurvey=
    """
    {"survey":{"id":"12344134","len":"2","questions":[{"type":"multiple","question":"How well do the professors teach at this university?","options":[{"1":"Extremely well"},{"2":"Very well"}]},{"type":"single","question":"How effective is the teaching outside yur major at the univesrity?","options":[{"1":"Extremetly effective"},{"2":"Very effective"},{"3":"Somewhat effective"},{"4":"Not so effective"},{"5":"Not at all effective"}]}]}} 
    """

    lateinit var jsonResult:org.json.JSONObject;
    var strJsonResult="";

    var arQuestion= emptyArray<baseQuestion>();
    var nSurveyId=0;
    var nPageCnt=0;
    var nPageId=-1;

    private fun okToGoNext():Boolean {
        when(nPageId)
        {
            -1-> return CheckWelcomeAccept.isChecked();
            arQuestion.count(),arQuestion.count()+1->return true;
            else ->
            {
                when (arQuestion[nPageId].strType) {
                    "single" -> {
                        for(i in 0..RgpQuestion.childCount-1)
                        {
                            val radio=RgpQuestion.getChildAt(i) as RadioButton
                            if(radio.isChecked())return true;
                        }
                    }
                    "multiple" -> {
                        for(i in 0..RgpQuestion.childCount-1)
                        {
                            val radio=RgpQuestion.getChildAt(i) as CheckBox
                            if(radio.isChecked())return true;
                        }
                    }
                    "text" -> {

                    }
                }
            }
        }
        return false;
    }
    fun initQuestion()
    {
        jsonSurvey=org.json.JSONObject(strJsonSurvey)
        var tmp= jsonSurvey.get("survey") as JSONObject
        nPageCnt=(jsonSurvey.get("survey")as JSONObject).getString("len").toInt()
        nSurveyId=(jsonSurvey.get("survey")as JSONObject).getString("id").toInt()
        val jsonQuesionArray =jsonSurvey.getJSONObject("survey").getJSONArray("questions");
        for(i in 0..jsonQuesionArray.length()-1)
        {
            arQuestion+=JsonToQuestion(jsonQuesionArray.getJSONObject(i))
        }
    }
    fun showQuestion()
    {
        RgpQuestion.removeAllViews()
        val strType =arQuestion[nPageId].strType
        if(strType=="single")
        {
            val question =arQuestion[nPageId] as singleOptionQuestion;
            TxtQuestionTitle.setText("Question "+(nPageId+1).toString())
            TxtQuesionDescription.setText(question.strQuestionDescription)
            for(i in 0..question.strOptions.count()-1)
            {
                val radio=RadioButton(this)
                RgpQuestion.addView(radio)
                radio.setText(question.strOptions[i])
            }
        }
        else if(strType=="multiple")
        {
            val question =arQuestion[nPageId] as multipleOptionQuestion;
            TxtQuestionTitle.setText("Question "+(nPageId+1).toString())
            TxtQuesionDescription.setText(question.strQuestionDescription)
            for(i in 0..question.strOptions.count()-1)
            {
                val check=CheckBox(this)
                RgpQuestion.addView(check)
                check.setText(question.strOptions[i])
            }
        }

    }
    fun btnNext()
    {
        if(!okToGoNext())return;
        when(nPageId)
        {
            -1, arQuestion.count(),arQuestion.count()+1->nPageId=nPageId;
            else ->
            {
                when (arQuestion[nPageId].strType) {
                    "single" -> {
                        var question=arQuestion[nPageId] as singleOptionQuestion;
                        for(i in 0..RgpQuestion.childCount-1)
                        {
                            val radio=RgpQuestion.getChildAt(i) as RadioButton
                            question.bOptions+=radio.isChecked();
                        }
                    }
                    "multiple" -> {
                        var question=arQuestion[nPageId] as multipleOptionQuestion;
                        for(i in 0..RgpQuestion.childCount-1)
                        {
                            val check=RgpQuestion.getChildAt(i) as CheckBox
                            question.bOptions+=check.isChecked();
                        }
                    }
                    "text" -> {
                    }
                }
            }
        }
        ++nPageId;
        when(nPageId)
        {
            arQuestion.count()->
            {
                setContentView(R.layout.summary)
                btnSummaryNext.setOnClickListener { btnNext(); }
                var strSummaryRecords=""
                for(i in 0..arQuestion.count()-1)
                    strSummaryRecords+=arQuestion[i].makeReport(i+1)
                TxtSummaryRecords.setText(strSummaryRecords);
            }
            arQuestion.count()+1->
            {

                setContentView(R.layout.finish_survey)
            }
            arQuestion.count()+2->
            {
                onDestroy()
            }
            else->
            {
                if(nPageId==0)
                {
                    setContentView(R.layout.question)
                    BtnQuestionNext.setOnClickListener { btnNext() }
                }
                showQuestion()
            };
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome)
        initQuestion()

        BtnWelcomeStart.setOnClickListener { btnNext() }
    }

}
