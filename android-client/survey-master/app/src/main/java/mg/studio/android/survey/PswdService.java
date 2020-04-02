package mg.studio.android.survey;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PswdService extends Service {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;
    private Handler handler;
    public PswdService() {
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setInitialPassword();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setInitialPassword() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O) {
            mLayoutParams.type=WindowManager.LayoutParams.TYPE_PHONE;
        }else{
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        // mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        mLayoutParams.format = PixelFormat.RGBA_8888;
//        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mView = LayoutInflater.from(this).inflate(R.layout.set_pswd, null);
        mView.setFocusableInTouchMode(true);
        mView.findViewById(R.id.set_unlock_pswd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Display display = getWindowManager().getDefaultDisplay();
//                int height = display.getHeight();

                String pswd = ((EditText)mView.findViewById(R.id.fill_pswd)).getText().toString();
                String cpswd = ((EditText)mView.findViewById(R.id.cf_pswd)).getText().toString();
                if(pswd.length()==0){
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    R.string.empty_pswd, Toast.LENGTH_LONG).show();
                            TextView hint=mView.findViewById(R.id.pswd_worn);
                            hint.setText("Please enter your password");
                        }

                    });
                    return;
                }else if(cpswd.length()==0){
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    R.string.empty_cf_pswd, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();

                            TextView hint=mView.findViewById(R.id.pswd_worn);
                            hint.setText("Please confirm your password");
                        }
                    });
                    return;
                }else if (!pswd.equals(cpswd)){
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.cf_fail,
                                    Toast.LENGTH_LONG).show();
                            TextView hint=mView.findViewById(R.id.pswd_worn);
                            hint.setText("Passwords not match");
                        }
                    });
                    return;
                }
                SharedPreferences sp = getSharedPreferences("pswd",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("spswd",pswd);
                editor.commit();
                // stop service
                onDestroy();
            }
        });
        mWindowManager.addView(mView,mLayoutParams);
    }

    // stop service
    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mView);
    }
}
