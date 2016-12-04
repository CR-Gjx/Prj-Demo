package com.example.gcls.prj_demo;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CameraActivity extends AppCompatActivity {

    public static final int CAMERA_PORT = 8686;
    private ServerSocket cameraSocket;
    public static Handler handler;
    private Bitmap bitmap;
    public TextView tV;
    public Button btn;
    public ImageView myImage;
    private Matrix matrix = new Matrix();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tV = (TextView)this.findViewById(R.id.textView2);
        tV.setText("dsafds");
        btn= (Button)findViewById(R.id.button);
        myImage = (ImageView)findViewById(R.id.imageView);
        handler = new Handler(){
            public void handleMessage(Message msg){
                System.out.println(msg.arg1+1);
                if(bitmap!=null && msg.arg1 == 100)
                    myImage.setImageBitmap(bitmap);
               // super.handleMessage(msg);
            }
        };

        ReceiveVideo videoDisplayer = new ReceiveVideo();
        videoDisplayer.start();
        //videoDisplayer.run();
    }

    class ReceiveVideo extends Thread{

        private int length = 0;
        private int num = 0;
        private byte[] buffer = new byte[2048];
        private byte[] data = new byte[204800];

        @Override
        public void run(){
            tV.setText("InHere2");
            try{
                //Log.e("video ", "video thread");
                System.out.println("服务器准备启动");
                cameraSocket = new ServerSocket(CAMERA_PORT);
                System.out.println("服务器启动");
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
                                System.out.println("得到输入");
                                System.arraycopy(buffer,0,data,num,length);
                                num += length;
                            }
                        }while(length >= 0);

                        new CameraActivity.setImageThread(data,num).start();
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
            matrix.setRotate(-90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
            //bitmap = RotateBitmap(bitmap, 90);
          //  myImage.setImageBitmap(bitmap);
            System.out.println("图片输出"+bitmap.toString());
            Message msg=new Message();
            msg.arg1 = 100;
           // msg.obj = bitmap;
            handler.sendMessage(msg);
        }
    }

}
