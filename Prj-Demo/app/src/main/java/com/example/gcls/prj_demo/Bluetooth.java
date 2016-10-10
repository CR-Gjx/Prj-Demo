package com.example.gcls.prj_demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import java.io.IOException;
import java.util.UUID;


public class BlueTooth extends Thread {

    private BluetoothAdapter adapter;
    private BluetoothSocket CarSocket;
    private boolean CarSocketSuccess;
    private TextView bluetoothText;
    private TextView Testtext;
    private LinearLayout bluetoothList;
    private MainActivity main;
    private UUID uuid;
    //private Button goButton;
    public Button refreshButton;
   // private boolean flagforrefresh=false;

    public BlueTooth(MainActivity main) {
        this.main = main;

       // Log.e("", "Refreshing");
        bluetoothList = (LinearLayout)main.findViewById(R.id.BluetoothList);
        bluetoothText = (TextView) main.findViewById(R.id.info);
        //bluetoothText.setText("blueText");
        //refreshButton = (Button)main.findViewById(R.id.refresh);
        refreshButton = new Button(main);
        refreshButton.setText("搜索已有蓝牙设备");
        bluetoothList.addView(refreshButton);
        Testtext = (TextView)main.findViewById(R.id.testText);
        Testtext.setText("success!");
        Testtext.setText("step2");

        refreshButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                refreshBluetoothDevice();
            }
        });
    }

    public void show(){
        Testtext.setText("Ido");
    }



    @Override
    public void run() {

    }

    public void refreshBluetoothDevice() {
        Testtext.setText("step5");
        if (adapter==null) adapter = BluetoothAdapter.getDefaultAdapter();//获取默认蓝牙适配器
        if (adapter != null) {		//若获取设备成功

            if (!adapter.isEnabled()) {			//如果手机蓝牙未开启
                bluetoothText.setText(" 请打开本机的蓝牙设备 ");			//text中显示提示信息
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                main.startActivity(enableBtIntent);
                return;
            }
            bluetoothList.removeAllViews();				//清空配对设备列表
            bluetoothList.addView(refreshButton);
            bluetoothText.setText(" 正在搜索周围的蓝牙设备");
            for (BluetoothDevice device : adapter.getBondedDevices()) {    //按配对设备循环操作，为每个设备创建一个按钮，当按钮被按下时，创建相应对象执行操作
                Testtext.setText("create button");
                Button temp = new Button(main);
                temp.setText(device.getName());
                temp.setTextColor(Color.BLACK);
                temp.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                bluetoothList.addView(temp);
                temp.setOnClickListener(new BluetoothDeviceListener(device));
            }
        } else {
            bluetoothText.setText(" 未能找到蓝牙设备！");
        }
    }

    private  class BluetoothDeviceListener implements View.OnClickListener {    //按下某个配对设备按钮后生成此对象，并执行下列操作
        BluetoothDevice device;

        BluetoothDeviceListener(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void onClick(View arg0) {
            try {							//尝试连接
                if (CarSocket != null)		//如果已经连接，则关闭连接通道
                    CarSocket.close();
                CarSocketSuccess=false;

                uuid = device.getUuids()[1].getUuid();//getUuids()返回已配备的设备UUID序列（很多个设备的UUID数组）
                CarSocket = device.createRfcommSocketToServiceRecord(uuid);//通过uuid识别连接
                bluetoothText.setText(" 连接"+device.getName());
                adapter.cancelDiscovery();  //取消蓝牙设备扫描
                //bluetoothText.setText(device.getName());
                CarSocket.connect();		//进行连接
                //bluetoothText.setText(device.getName());
                CarSocketSuccess=true;

                bluetoothText.setText(" 连接成功：" + device.getName());
                bluetoothList.removeAllViews();		//清空配对设备列表
                bluetoothList.addView(refreshButton);
                return;
            } catch (IOException e) {		//异常
                e.printStackTrace();
            }
            bluetoothText.setText(" 连接"+device.getName()+"失败");
        }
    }



    public void sendInformation(String information) {
        if (CarSocket == null) {
            bluetoothText.setText(" 连接失败");
            return;
        }
        try {
            //发送信息
            CarSocket.getOutputStream().write(String.valueOf(information).getBytes());
            CarSocket.getOutputStream().flush(); //刷新输出缓冲区
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        bluetoothText.setText(" 发送信息失败");
    }
}
