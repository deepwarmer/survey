package mg.studio.android.survey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
public class MainActivity extends AppCompatActivity {

    int iPage=0;
    int layers[]={
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iPage=0;
        setContentView(R.layout.welcome);
    }
    public void gonext(View view){
        iPage=iPage+1;
        if(iPage==layers.length)
            onDestroy();
        setContentView(layers[iPage]);
    }
}
