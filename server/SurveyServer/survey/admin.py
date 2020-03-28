from django.contrib import admin
from .models import *
# Register your models here.


class SurveyAdmin(admin.ModelAdmin):
    pass


admin.site.register(clsSurvey,SurveyAdmin)
admin.site.register(clsSingleQuestion,SurveyAdmin)
admin.site.register(clsCheckboxQuestion,SurveyAdmin)
admin.site.register(clsSingleOption,SurveyAdmin)
admin.site.register(clsCheckboxOption,SurveyAdmin)
admin.site.register(clsTextQuestion,SurveyAdmin)
admin.site.register(clsTextAnswer,SurveyAdmin)