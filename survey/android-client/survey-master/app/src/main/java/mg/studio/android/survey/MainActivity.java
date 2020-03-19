package mg.studio.android.survey;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private CheckBox mCbAccept;
    private JSONArray questions;
    private JSONObject[] answers;
    static AppCompatActivity mainActivity;
    private String text;
    private int qNum = 0; // number of questions
    private int qSeq = 0; // sequence
    private String surveyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        ImageView imgScan;
        imgScan = findViewById(R.id.img_scan);
        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQRCode();
            }
        });
        mCbAccept = (CheckBox) findViewById(R.id.cb_accept);
        mainActivity = this;
    }

    // get camera permission
    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, 110);
        }
    }

    // scan qr code
    private void scanQRCode() {
        getCameraPermission();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    // get text from qr code
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @androidx.annotation.Nullable Intent data) {
        if (requestCode == 0) {
            super.onActivityResult(requestCode, resultCode, data);
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                startService(new Intent(this, PswdService.class));
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                text = scanResult.getContents();
                Toast.makeText(getApplicationContext(), R.string.get_data, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.get_no_data, Toast.LENGTH_LONG).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // analyse the question text
    @Nullable
    private JSONArray GetQuestions() {
        try {
            if (text == null || text.length() == 0) return null;
            JSONObject json = new JSONObject(text);
            JSONObject survey = json.getJSONObject("survey");
            // save survey id
            surveyId = survey.getString("id");
            // return question json object list
            return survey.getJSONArray("questions");
        } catch (JSONException je) {
            return null;
        }
    }

    // if the type is single, the app will load a layout
    // to display the single choice question
    public void setSingleLayout(JSONObject ques,
                                int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_single);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
        // get options
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        RadioGroup rGroup = findViewById(R.id.options);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        // add the options into the ViewGroup
        for (int i = 0; i < size; i++) {
            optionText[i] = ((JSONObject) jArray.get(i)).getString(String.valueOf(i + 1));
            RadioButton option = new RadioButton(this);
            option.setText(optionText[i]);
            option.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.font_size_average));
            option.setPadding(0, 5, 0, 5);
            rGroup.addView(option, lp);
        }
    }

    // if the type is multiple, the app will load a layout
    // to display the multiple choice question
    public void setMultipleLayout(JSONObject ques,
                                  int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_multiple);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
        // get the options
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        LinearLayout lLayout = findViewById(R.id.options);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // add the options to the ViewGroup
        for (int i = 0; i < size; i++) {
            optionText[i] = ((JSONObject) jArray.get(i)).getString(String.valueOf(i + 1));
            CheckBox cb = new CheckBox(this);
            cb.setText(optionText[i]);
            cb.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.font_size_average));
            cb.setPadding(0, 5, 0, 5);
            lLayout.addView(cb, lp);
        }
    }

    // if the type is fill, the app will load a layout
    // to display the fill-blank-question
    public void setFillLayout(JSONObject ques,
                              int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.question_fill);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.question)).setText(ques.getString("question"));
    }

    // the app will check whether the user agrees to
    // our requirements or not, then load the question layout
    public void onClickGo(View view) {
        checkInitialPswd();
        // initial question list
        if (text == null || text.length() == 0) {
            Toast.makeText(this, R.string.without_data,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        questions = GetQuestions();
        if (questions == null) {
            qNum = 0;
            answers = null;
        } else {
            qNum = questions.length();
            answers = new JSONObject[qNum];
        }
        qSeq = 0;
        if (mCbAccept.isChecked()) {
            goNextPage();
        } else {
            Toast.makeText(this, R.string.accept_requests,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the single choice question layout
    public void onClickSingleNext(View view) {
        RadioGroup rGroup = findViewById(R.id.options);
        // get the checked radiobutton
        int checkedId = rGroup.getCheckedRadioButtonId();
        if (checkedId > 0) {
            try {
                String answer = ((RadioButton) findViewById(checkedId)).getText().toString();
                Log.i("onClickSingleNext", answer);
                JSONObject jQuestion = new JSONObject();
                jQuestion.put("type", "radio");
                TextView question = findViewById(R.id.question);
                jQuestion.put("question", question.getText().toString());
                JSONObject jOption = new JSONObject();
                jOption.put("1", answer);
                jQuestion.put("answer", jOption);
                answers[qSeq - 1] = jQuestion;
                goNextPage(); // load next question
            } catch (JSONException je) {
                return;
            }
        } else {
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the multiple choice question layout
    public void onClickMultipleNext(View view) {
        LinearLayout lLayout = findViewById(R.id.options);
        int n = lLayout.getChildCount();
        int x = 0;
        try {
            JSONObject jQuestion = new JSONObject();
            jQuestion.put("type", "checkbox");
            TextView view1 = findViewById(R.id.question);
            jQuestion.put("question", view1.getText().toString());
            JSONArray jAnswer = new JSONArray();
            // check every checkbox
            for (int i = 0; i < n; i++) {
                CheckBox cb = (CheckBox) lLayout.getChildAt(i);
                if (cb.isChecked()) {
                    String answer = cb.getText().toString();
                    JSONObject jOption = new JSONObject();
                    jOption.put(String.valueOf(x + 1), answer);
                    jAnswer.put(jOption);
//                    Log.i("onClickMultipleNext", answer);
                    x++;
                }
            }
            jQuestion.put("answer", jAnswer);
            answers[qSeq - 1] = jQuestion;
        } catch (JSONException je) {
            return;
        }
        if (x > 0) {
            goNextPage(); // load next question
        } else {
            // if no one option was selected!
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the fill-blank-question layout
    public void onClickFillNext(View view) {
        EditText editText = findViewById(R.id.answer_text);
        String answer = editText.getText().toString();
        if (answer.length() != 0) {
            try {
                JSONObject jQuestion = new JSONObject();
                jQuestion.put("type", "text");
                TextView view1 = findViewById(R.id.question);
                jQuestion.put("question", view1.getText().toString());
                JSONObject jText = new JSONObject();
                jText.put("1", answer);
                jQuestion.put("answer", jText);
                answers[qSeq - 1] = jQuestion;
                goNextPage();
            } catch (JSONException je) {
                return;
            }
        } else {
            Toast.makeText(this, R.string.empty_answer,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // check the type of next question and then load it
    private void goNextPage() {
        try {
            if (qSeq < qNum) {
                JSONObject question = ((JSONObject) questions.get(qSeq++));
                String type = question.getString("type");
                if (type.equals("radio")) {
                    setSingleLayout(question, R.string.single);
                } else if (type.equals("checkbox")) {
                    setMultipleLayout(question, R.string.multiple);
                } else if (type.equals("text")) {
                    setFillLayout(question, R.string.fill);
                }
            } else {
                setContentView(R.layout.finish_survey);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    // start report activity
    public void onClickReport(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < qNum; i++)
            jsonArray.put(answers[i]);
        intent.putExtra("data", jsonArray.toString());
        intent.putExtra("length", qNum);
        intent.putExtra("surveyId", surveyId);
        startActivity(intent);
    }

    // set a password to unlock
    private void checkInitialPswd() {
        SharedPreferences sp = getSharedPreferences("pswd", MODE_PRIVATE);
        if (sp.getString("spswd", "").length() != 0) return;

        if (!Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(this, PswdService.class));
        }
    }
}
