package mg.studio.android.survey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        zXingScannerView=new ZXingScannerView(this);
        ViewGroup qrFrame=findViewById(R.id.qr_frame);
        qrFrame.addView(zXingScannerView);
    }
    //scan again
    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.setAutoFocus(true);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCameraPreview();
        zXingScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        zXingScannerView.stopCameraPreview();
        zXingScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zXingScannerView.stopCameraPreview();
        zXingScannerView.stopCamera();
    }
//return the result of scanning
    @Override
    public void handleResult(Result result) {

        Intent intent=new Intent(ScanActivity.this,MainActivity.class);
        intent.putExtra("content",result.getText());
        Log.i("获得的数据", "handleResult: "+result.getText());
        this.setResult(2020,intent);
        finish();
    }
}
