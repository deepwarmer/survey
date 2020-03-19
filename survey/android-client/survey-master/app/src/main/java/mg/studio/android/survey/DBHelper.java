package mg.studio.android.survey;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "survey_answer", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // survey id  time / location / IMEI
        db.execSQL("create table AnswerManager(surveyId varchar(20), answerTime text, location text, IMEI varchar(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
