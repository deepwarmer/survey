# readme for survey application

## For lab2

Android Survey core project for a primary design practice using Android Studio.

陈潇涵 email_address:120512900@qq.com  
张义 email_address:deepworm@qq.com  
王丹豪 email_address:wang904183923@163.com  
郭俊帅 email_address:conquerfate9@gmail.com  
岑秋兰 email_address:1303214164@qq.com

## For coding activity 8 and mini

### members

| Name(En) |  CQU ID  |             rep             | job  |
| :------: | :------: | :-------------------------: | :--: |
|    DW    | 20171714 | https://deepworm.xyz/survey | TODO |
|   Dean   | 20171744 |            TOTO             | TODO |

### Requirements/Platform

Android 10 or above

### Installation

install android-client/build/survey.apk

### Usage

#### For survey creators

- from web: open deepworm.xyz:8000 in browser, create the survey, click the button and you will get a QR Code. Let your surveyed scan the QR Code with our android client.
- from android: this should be updated later(TODO:)
  For the surveyed:

#### For the surveyed

You need to download and install android-client/survey.apk, scan the QR Code given and finish the survey

### How Does it work

The survey creator should create a survey at [create page](deepworm.xyz:8000/survey/create) or at android client(same as the surveyed). And a QR Code will be given, which contains a url to get survey data. Then the surveyed should install [survey.apk](./android-client/survey.apk) and scan the QR Code. After finishing the survey, the surveyed should click the "Submit" button and the survey result will be uploaded.  
If the survey was created at android client, the creator will be given a url with the QR Code. The url is important because you will need it to see the statistical result. You can see your survey result at any time by visit that url in your browser.

### Completed functions

#### Password to keep safe

When the android client is install, you can initialize a password to protect your android client. When anyone finished the survey, he will need the password to exit the android app.

#### Create new questionnaire easily

You don't need to be a profession to create a survey -- everyone can. You only need install our app or visit our website to create a survey.

#### Scan QR code to load questionnaires

The surveyed can scan a QR Code to load any survey.

### Uncompleted functions

#### Create survey at android Client

You can create your survey at the android client, too.

#### View your survey result in web page

We automatically statistics and display visualized results at a webpage. So you can see your survey result after delivering your survey.

### API

#### deepworm.xyz:8000/getsurvey/<int:surveyid>

Get this url, and the server will return the survey json string.

#### deepworm.xyz:8000/savesurvey

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

#### deepworm.xyz:8000/submitsurvey/<int:surveyid>

Post your survey result json string here.  
The Sample result json string will be uploaded later.
