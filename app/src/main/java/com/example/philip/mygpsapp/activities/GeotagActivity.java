package com.example.philip.mygpsapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.example.philip.mygpsapp.services.Coordinates;
import com.example.philip.mygpsapp.services.GPSTracker;
import com.example.philip.mygpsapp.services.SensorTracker;


/**
 * Created by Phil on 11/9/2015.
 */
public class GeotagActivity extends Activity {
    private Context mContext;
    private GPSTracker mGPS;
    private SensorTracker mSensor;
    //private DroneTracker mDrone;
    // Camera is locked in portrait mode
    private double gpsLatitude; // Decimal with 5 trailing 0.00001
    private double gpsLongitude;
    private double altitude;
    // The Field of View is obtained manually
    // Differs from phone to phone
    // These values are for when the phone is in portrait mode
    // Horizontal is the short side, vertical is the long side
    private final float cameraFieldOfViewHorizontal = 18.33f; // Obtain with actual measurements
    private final float cameraFieldOfViewVertical = 30.96f;
    private float azimuth;
    private float pitch;
    private float roll;

    private final int HORIZONTAL_PIXELS = 3096;
    private final int VERTICAL_PIXELS = 4128;

    // Only need two Coorinates to get the original Rectangular image
    // Using origin and topleft to make a diagonal, where the other ends can be found
    private Coordinates originalTopLeft; // Whatever Megapixel size we choose in portrait mode
    private Coordinates originalTopRight;
    private Coordinates originalBottomLeft;
    private Coordinates originalBottomRight;
    //  ____
    // |  / |
    // | /  |
    // |/___|
    //
    private Line originalDiagonal;
    private Coordinates gpsCoordinates;

    // The new image can be skewed in every direction
    // Will not be a regular rectangular shape
    private Coordinates newTopLeft;
    private Coordinates newTopRight;
    private Coordinates newBottomLeft;
    private Coordinates newBottomRight;
    // When doing the transform, one of the corners (The closest to camera) will remain
    // the image origin for the new image
    private boolean isLeftOrigin;
    private boolean isTopOrigin;
    // Bottom left corner of the image will be the origin for all calulations

    public GeotagActivity(Context context, GPSTracker gps, SensorTracker sensor) {
        this.mContext = context;
        this.mGPS = gps;
        this.mSensor = sensor;
        loadData();
        findOrigin();
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

    public void loadData() {
        this.azimuth = mSensor.getAzimuth();
        this.pitch = mSensor.getPitch();
        this.roll = mSensor.getRoll();
        this.gpsLatitude = mGPS.getLatitude();
        this.gpsLongitude = mGPS.getLongitude();
        this.altitude = mGPS.getAltitude();

    }

    public void findOrigin() {
        // Phone is pointing to the right side like -> / when roll <0
        // Origin is closer to the left side          ^
        isLeftOrigin = (roll <= 0); // If angle is 0, want left bottom as origin
        // Phone is pointing forward when pitch > 0
        isTopOrigin = (pitch < 0);
        if (isLeftOrigin && isTopOrigin) {
            // Left top origin
            originalTopLeft = new Coordinates(0,0);
            originalTopRight = new Coordinates(HORIZONTAL_PIXELS,0);
            originalBottomLeft = new Coordinates(0, -VERTICAL_PIXELS);
            originalBottomRight = new Coordinates(HORIZONTAL_PIXELS, -VERTICAL_PIXELS);
            // Left top of new image is also origin
            newTopLeft = new Coordinates(0,0);
            // GPS located somewhere in top left hemisphere
            // -------------------- GPS relative to Origin
        } else if (isLeftOrigin) {
            // Left Bottom origin
            originalTopLeft = new Coordinates(0, VERTICAL_PIXELS);
            originalTopRight = new Coordinates(HORIZONTAL_PIXELS, VERTICAL_PIXELS);
            originalBottomLeft = new Coordinates(0,0);
            originalBottomRight = new Coordinates(HORIZONTAL_PIXELS, 0);
            // Left Bottom of new image is origin
            newBottomLeft = new Coordinates(0,0);
            // GPS located somewhere in bottom left hemisphere
            //
        } else if (isTopOrigin) {
            // Right Top origin
            originalTopLeft = new Coordinates(-HORIZONTAL_PIXELS,0);
            originalTopRight = new Coordinates(0,0);
            originalBottomLeft = new Coordinates(-HORIZONTAL_PIXELS, -VERTICAL_PIXELS);
            originalBottomRight = new Coordinates(0, -VERTICAL_PIXELS);
            // Right Top of new image is origin
            newTopRight = new Coordinates(0,0);
            // GPS located somewhere in top right hemisphere
        } else {
            // Right Bottom origin
            originalTopLeft = new Coordinates(-HORIZONTAL_PIXELS, VERTICAL_PIXELS);
            originalTopRight = new Coordinates(0, VERTICAL_PIXELS);
            originalBottomLeft = new Coordinates(-HORIZONTAL_PIXELS, 0);
            originalBottomRight = new Coordinates(0,0);
            // Right Bottom of new image is origin
            newBottomRight = new Coordinates(0,0);
            // GPS located somewhere in bottom right hemisphere
        }
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
