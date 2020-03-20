from django.urls import path
from . import views
urlpatterns=[
    path('create',views.create,name='create'),
    path('getsurvey/<int:survey_id>',views.getSurvey,name='getSurvey'),
    path('savesurvey',views.saveSurvey,name='saveSurvey')
]
