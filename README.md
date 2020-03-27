b2

Android Survey core project for a primary design practice using Android Studio.

陈潇涵 email_address:120512900@qq.com
张义 email_address:deepworm@qq.com
王丹豪 email_address:wang904183923@163.com
郭俊帅 email_address:conquerfate9@gmail.com
岑秋兰 email_address:1303214164@qq.com

# For coding activity 8

Android API TODO: or above

## Requirements/Platform

## Installation

intall android-client/build/survey.apk

## Usage

For survey creators:  
For the surveyed:

## How Does it work

## API

### deepworm.xyz:8000/getsurvey/<int:surveyid>

Get this url, and the server will return the survey json string.

### deepworm.xyz:8000/savesurvey

Post your survey json string to here. The json string should be in "content" field.  
Then the server will return you a Integer which means the survey Id.

Sample:  
the survey json string:

```json
{
  "survey": {
    "len": "3",
    "questions": [
      {
        "type": "radio",
        "question": "请输入单选描述",
        "options": [
          {
            "1": "选项1"
          },
          {
            "2": "选项2"
          }
        ]
      },
      {
        "type": "checkbox",
        "question": "请输入多选描述",
        "options": [
          {
            "1": "选项1"
          },
          {
            "2": "选项2"
          },
          {
            "3": "选项3"
          }
        ]
      },
      {
        "type": "text",
        "question": "请输入文本框描述"
      }
    ]
  }
}
```

### deepworm.xyz:8000/submitsurvey/<int:surveyid>

Post your survey result json string here.  
The Sample result json string will be uploaded later.

