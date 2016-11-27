package com.example.gcls.car_socket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.SurfaceView;
import android.telephony.TelephonyManager;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public String clientIP;
    public boolean isFacing = false;
    private SurfaceView surfaceView = null;
    public VideoThread videoThread = null;
    public  Button CameraButton = null;
    TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CameraButton = (Button)findViewById(R.id.Begin);
        testText = (TextView)findViewById(R.id.test);
//        CameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        videoThread = new VideoThread(this);
        videoThread.start();
    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    protected static String getImei(Context context) {
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }
}
