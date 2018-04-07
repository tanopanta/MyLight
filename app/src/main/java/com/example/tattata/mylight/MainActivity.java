package com.example.tattata.mylight;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button buttonOnOff;
    private ImageView imageView;
    private CameraManager mCameraManager;
    private CameraManager.TorchCallback mTorchCallback;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private String mCameraID;
    private boolean isOn;

    private static final int NOTIFICATION_INTENT_ID = 112313;
    private static final int INTENT_REQUEST_CODE = 32242;
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
                setNotification(!isOn);
                torchToggle();
            }
        });

        //通知欄に状態を表示
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(), INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("MyLight")
                .setContentText("点灯中")
                .setContentIntent(contentIntent)
                .build();
        mNotification.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
            imageView.setColorFilter(Color.rgb(222, 210, 64));//yellow
        } else {
            imageView.setColorFilter(Color.rgb(48, 48, 48));//gray
        }
    }
    private void setNotification(boolean boo) {
        if(boo) {
            //点灯時
            mNotificationManager.notify(NOTIFICATION_INTENT_ID, mNotification);
        } else {
            //消灯時
            mNotificationManager.cancel(NOTIFICATION_INTENT_ID);
        }
    }


    @Override
    public void onDestroy(){
        //タスク管理から消した場合よばれない
        super.onDestroy();
        try {
            mCameraManager.setTorchMode(mCameraID, false);
        } catch (CameraAccessException e) {}
        mCameraManager.unregisterTorchCallback(mTorchCallback);
        mNotificationManager.cancel(NOTIFICATION_INTENT_ID);
    }
}
