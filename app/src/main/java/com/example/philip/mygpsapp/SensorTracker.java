package com.example.philip.mygpsapp;

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


/**
 * Created by Phil on 11/6/2015.
 */
public class SensorTracker extends Activity implements SensorEventListener {
    private final Context mContext;
    private SensorManager mSensorManager;
    private Sensor gyro;
    private Sensor accel;
    private Sensor compass;

    private boolean gyroscopeIsAvailable;
    private boolean accelerometerIsAvailable;
    private boolean magneticFieldIsAvailable;

    private float azimuthAvg; // Angle from magnetic north
    private float pitchAvg; // When in portrait, tilting phone towards face
    private float rollAvg; // When in portrait, tilting phone side to side

    private float azimuthTotal;
    private float pitchTotal;
    private float rollTotal;

    private float[] azimuthValue = new float[3];
    private float[] pitchValue = new float[3];
    private float[] rollValue = new float[3];

    private float[] accelValues = new float[3];
    private float[] compassValues = new float[3];
    private float[] inR = new float[9];
    private float[] inclineMatrix = new float[9];
    private float[] prefValues = new float[3];
    private int counter = 0;



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
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.d("Magnetic Field", "Obtained values");
            for (int i = 0; i < 3; i++) {
                compassValues[i] = event.values[i];
            }
        }
        boolean success = mSensorManager.getRotationMatrix(inR, inclineMatrix, accelValues, compassValues);
        if (success) {
            // Got a good rotation matrix
            Log.d("Rotation matrix", "Success");
            mSensorManager.getOrientation(inR, prefValues); // Loads the matrix into prefValues
            doUpdate();
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
        azimuthAvg = (float) Math.toDegrees(prefValues[0] + 13);
        pitchAvg = -1*(float) Math.toDegrees(prefValues[1]);
        rollAvg = (float) Math.toDegrees(prefValues[2]);
    }

    public float getAzimuth() {
        return azimuthAvg;
    }

    public float getPitch() {
        return pitchAvg;
    }

    public float getRoll() {
        return rollAvg;
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
