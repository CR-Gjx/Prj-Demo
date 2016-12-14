package com.example.gcls.prj_demo;

        import java.net.ServerSocket;
        import java.util.Calendar;

        import android.app.Activity;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.SurfaceView;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;
        import  java.io.Serializable;
        import android.content.Intent;
        import android.app.Activity;

public class GravityActivity extends Activity implements SensorEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView textview4;
    private Button button1;
    private Button button2;
    String last_s = "停止";
    public  BlueTooth GraBlueTooth;
    private int start;
    private long lasttimestamp = 0;
    Calendar mCalendar;
    private SurfaceView surfaceView1;
    private ServerSocket serverSocket;
    private MainActivity main;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);
        textview4 = (TextView) findViewById(R.id.myTextView4);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                start = 1;
            }
        });

        button2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                start = 0;
            }
        });

        start = 0;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
        if (null == mSensorManager) {
            Log.d(TAG, "deveice not support SensorManager");
        }
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        Intent intent = getIntent();
        //main = (MainActivity)intent.getSerializableExtra("main");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];

            if (start == 0)
                x = y = z = 0;

            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis() / 1000l;
            if ((stamp - lasttimestamp) > 0) {
                lasttimestamp = stamp;
                String s = "";
                if(x > 3) {
                    s += "向左";
                    MainActivity.blueTooth.TurnLeft();
                }
                else if(x < -3) {
                    s += "向右";
                    MainActivity.blueTooth.TurnRight();
                }
                else if(y < -3) {
                    s += "前进";
                    MainActivity.blueTooth.Forward();
                }
                else if(y > 3) {
                    s += "后退";
                    MainActivity.blueTooth.Backward();
                }
                if(s == "") {
                    s = "停止";
                    MainActivity.blueTooth.sendInformation("5");
                }
                textview4.setText("当前状态:"+s);

                if (last_s==s)
                    return;
               /* if (s.compareTo("前进")==0)
                    blueTooth.sendInformation("1");
                if (s.compareTo("向左")==0){
                    }
                if (s.compareTo("向右")==0){
                    }
                if (s.compareTo("后退")==0)
                   ;
                if (s.compareTo("停止")==0)
                    */
                last_s = s;
            }


        }

    }
}
