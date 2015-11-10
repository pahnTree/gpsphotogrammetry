package com.example.philip.mygpsapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Phil on 11/9/2015.
 */
public class GeotagActivity extends Activity {
    // Camera is locked in portrait mode
    private float gpsCoordinates; // Decimal with 5 trailing 0.00001
    private final float cameraFieldOfViewHorizontal = 22.5f; // Obtain with actual measurements
    private final float cameraFieldOfViewVertical = 30.0f;
    private float altitude;
    private float azimuth;
    private float pitch;
    private float roll;
    // Bottom left corner of the image will be the origin for all calulations

    public GeotagActivity(GPSTracker gps, SensorTracker sensor) {


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
