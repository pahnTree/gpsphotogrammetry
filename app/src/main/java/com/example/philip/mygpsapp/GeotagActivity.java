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

    // Only need two Coorinates to get the original Rectangular image
    // Using origin and topleft to make a diagonal, where the other ends can be found
    private Coordinates originalTopLeft;
    private Coordinates originalBottomLeft; // Same as origin

    // The new image can be skewed in every direction
    // Will not be a regular rectangular shape
    private Coordinates newTopLeft;
    private Coordinates newTopRight;
    private Coordinates newBottomLeft;
    private Coordinates newBottomRight;

    // Bottom left corner of the image will be the origin for all calulations

    public GeotagActivity(Context context, GPSTracker gps, SensorTracker sensor) {
        this.mContext = context;
        this.mGPS = gps;
        this.mSensor = sensor;
    }



    /**
     * Local class to store two coordinates to make a line
     */
    private class Line {
        private Coordinates p1, p2;
        private float magnitudePixel, magnitudeDistance;

        public Line(Coordinates p1, Coordinates p2) {
            this.p1 = p1;
            this.p2 = p2;
            this.magnitudeDistance = calculateMagnitude(1);
            this.magnitudePixel = calculateMagnitude(0);
        }

        /**
         * Calculates the magnitude of the distance between two points
         * @param c Either 0 for pixels or 1 for meters
         * @return
         */
        public float calculateMagnitude(int c) {
            if (c == 0) {
                int pixelX = Math.abs(p2.x - p1.x);
                int pixelY = Math.abs(p2.y - p1.y);
                return (int)Math.sqrt( Math.pow(pixelX, 2) + Math.pow(pixelY, 2) );
            } else {
                float distX = Math.abs(p2.distanceX - p1.distanceX);
                float distY = Math.abs(p2.distanceY - p1.distanceY);
                return (float)Math.sqrt( Math.pow(distY, 2) + Math.pow(distY, 2));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void calculateLocations() {

    }

    private void negativeRollCaluclations() {

    }

    private void positiveRollCalculations() {

    }

    private void negativePitchCalculations() {

    }

    private void positivePitchCalculations() {

    }

    public Coordinates getNewTopLeft() { return newTopLeft; }

    public Coordinates getNewTopRight() { return newTopRight; }

    public Coordinates getNewBottomLeft() { return newBottomLeft; }

    public Coordinates getNewBottomRight() { return newBottomRight; }


}
