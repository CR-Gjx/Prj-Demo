package com.example.gcls.prj_demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector;
import android.util.Log;
import  android.view.MotionEvent;
import  android.view.GestureDetector.SimpleOnGestureListener;
import android.support.v4.app.FragmentActivity;
import android.view.View.OnTouchListener;

public class GestureActivity extends AppCompatActivity {

    int BeginFlag = 0;
    Button BeginGesture;
    Button StopGesture;
    private MyGestureListener mgListener;
    private GestureDetector mDetector;
    private GestureDetector gestureDetector;
    TextView GestureState;
    private int x = 0;
    private int y = 0;
    private final static int MIN_MOVE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GestureState = (TextView) findViewById(R.id.textView5);

        mgListener = new MyGestureListener();
        mDetector = new GestureDetector(this, mgListener);
        BeginGesture = (Button) findViewById(R.id.BeginGesture);
        StopGesture = (Button) findViewById(R.id.StopGesture);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        BeginGesture.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                BeginFlag = 1;
            }
        });
        StopGesture.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                BeginFlag = 0;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private class MyGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            //Log.d(TAG, "onDown:按下");
            return false;
        }

        //
        @Override
        public void onShowPress(MotionEvent motionEvent) {
            //Log.d(TAG, "onShowPress:手指按下一段时间,不过还没到长按");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            // Log.d(TAG, "onSingleTapUp:手指离开屏幕的一瞬间");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            // Log.d(TAG, "onScroll:在触摸屏上滑动");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            //Log.d(TAG, "onLongPress:长按并且没有松开");
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
            if(BeginFlag == 1) {
                GestureState.setText("停下");
                MainActivity.blueTooth.sendInformation("5");
            }
            else GestureState.setText("已停止，请点开始按钮");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            if(BeginFlag == 1) {
                if (motionEvent.getY() - motionEvent1.getY() > MIN_MOVE) {
                    GestureState.setText("向前");
                    MainActivity.blueTooth.Forward();
                    return true;
                } else if (motionEvent1.getY() - motionEvent.getY() > MIN_MOVE) {
                    GestureState.setText("向后");
                    MainActivity.blueTooth.Backward();
                    return true;
                }

                if (motionEvent1.getX() - motionEvent.getX() > 100) {
                    GestureState.setText("向右");
                    MainActivity.blueTooth.TurnRight();
                    return true;
                } else if (motionEvent.getX() - motionEvent1.getX() > 100) {
                    GestureState.setText("向左");
                    MainActivity.blueTooth.TurnLeft();
                    return true;
                }
            }
            else GestureState.setText("已停止，请点开始按钮");
            return true;
        }
    }
}






