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
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    public String clientIP = "df";
    public boolean isFacing = false;
    private SurfaceView surfaceView = null;
    public VideoThread videoThread = null;
    public  Button CameraButton = null;
    TextView testText;
    EditText editText;
    static  String IPAddress = "";
    MainActivity Main;
    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CameraButton = (Button)findViewById(R.id.Begin);
        Main = this;
        //testText = (TextView)findViewById(R.id.test);
        editText = (EditText)findViewById(R.id.editText);
        CameraButton.setOnClickListener(new Button.OnClickListener(){ // 点击forward按钮，蓝牙传输信息
            public  void onClick(View v){
                IPAddress = editText.getText().toString();
                BeginCamera();
            }
        });

//        videoThread = new VideoThread(Main);
//        videoThread.start();



    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    public void BeginCamera()
    {
        videoThread = new VideoThread(Main);
        videoThread.start();
        System.out.println(IPAddress);
    }

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
