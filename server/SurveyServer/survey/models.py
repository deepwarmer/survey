from django.db import models

# Create your models here.

from django.db.models import Model
class clsSurvey(models.Model):
    jsonContent=models.CharField(max_length=1000000000)
    nLen=models.IntegerField(default=0)
class clsSingleQuestion(models.Model):
    survey=models.ForeignKey(clsSurvey,on_delete=models.CASCADE)
    strDescription=models.CharField(max_length=1000000000)
class clsCheckboxQuestion(models.Model):
    survey=models.ForeignKey(clsSurvey,on_delete=models.CASCADE)
    strDescription=models.CharField(max_length=1000000000)
class clsTextQuestion(models.Model):
    survey=models.ForeignKey(clsSurvey,on_delete=models.CASCADE)
    strDescription=models.CharField(max_length=1000000000)
class clsTextAnswer(models.Model):
    question=models.ForeignKey(clsTextQuestion,on_delete=models.CASCADE)
    strAnswer=models.CharField(max_length=1000000000)
class clsCheckboxOption(models.Model):
    question=models.ForeignKey(clsCheckboxQuestion,on_delete=models.CASCADE)
    # count how many times it has been choose
    count=models.IntegerField(default=0)
    strDescription=models.CharField(max_length=1000000000)
    
class clsSingleOption(models.Model):
    question=models.ForeignKey(clsSingleQuestion,on_delete=models.CASCADE)
    # times it has been choose
    count=models.IntegerField(default=0)
    strDescription=models.CharField(max_length=1000000000)

