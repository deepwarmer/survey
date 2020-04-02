from django.shortcuts import render
from django.template import loader
from django.http import HttpResponse
from django.shortcuts import get_object_or_404
from .models import *
import shutil
import wordcloud
import json
# Create your views here.
def create(request):
    return render(request,'survey/create.html')

def saveSurvey(request):
    request.encoding='utf-8'
    if request.method=="POST":
        message=request.POST['content']

        # return HttpResponse(json.dumps(objJson))
        objSurvey=clsSurvey()
        
        objSurvey.jsonContent=message
        objSurvey.save()

        objJson=json.loads(message)
        objJson['survey']['id']=str(objSurvey.id)
        message=json.dumps(objJson)

        objSurvey.jsonContent=message
        objSurvey.save()
        
        return HttpResponse("http://deepworm.xyz:8000/survey/getsurvey/"+str(objSurvey.id))
    else:
        return HttpResponse("no survey content!")
def getSurvey(request,survey_id):
    # objSurvey=clsSurvey.objects
    objSurvey=get_object_or_404(clsSurvey,id=survey_id)
    #直接返回文本
    return HttpResponse(objSurvey.jsonContent)

submit_sample_json='''
{
  "surveyId": 19,
  "length": 3,
  "data": [
    {
      "type": "radio",
      "question": "What's your gender?",
      "option": { "1": "male" }
    }, 
    {
      "type": "checkbox",
      "question": "What smartphone brands do you like?",
      "option": { "1": "Huawei", "2": "Xiao Mi" }
    },
    {
      "type": "text",
      "question": "What do you care aboud most when buying a smartphone?",
      "option": { "1": "appearance" }
    }
  ]
}
'''

def submitSurvey(request):
    request.encoding='utf-8'
    # if True:
        # result=json.loads(submit_sample_json)
    if request.method=="POST" and request.POST['content']:
    # if request.method=="POST" :
        # return HttpResponse("t1")
        result=json.loads(request.POST['content'])
        survey_id=result['surveyId']
        for question in result['data']:
            strType=question['type']
            if strType=='radio':
                objSingleQuestion=clsSingleQuestion.objects.filter(survey=survey_id,strDescription=question['question'])[0]
                objSingleOption=clsSingleOption.objects.filter(question=objSingleQuestion.id,strDescription=question['option']['1'])[0]
                objSingleOption.count=objSingleOption.count+1
                objSingleOption.save()
            elif strType=='checkbox':
                objCheckboxQuestion=clsCheckboxQuestion.objects.filter(survey=survey_id,strDescription=question['question'])[0]
                tmp={}
                for option in question['option'].values():
                    objCheckboxOption=clsCheckboxOption.objects.filter(question=objCheckboxQuestion.id,strDescription=option)[0]
                    objCheckboxOption.count =objCheckboxOption.count+1
                    objCheckboxOption.save()

            elif strType=='text':
                objTextQuestion=clsTextQuestion.objects.filter(survey=survey_id,strDescription=question['question'])[0]
                objTextAnswer=clsTextAnswer()
                objTextAnswer.strAnswer=question['option']['1']
                objTextAnswer.question=objTextQuestion
                objTextAnswer.save()
                
            else :return HttpResponse("Wrong question type.")
        return HttpResponse("success")
    else:
        return HttpResponse("You should POST survey result in content")
def getSurveyResult(survey_id):
    res={"id":survey_id}
    res['questions']=[]
    objSinglesQuestions=clsSingleQuestion.objects.filter(survey=survey_id)
    objCheckboxQuestions=clsCheckboxQuestion.objects.filter(survey=survey_id)
    objTextQuestions=clsTextQuestion.objects.filter(survey=survey_id)

    for question in objSinglesQuestions:
        strQuestion={"type":"single","id":str(question.id),"description":question.strDescription}
        strQuestion["options"]=[]
        objOptions=clsSingleOption.objects.filter(question=question.id)
        for option in objOptions:
            strQuestion["options"].append({"description":option.strDescription,"count":option.count})
        res["questions"].append(strQuestion)
    
    for question in objCheckboxQuestions:
        strQuestion={"type":"checkbox","id":str(question.id),"description":question.strDescription}
        strQuestion["options"]=[]
        objOptions=clsCheckboxOption.objects.filter(question=question.id)
        for option in objOptions:
            strQuestion["options"].append({"description":option.strDescription,"count":option.count})
        res["questions"].append(strQuestion)

    for question in objTextQuestions:
        strQuestion={"type":"text","id":str(question.id),"description":question.strDescription}
        strQuestion["answers"]=[]
        objAnswers=clsTextAnswer.objects.filter(question=question.id)
        for answer in objAnswers:
            strQuestion["answers"].append(answer.strAnswer)
        res["questions"].append(strQuestion)
        
        strTot=""
        for s in strQuestion['answers']:strTot+=s+' '
        wdcld=wordcloud.WordCloud(background_color="white")
        wdcld.generate(strTot)
        wdcld.to_file("wdcld_qid_"+str(question.id)+".png")
        shutil.move("wdcld_qid_"+str(question.id)+".png","survey/static/survey/imgs/wdcld_qid_"+str(question.id)+".png")

    res["questions"]=sorted(res['questions'],key=id)
    return json.dumps(res)
def showSurvey(request,survey_id):
    # return HttpResponse(getSurveyResult(survey_id))
    return render(request,"survey/show.html",{"result":getSurveyResult(survey_id)})