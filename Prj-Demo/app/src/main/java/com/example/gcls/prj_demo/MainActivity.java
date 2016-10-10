package com.example.gcls.prj_demo;

import android.content.Context;
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
import java.net.ServerSocket;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    public Button ButtonBtConnect;
    public Intent IntentBtConnect;
    public static BluetoothSocket CarSocket;
    public BlueTooth blueTooth;
    public Button forwardBt;


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
        ButtonBtConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public  void onClick(View v)
            {
            }
        }
        );
        forwardBt = (Button)findViewById(R.id.forward);
        forwardBt.setOnClickListener(new Button.OnClickListener(){ // 点击forward按钮，蓝牙传输信息
           public  void onClick(View v){
               blueTooth.sendInformation("forward");
           }
        });

    }

}
