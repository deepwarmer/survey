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

    # elif 'content' in request.GET and request.GET['content']:
    #     message=request.content
    #     objSurvey=clsSurvey()
    #     objSurvey.jsonContent=message
    #     objSurvey.save()
    #     return HttpResponse(objSurvey.id)
    else:
        return HttpResponse("no survey content!")
def getSurvey(request,survey_id):
    # objSurvey=clsSurvey.objects()
    objSurvey=get_object_or_404(clsSurvey,id=survey_id)
    #直接返回文本
    return HttpResponse(objSurvey.jsonContent)
    