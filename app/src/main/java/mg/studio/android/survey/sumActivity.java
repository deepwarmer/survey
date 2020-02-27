package mg.studio.android.survey;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}
