package mg.studio.android.survey;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


public class LockService extends Service {
    private WindowManager wManager;
    private WindowManager.LayoutParams lParams;
    private View view;
    private Handler handler;
    public LockService()
    {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLockView();
        return super.onStartCommand(intent, flags, startId);
    }

    // set lock
    private void startLockView(){
        if (ReportActivity.bStart) return;
        wManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        lParams = new WindowManager.LayoutParams();
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O) {
            lParams.type=WindowManager.LayoutParams.TYPE_PHONE;
        }else{
            lParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
//        lParams.format = PixelFormat.RGBA_8888;
//        lParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        lParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        lParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        lParams.x = 0;
        lParams.y = 0;
        view = LayoutInflater.from(this).inflate(R.layout.exit_lock, null);
        view.setFocusableInTouchMode(true);
        view.findViewById(R.id.unlock_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password =
                        ((EditText)view.findViewById(R.id.unlock_pswd)).getText().toString();
                SharedPreferences sp =
                        getSharedPreferences("pswd",MODE_PRIVATE);
                String sPswd = sp.getString("spswd","");
                // if password is right, unlock
                if (password.equals(sPswd)){
                    onDestroy();
                }else{
                    handler=new Handler(Looper.getMainLooper());
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getApplicationContext(), "Wrong Password",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        wManager.addView(view, lParams);
        ReportActivity.bStart = true;
    }

     // unlock, stop lock service
    @Override
    public void onDestroy() {
        super.onDestroy();
        ReportActivity.bStart = false;
        wManager.removeView(view);
        ReportActivity.reportActivity.finish();
        MainActivity.mainActivity.finish();
    }
}
