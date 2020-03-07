package com.examle.survey_with_kt
import org.json.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

fun JsonToQuestion(json:JSONObject):baseQuestion
{
    val strType=json.get("type").toString()

    when(strType)
    {
        "single"->
        {
            val ret=singleOptionQuestion();
            ret.strType=strType
            ret.strQuestionDescription=json.getString("question")
            val ar=  json.getJSONArray("options");
            for(i in 1..ar.length())
            {
                ret.strOptions+=ar.getJSONObject(i-1).getString(i.toString())
            }
            return ret;
        }
        "multiple"->
        {
            val ret=multipleOptionQuestion();
            ret.strType=strType
            ret.strQuestionDescription=json.getString("question")
            val ar=  json.getJSONArray("options");
            for(i in 1..ar.length())
            {
                ret.strOptions+=ar.getJSONObject(i-1).getString(i.toString())
            }
            return ret;
        }
        "text"->
        {
            val ret=textQuestion();
            ret.strType=strType
            ret.strQuestionDescription=json.getString("question")
            return ret;
        }
    }
    //TODO throw what?
    throw
    return baseQuestion();
}

open class baseQuestion {
    lateinit var strType: String;
    lateinit var strQuestionDescription: String;
    open fun makeReport(id:Int):String{return "Question "+id.toString()+": "+strQuestionDescription+"\n";}
}

class singleOptionQuestion : baseQuestion() {
    var strOptions=emptyArray<String>();
    var bOptions=emptyArray<Boolean>();
    override fun makeReport(id: Int): String {
        var ret=super.makeReport(id)
        for(i in 0..strOptions.count()-1)if(bOptions[i])
        {
            ret+='\t'+strOptions[i]+'\n'
        }
        return ret
    }
}

class multipleOptionQuestion : baseQuestion() {
    var strOptions=emptyArray<String>();
    var bOptions=emptyArray<Boolean>();
    override fun makeReport(id: Int): String {
        var ret=super.makeReport(id)
        for(i in 0..strOptions.count()-1)if(bOptions[i])
        {
            ret+='\t'+strOptions[i]+'\n'
        }
        return ret
    }
}

class textQuestion : baseQuestion() {
    lateinit var strAnswer:String;
    override fun makeReport(id: Int): String {
        return super.makeReport(id)+"\t"+strAnswer+'\n';
    }
}