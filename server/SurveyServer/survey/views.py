from django.shortcuts import render
from django.template import loader
from django.http import HttpResponse
from django.shortcuts import get_object_or_404
from .models import *
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

def submitSurvey(request,survey_id):
    result=json.loads(request.POST['content'])
    surveyid=1
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
    res["questions"]=sorted(res['questions'],key=id)
    return json.dumps(res)
def showSurvey(request,survey_id):
    return HttpResponse(getSurveyResult(survey_id))