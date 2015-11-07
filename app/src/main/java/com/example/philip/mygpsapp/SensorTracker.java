package com.example.philip.mygpsapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.WindowManager;

import static android.hardware.Sensor.TYPE_ORIENTATION;

/**
 * Created by Phil on 11/6/2015.
 */
public class SensorTracker extends Activity implements SensorEventListener {
    private final Context mContext;
    private SensorManager mSensorManager;
    private Sensor accel;
    private Sensor compass;
    private Sensor orientation;

    private float azimuth; // Angle from magnetic north
    private float pitch; // When in portrait, tilting phone towards face
    private float roll; // When in portrait, tilting phone side to side

    private float[] accelValues = new float[3];
    private float[] compassValues = new float[3];
    private float[] inR = new float[9];
    private float[] inclineMatrix = new float[9];
    private float[] orientationValues = new float[3];
    private float[] prefValues = new float[3];

    private float mInclination;
    private int counter;
    private int mRotation;

    public SensorTracker(Context context) {
        mContext = context;
        getAngles();
    }

    public void getAngles() {
        try {
            mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
            accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            WindowManager window = (WindowManager) this.getSystemService(WINDOW_SERVICE);
            int apiLevel = Integer.parseInt(Build.VERSION.SDK);
            if (apiLevel < 8) {
                mRotation = window.getDefaultDisplay().getOrientation();
            } else {
                mRotation = window.getDefaultDisplay().getRotation();
            }
            // -------------------------------------------

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
