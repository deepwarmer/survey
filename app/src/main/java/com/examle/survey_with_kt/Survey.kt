package com.examle.survey_with_kt
import org.json.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//Load a detailed question from a json object
fun JsonToQuestion(json:JSONObject):baseQuestion
{
    val strType=json.get("type").toString()

    // ret is the question to return
    // you should read it with strJsonSurvey
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
    // use return baseQuestion() to avoid syntax error
    // This should be fixed after further study
    throw
    return baseQuestion();
}

open class baseQuestion {
    lateinit var strType: String;
    lateinit var strQuestionDescription: String;
    open fun makeReport(id:Int):String{return "Question "+id.toString()+": "+strQuestionDescription+"\n";}
    open fun makeJson():JSONObject{
        var json=JSONObject();
        json.put("type",strType)
        json.put("question",strQuestionDescription);
        return json
    }
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

    override fun makeJson(): JSONObject {
        var json = super.makeJson()
        var answers=JSONArray();
        for(i in 0..bOptions.count()-1)if(bOptions[i])
        {
            var answer=JSONObject();answer.put((i+1).toString(),strOptions[i])
            answers.put(answer)
        }
        json.put("answers",answers)
        return json
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
    override fun makeJson(): JSONObject {
        val json = super.makeJson()
        val answers=JSONArray();
        for(i in 0..bOptions.count()-1)if(bOptions[i])
        {
            val answer=JSONObject();answer.put((i+1).toString(),strOptions[i])
            answers.put(answer)
        }
        json.put("answers",answers)
        return json
    }
}

class textQuestion : baseQuestion() {
    lateinit var strAnswer:String;
    override fun makeReport(id: Int): String {
        return super.makeReport(id)+"\t"+strAnswer+'\n';
    }

    override fun makeJson(): JSONObject {
        val json = super.makeJson()
        json.put("answer",strAnswer)
        return json
    }
}