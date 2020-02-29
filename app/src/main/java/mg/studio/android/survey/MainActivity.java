package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> tmpSumData = new ArrayList<String>();
    private int iPage = 0;
    private int layers[] = {
            R.layout.welcome,
            R.layout.question_one,
            R.layout.question_two,
            R.layout.question_three,
            R.layout.question_four,
            R.layout.question_five,
            R.layout.question_six,
            R.layout.question_seven,
            R.layout.question_eight,
            R.layout.question_nine,
            R.layout.question_ten,
            R.layout.question_eleven,
            R.layout.question_twelve,
            R.layout.finish_survey
    };

    private boolean dataInputed() {

        switch (questionTypes[iPage]) {
            case 1:
                ViewGroup choices_1 = findViewById(R.id.choose);
                for (int i = 0; i < choices_1.getChildCount(); ++i) {
                    RadioButton but = (RadioButton) choices_1.getChildAt(i);
                    if (but.isChecked()) {
                        return true;
                    }
                }
                break;
            case -2:
                CheckBox enterSurvey=(CheckBox)findViewById(R.id.enterSurvey);
                if(enterSurvey.isChecked())
                    return true;
                break;
            case 2:
                ViewGroup choices_2 = findViewById(R.id.choose);
                for (int i = 0; i < choices_2.getChildCount(); ++i) {
                    CheckBox but = (CheckBox) choices_2.getChildAt(i);
                    if (but.isChecked()) {
                        return true;
                    }
                }
                break;
            case 3:
                EditText choices_edit = findViewById(R.id.choose_txt);
                if (choices_edit.getText().toString().length() > 0)
                    return true;
                break;
            default:
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iPage = 0;
        setContentView(R.layout.welcome);
    }

    Integer questionTypes[] = {-2, 1, 1, 1, 2, 2, 3, 1, 1, 1, 1, 1, 1, -1};

    public void gonext(View view) {
        if(!dataInputed())return;
        if (questionTypes[iPage] > 0) {
            String ret = "";
            ret += questionTypes[iPage].toString();
            ret += "Question " + Integer.toString(iPage) + ".";
            TextView choice_txt = findViewById(R.id.qContent);
            ret += choice_txt.getText().toString();
            tmpSumData.add(ret);

            switch (questionTypes[iPage]) {
                case 1:
                    ViewGroup choices_1 = findViewById(R.id.choose);
                    for (int i = 0; i < choices_1.getChildCount(); ++i) {
                        RadioButton but = (RadioButton) choices_1.getChildAt(i);
                        if (but.isChecked()) {
                            ret = "-";
                            ret += but.getText().toString();
                            tmpSumData.add(ret);
                        }
                    }
                    break;
                case 2:
                    ViewGroup choices_2 = findViewById(R.id.choose);
                    for (int i = 0; i < choices_2.getChildCount(); ++i) {
                        CheckBox but = (CheckBox) choices_2.getChildAt(i);
                        if (but.isChecked()) {
                            ret = "-";
                            ret += but.getText().toString();
                            tmpSumData.add(ret);
                        }
                    }
                    break;
                case 3:
                    EditText choices_edit = findViewById(R.id.choose_txt);
                    ret = "-";
                    ret += choices_edit.getText().toString();
                    tmpSumData.add(ret);
                    break;
                default:
                    break;
            }
        }
        iPage = iPage + 1;
        if (iPage == layers.length ) {
            Intent ittMainToSum = new Intent(this, sumActivity.class);
            ittMainToSum.putExtra("result", tmpSumData);
            startActivity(ittMainToSum);
        } else
            setContentView(layers[iPage]);

    }
}
