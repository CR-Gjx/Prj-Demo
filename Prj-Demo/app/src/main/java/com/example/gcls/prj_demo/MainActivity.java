package com.example.gcls.prj_demo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.ComponentName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity implements Serializable {
    public Button ButtonBtConnect;
    public Intent IntentBtConnect;
    public static BluetoothSocket CarSocket;
    static public BlueTooth blueTooth;
    public Button forwardBt;
    public Button backwardBt;
    public Button leftBt;
    public Button rightBt;
    public Button videoBt;
    public Button gvtBt;
    public Button speechBt;
    public MainActivity main;
    public TextView tV;
    public static final int CAMERA_PORT = 8686;
    private ServerSocket cameraSocket;
    public static  Handler handler;
    private Bitmap bitmap;

//    private ServiceConnection conn = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            System.out.println("------Service DisConnected-------");
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            System.out.println("------Service Connected-------");
//        }
//    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blueTooth = new BlueTooth(this);
        blueTooth.start();
        ButtonBtConnect = (Button) findViewById(R.id.btnBtConnect);
        WifiInfo infowifi=  ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo();
        main = this;
        tV = (TextView)findViewById(R.id.info);
        int Ip = infowifi.getIpAddress();
        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
        tV.setText(strIp.toString());




        ButtonBtConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public  void onClick(View v)
            {
            }
        }
        );
        forwardBt = (Button)findViewById(R.id.forward);
        leftBt = (Button)findViewById(R.id.Left);
        rightBt = (Button)findViewById(R.id.Right);
        backwardBt = (Button)findViewById(R.id.Backward);
        videoBt = (Button)findViewById(R.id.btnVideo);
        gvtBt = (Button)findViewById(R.id.btngravity);
        speechBt = (Button)findViewById(R.id.btnSpeech);

        forwardBt.setOnClickListener(new Button.OnClickListener(){ // 点击forward按钮，蓝牙传输信息
            public  void onClick(View v){
                blueTooth.Forward();
            }
        });

        leftBt.setOnClickListener(new Button.OnClickListener(){ // 点击forward按钮，蓝牙传输信息
            public  void onClick(View v){
                blueTooth.TurnLeft();

            }});

        rightBt.setOnClickListener(new Button.OnClickListener(){ // 点击forward按钮，蓝牙传输信息
            public  void onClick(View v){
                blueTooth.TurnRight();

            }});

        backwardBt.setOnClickListener(new Button.OnClickListener(){ // 点击forward按钮，蓝牙传输信息
            public  void onClick(View v){
                blueTooth.Backward();

    }});

        videoBt.setOnClickListener(new Button.OnClickListener(){
            public  void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });

        gvtBt.setOnClickListener(new Button.OnClickListener(){
            public  void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,GravityActivity.class);
                //intent.putExtra("main", main);
                startActivity(intent);
            }
        });

        speechBt.setOnClickListener(new Button.OnClickListener(){
            public  void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SpeechActivity.class);
                //intent.putExtra("blueTooth", blueTooth);
                startActivity(intent);
            }
        });



    }



    class ReceiveVideo extends Thread{

        private int length = 0;
        private int num = 0;
        private byte[] buffer = new byte[2048];
        private byte[] data = new byte[204800];

        @Override
        public void run(){
            try{
                //Log.e("video ", "video thread");
                cameraSocket = new ServerSocket(CAMERA_PORT);
                while(true){
                    //Log.e("video ", "video thread");
                    Socket socket = cameraSocket.accept();
                    //Log.e("video ", "video accept");
                    try{
                        InputStream input = socket.getInputStream();
                        num = 0;
                        do{
                            length = input.read(buffer);
                            if(length >= 0){
                                System.arraycopy(buffer,0,data,num,length);
                                num += length;
                            }
                        }while(length >= 0);

                        new setImageThread(data,num).start();
                        input.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        socket.close();
                    }
                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    class setImageThread extends Thread{

        private byte[]data;
        private int num;
        public setImageThread(byte[] data, int num){
            this.data = data;
            this.num = num;
        }

        @Override
        public void run(){
            bitmap = BitmapFactory.decodeByteArray(data, 0, num);
            //bitmap = RotateBitmap(bitmap, 90);
            Message msg=new Message();
            msg.arg1 = 100;
            handler.sendMessage(msg);
        }
    }
}


