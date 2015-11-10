package com.example.philip.mygpsapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Phil on 11/9/2015.
 */
public class GeotagActivity extends Activity {
    private Context mContext;
    private GPSTracker mGPS;
    private SensorTracker mSensor;
    //private DroneTracker mDrone;
    // Camera is locked in portrait mode
    private float gpsCoordinates; // Decimal with 5 trailing 0.00001
    // The Field of View is obtained manually
    // Differs from phone to phone
    private final float cameraFieldOfViewHorizontal = 22.5f; // Obtain with actual measurements
    private final float cameraFieldOfViewVertical = 30.0f;
    private float altitude;
    private float azimuth;
    private float pitch;
    private float roll;
    // Bottom left corner of the image will be the origin for all calulations

    public GeotagActivity(Context context, GPSTracker gps, SensorTracker sensor) {
        this.mContext = context;
        this.mGPS = gps;
        this.mSensor = sensor;

    }

    public class Coordinates {
        int x, y; // The pixel locations
        float distanceX, distanceY; // The distances away in meters

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Coordinates(float x, float y) {
            this.distanceX = x;
            this.distanceY = y;
        }

        public Coordinates(int x, int y, float distanceX, float distanceY) {
            this.x = x;
            this.y = y;
            this.distanceX = distanceX;
            this.distanceY = distanceY;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void calculationLocations() {

    }

    private void negativeRollCaluclations() {

    }

    private void positiveRollCalculations() {

    }

    private void negativePitchCalculations() {

    }

    private void positivePitchCalculations() {

    }


}
