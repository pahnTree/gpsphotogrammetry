package com.example.philip.mygpsapp.services;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
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

    private boolean gyroscopeIsAvailable;
    private boolean accelerometerIsAvailable;
    private boolean magneticFieldIsAvailable;

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
    private int counter = 0;
    private int mRotation;

    public SensorTracker(Context context) {
        this.mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        getAngles();
    }

    public void getAngles() {
        try {
            getSensorManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSensorManagers() {
        if (mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
            Log.d("Gyroscope enabled", "Gyroscope enabled");
            gyroscopeIsAvailable = true;
            gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        // Acceleration on device
        if (mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) {
            Log.d("Accelerometer enabled", "Accelerometer enabled");
            accelerometerIsAvailable = true;
            accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Finds magnetic north (May be uncalibrated)
        if (mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
            Log.d("Magnetic Field enabled", "Magnetic Field enabled");
            magneticFieldIsAvailable = true;
            compass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    public void startSensors() {
        mSensorManager.registerListener(this, gyro, mSensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accel, mSensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, compass, mSensorManager.SENSOR_DELAY_UI);
    }

    public void stopSensors() {
        mSensorManager.unregisterListener(this, gyro);
        mSensorManager.unregisterListener(this, accel);
        mSensorManager.unregisterListener(this, compass);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Need to get both accelerometer and compass
        // before determine orientationValues
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            Log.d("Accelerometer", "Obtained values");
            for (int i = 0; i < 3; i++) {
                accelValues[i] = event.values[i];
            }
            ready = (compassValues[2] != 0) ? true : false;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.d("Magnetic Field", "Obtained values");
            for (int i = 0; i < 3; i++) {
                compassValues[i] = event.values[i];
            }
            ready = (accelValues[2] != 0) ? true : false;
        }
        /* legacy code
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            for (int i = 0; i < 3; i++) {
                orientationValues[i] = event.values[i];
            }
        }
        */

        /*
        if (!ready) {
            return;
        }
        */
        boolean success = mSensorManager.getRotationMatrix(inR, inclineMatrix, accelValues, compassValues);
        if (success) {
            // Got a good rotation matrix
            Log.d("Rotation matrix", "Success");
            mSensorManager.getOrientation(inR, prefValues); // Loads the matrix into prefValues
            doUpdate();
            mInclination = mSensorManager.getInclination(inclineMatrix);
            // Display every 10th value
            if (counter++ % 10 == 0) {
                doUpdate();
                counter = 1;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void doUpdate() {
        azimuth = (float) prefValues[0];
        azimuth = (float) Math.toDegrees(azimuth);
        pitch = (float) Math.toDegrees(prefValues[1]);
        roll = (float) Math.toDegrees(prefValues[2]);
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

    public boolean getGyroscopeIsAvailable() {
        return gyroscopeIsAvailable;
    }

    public boolean getAccelerometerIsAvailable() {
        return accelerometerIsAvailable;
    }

    public boolean getMagneticFieldIsAvailable() {
        return magneticFieldIsAvailable;
    }
}
