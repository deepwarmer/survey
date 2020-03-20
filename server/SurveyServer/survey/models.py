from django.db import models

# Create your models here.

from django.db.models import Model
class clsSurvey(models.Model):
    jsonContent=models.CharField(max_length=1000000000)
