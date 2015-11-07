package com.example.philip.mygpsapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import static android.hardware.Sensor.TYPE_ORIENTATION;

/**
 * Created by Phil on 11/6/2015.
 */
public class SensorTracker extends Activity implements SensorEventListener {
    private final Context mContext;
    private SensorManager mSensorManager;
    private Sensor gyro;
    private Sensor accel;
    private Sensor compass;
    private Sensor orientation;

    private float azimuth; // Angle from magnetic north
    private float pitch; // When in portrait, tilting phone towards face
    private float roll; // When in portrait, tilting phone side to side

    private boolean ready = false;
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
        this.mContext = context;
        getAngles();
    }

    public void getAngles() {
        try {
            mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

            gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            // Acceleration on device
            accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // Finds magnetic north (May be uncalibrated)
            compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            // Gives Azimuth [0], Pitch [1], Roll [2]
            orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            WindowManager window = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
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
        // Need to get both accelerometer and compass
        // before determine orientationValues
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER: {
                for (int i = 0; i < 3; i++) {
                    accelValues[i] = event.values[i];
                }
                if (compassValues[0] != 0) {
                    ready = true;
                }
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD: {
                for (int i = 0; i < 3; i++) {
                    compassValues[i] = event.values[i];
                }
                if (accelValues[2] != 0) {
                    ready = true;
                }
                break;
            }
            case Sensor.TYPE_ORIENTATION: {
                for (int i = 0; i < 3; i++) {
                    orientationValues[i] = event.values[i];
                }
                break;
            }
        }
        if (!ready) {
            return;
        }
        if (mSensorManager.getRotationMatrix(inR, inclineMatrix, accelValues, compassValues)) {
            // Got a good rotation matrix
            mSensorManager.getOrientation(inR, prefValues);
            mInclination = mSensorManager.getInclination(inclineMatrix);
            // Display every 10th value
            if (counter++ % 10 == 0) {
                doUpdate(null);
                counter = 1;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void doUpdate(View view) {
        if (!ready) {
            return;
        }
        azimuth = (float) Math.toDegrees(orientationValues[0]);
        if (azimuth < 0) {
            azimuth += 360.0f;
        }
        pitch = (float) Math.toDegrees(orientationValues[1]);
        roll = (float) Math.toDegrees(orientationValues[2]);
    }

    public float getAzimuth() {
        return azimuth;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }
}
