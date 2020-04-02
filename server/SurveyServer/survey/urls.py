from django.urls import path
from . import views
urlpatterns=[
    # page to create survey
    path('create',views.create,name='create'),
    # http get this url to get survey.json 
    path('getsurvey/<int:survey_id>',views.getSurvey,name='getSurvey'),
    # post your survey.json here to create a survey,it returns a url including id
    path('savesurvey',views.saveSurvey,name='saveSurvey'),
    # submit your survey result here
    path('submitsurvey',views.submitSurvey,name='submitSurvey'),
    #you can see your survey result here
    path('showsurvey/<int:survey_id>',views.showSurvey,name='showSurvey'),
]
