package com.example.tattata.mylight;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button buttonOnOff;
    private ImageView imageView;
    private CameraManager mCameraManager;
    private CameraManager.TorchCallback mTorchCallback;
    private String mCameraID;
    private boolean isOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOnOff = (Button)findViewById(R.id.buttonOnOff);
        imageView = (ImageView)findViewById(R.id.imageView);

        mCameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        mTorchCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                mCameraID = cameraId;
                isOn = enabled;
            }
        };
        mCameraManager.registerTorchCallback(mTorchCallback, null);

        buttonOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnOffMessage(!isOn);
                torchToggle();
            }
        });
    }
    private void torchToggle() {
        if(mCameraID == null) {
            return;
        }
        try {
            mCameraManager.setTorchMode(mCameraID, !isOn);
        } catch (CameraAccessException e) {
            Toast.makeText(getApplicationContext(), "カメラにアクセスできませんね", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private void setOnOffMessage(boolean boo) {
        if(boo) {
            imageView.setImageResource(R.drawable.light_on);
        } else {
            imageView.setImageResource(R.drawable.light_off);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            mCameraManager.setTorchMode(mCameraID, false);
        } catch (CameraAccessException e) {}
        mCameraManager.unregisterTorchCallback(mTorchCallback);
    }
}
