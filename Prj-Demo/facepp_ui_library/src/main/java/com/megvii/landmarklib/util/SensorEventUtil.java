package com.megvii.landmarklib.util;

import com.megvii.landmarklib.OpenglActivity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorEventUtil implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private OpenglActivity opActivity;

	public SensorEventUtil(OpenglActivity opActivity) {
        this.opActivity = opActivity;
		mSensorManager = (SensorManager) opActivity.getSystemService(opActivity.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
		// 参数三，检测的精准度
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == null) {
			return;
		}

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0] / 10.0f;
			float y = event.values[1] / 10.0f;
			float z = event.values[2] / 10.0f;
			opActivity.getSensor(x, y, z);
		}
	}
}
