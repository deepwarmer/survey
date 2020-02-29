package mg.studio.android.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;

import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class sumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);
        ArrayList<String> tmpData = (ArrayList<String>) getIntent().getSerializableExtra("result");
        LinearLayout sumLayer = findViewById(R.id.sumLayout);
        for (int i = 0; i < tmpData.size(); ++i) {
            String now = tmpData.get(i);
            TextView txt = new TextView(this);
            switch (now.charAt(0)) {
                case '-':
                    txt.setTextSize(12);
                    txt.setGravity(android.view.Gravity.CENTER);
                default:
                    txt.setTextSize(20);
                    now = now.charAt(0) + "\n" + now.substring(1, now.length());
            }
            now = now.substring(1, now.length());

            txt.setText(now);
            sumLayer.addView(txt);
        }
        saveTo();
    }

    // Unique request code.
    private static final int WRITE_REQUEST_CODE = 43;

    // use one file to reacord all survey reasults:
    public void saveTo() {

        //get that data
        ArrayList<String> tmpData = (ArrayList<String>) getIntent().getSerializableExtra("result");

        //Gson is not used beacause I'm trying to edit json file by myself
        {
            File dir = getExternalFilesDir("SurveyResult").getAbsoluteFile();
            File file = new File(dir, "survey.json");

            //create the file
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    FileOutputStream fout = new FileOutputStream(file, true);
                    fout.write("[".getBytes());
                    fout.close();
                }
                //edit json file to get ready for next item
                else{
                    FileInputStream fin = new FileInputStream(file);
                    byte[] buffer = new byte[fin.available()];
                    fin.read(buffer);
                    String toWrite = new String(buffer);
                    toWrite=toWrite.substring(0,toWrite.length()-1);
                    fin.close();

                    FileOutputStream fout = new FileOutputStream(file, false);
                    fout.write(toWrite.getBytes());
                    fout.close();
                }
            } catch (Exception e) {
                //Todo log
            }

            // start to log
            try {
                FileOutputStream fout = new FileOutputStream(file, true);
                fout.write("{".getBytes());
                boolean isBegin=true;
                for(Integer i=0;i<tmpData.size();i++)
                {
                    if(tmpData.get(i).charAt(0)!='-'){
                        if(isBegin){
                            fout.write(('"'+ tmpData.get(i)+"\":[").getBytes());
                        }else{
                            fout.write(("],").getBytes());
                            fout.write(('"'+ tmpData.get(i)+"\":[").getBytes());
                        }
                    }else{
                        fout.write(('"'+tmpData.get(i)+"\",").getBytes());
                    }
                    isBegin=false;
                }
                fout.write(("]},]").getBytes());
                fout.close();
            } catch (Exception e) {
            }
        }
    }
}
