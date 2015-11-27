package com.example.philip.mygpsapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.services.Coordinates;
import com.example.philip.mygpsapp.services.GPSTracker;
import com.example.philip.mygpsapp.services.SensorTracker;
import com.o3dr.services.android.lib.coordinate.LatLong;

import org.w3c.dom.Text;


/**
 * Created by Phil on 11/9/2015.
 */
public class GeotagActivity extends Activity {
    private Context mContext;
    private GPSTracker mGPS;
    private SensorTracker mSensor;
    private Activity mainAct;
    // Camera is locked in portrait mode
    private double gpsLatitude; // Decimal with 5 trailing 0.00001
    private double gpsLongitude;
    private double altitude;
    // The Field of View is obtained manually
    // Differs from phone to phone
    // These values are for when the phone is in portrait mode (from middle to one side)
    // Total viewing angle is x2
    // Horizontal is the short side, vertical is the long side
    private final float cameraFieldOfViewHorizontal = 18.33f; // Obtain with actual measurements
    private final float cameraFieldOfViewVertical = 30.96f;
    private float azimuth;
    private float pitch;
    private float roll;



    // For every 0.8627m change in distance is approximately 0.00001 degree
    private final double METER_TO_DEGREE_CONVERSION = 0.00001/0.8627;

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

    private LatLong TopLeft, TopRight, BottomLeft, BottomRight;

    public GeotagActivity(Context context, GPSTracker gps, SensorTracker sensor) {
        this.mContext = context;
        this.mainAct = (Activity)context;
        this.mGPS = gps;
        this.mSensor = sensor;
        loadData();
        gpsCoordinates = new Coordinates(gpsLongitude, gpsLatitude);
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
            this.magnitudeDistance = calculateMagnitude();
        }

        /**
         * Calculates the magnitude of the distance between two points
         * @return
         */
        public float calculateMagnitude() {
            float distX = Math.abs(p2.distanceX - p1.distanceX);
            float distY = Math.abs(p2.distanceY - p1.distanceY);
            return (float)Math.sqrt( Math.pow(distX, 2) + Math.pow(distY, 2));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void loadData() {
        this.azimuth = mSensor.getAzimuth();
        this.pitch = mSensor.getPitch();
        this.roll = mSensor.getRoll();
        this.gpsLatitude = mGPS.getLatitude();
        this.gpsLongitude = mGPS.getLongitude();
        this.altitude = mGPS.getAltitude();
    }

    public void calculateLocations(GPSTracker gps, SensorTracker sensor) {
        mGPS = gps;
        mSensor = sensor;
        loadData();

        double x, y; // Distance in meter from GPS to picture edges
        double xlatm, xlonm, ylatm, ylonm; // Distances accounting for azimuth in meters
        double deltaLatm, deltaLonm; // Total changes for lat, lon
        double deltaLatDeg, deltaLonDeg; // Convert from meter to degree
        // Opposite side things
        double xO, yO;
        double xOlatm, xOlonm, yOlatm, yOlonm;
        double deltaOLatm, deltaOLonm;
        double deltaOLatDeg, deltaOLonDeg;

        double yAngle = cameraFieldOfViewVertical - pitch;
        double yOppositeAngle = cameraFieldOfViewVertical + pitch;
        double xAngle = cameraFieldOfViewHorizontal + roll;
        double xOppositeAngle = cameraFieldOfViewHorizontal - roll;
        double aziAngle = (Math.abs(azimuth) > 90) ? 180 - Math.abs(azimuth) : Math.abs(azimuth);
        // Convert to radians
        yAngle = Math.toRadians(yAngle);
        yOppositeAngle = Math.toRadians(yOppositeAngle);
        xAngle = Math.toRadians(xAngle);
        xOppositeAngle = Math.toRadians(xOppositeAngle);
        aziAngle = Math.toRadians(aziAngle);

        // When the phone is pitch up
        // The distance in meters between the GPS source and the bottom on the photo
        y = altitude * Math.tan(yAngle);
        ylatm = y * Math.cos(aziAngle);
        ylonm = y * Math.sin(aziAngle);
        // The other side
        yO = altitude * Math.tan(yOppositeAngle);
        yOlatm = yO * Math.cos(aziAngle);
        yOlonm = yO * Math.sin(aziAngle);

        // If the phone is pointing towards the right when roll is -
        x = altitude * Math.tan(xAngle);
        xlatm = x * Math.sin(aziAngle);
        xlonm = x * Math.cos(aziAngle);
        // Other side
        xO = altitude * Math.tan(xOppositeAngle);
        xOlatm = xO * Math.sin(aziAngle);
        xOlonm = xO * Math.cos(aziAngle);

        // Create a coordinate for Bottom left corner of picture
        if (Math.abs(azimuth) <= 90) {
            deltaLatm = (azimuth >= 0) ? ylatm - xlatm : ylatm + xlatm;
            deltaLonm = (azimuth >= 0) ? xlonm + ylonm : xlonm - ylonm;
            // Opposite
            deltaOLatm = (azimuth >= 0) ? yOlatm - xOlatm : yOlatm + xOlatm;
            deltaOLonm = (azimuth >= 0) ? xOlonm + yOlonm : xOlonm - yOlonm;
        } else {
            // When pointing south
            deltaLatm = (azimuth >= 0) ? ylatm + xlatm : ylatm - xlatm;
            deltaLonm = (azimuth >= 0) ? xlonm - ylonm : xlonm + ylonm;
            // Opposite side
            deltaOLatm = (azimuth >= 0) ? yOlatm + xOlatm : yOlatm - xOlatm;
            deltaOLonm = (azimuth >= 0) ? xOlonm - yOlonm : xOlonm + yOlonm;
        }
        // Convert
        deltaLatDeg = METER_TO_DEGREE_CONVERSION * deltaLatm;
        deltaLonDeg = METER_TO_DEGREE_CONVERSION * deltaLonm;

        deltaOLatDeg = METER_TO_DEGREE_CONVERSION * deltaOLatm;
        deltaOLonDeg = METER_TO_DEGREE_CONVERSION * deltaOLonm;

        // Coordinates of corners of image
        double ptBLlat, ptBLlon; // Image Bottom Left corner
        double ptBRlat, ptBRlon; // Bottom Right corner
        double ptTLlat, ptTLlon; // Top Left corner
        double ptTRlat, ptTRlon; // Top Right corner


        // South is azimuth -135 >> -180 && 180 >> 135
        // West is azimuth -135 >> -45
        // North is azimuth -45 >> 0 && 0 >> 45
        // East is azimuth 45 >> 135

        /*
        Adding or subtracting the Delta Latitude/Longitudes depends on the pitch, roll, and azimuth
        of the phone and changes which slight variations of the angles. Need to figure out how to
        calculate when to add or subtract.
        May have something to do with the Field Of View vs Pitch/Roll
         */
        if (-90 <= azimuth && azimuth < 0) {
            // Camera pointing North West
            ptBLlat = gpsLatitude - deltaLatDeg;
            ptBLlon = gpsLongitude - deltaLonDeg;
            ptBRlat = gpsLatitude - METER_TO_DEGREE_CONVERSION * (xlatm);
            ptBRlon = gpsLongitude + METER_TO_DEGREE_CONVERSION * (xOlonm + ylonm);
            ptTLlat = gpsLatitude + METER_TO_DEGREE_CONVERSION * (yOlatm - xlatm);
            ptTLlon = gpsLongitude - METER_TO_DEGREE_CONVERSION * (xlonm + yOlonm);
            ptTRlat = gpsLatitude + deltaOLatDeg;
            ptTRlon = gpsLongitude + deltaOLonDeg;
        } else if (0 <= azimuth && azimuth < 90) {
            // Camera pointing North East
            ptBLlat = gpsLatitude - deltaLatDeg;
            ptBLlon = gpsLongitude - deltaLonDeg;
            ptBRlat = gpsLatitude - METER_TO_DEGREE_CONVERSION * (ylatm + xOlatm);
            ptBRlon = gpsLongitude - METER_TO_DEGREE_CONVERSION * (ylonm - xOlonm);
            ptTLlat = gpsLatitude + METER_TO_DEGREE_CONVERSION * (xlatm + yOlatm);
            ptTLlon = gpsLongitude - METER_TO_DEGREE_CONVERSION * (xlonm - yOlonm);
            ptTRlat = gpsLatitude + deltaOLatDeg;
            ptTRlon = gpsLongitude + deltaOLonDeg;
        } else if (90 <= azimuth && azimuth < 180) {
            // Camera pointing South East
            ptBLlat = gpsLatitude + deltaLatDeg;
            ptBLlon = gpsLongitude + deltaLonDeg;
            ptBRlat = gpsLatitude - METER_TO_DEGREE_CONVERSION * (ylatm - xOlatm);
            ptBRlon = gpsLongitude - METER_TO_DEGREE_CONVERSION * (ylonm + xOlonm);
            ptTLlat = gpsLatitude - METER_TO_DEGREE_CONVERSION * (xlatm - xOlatm);
            ptTLlon = gpsLongitude + METER_TO_DEGREE_CONVERSION * (xlonm + yOlonm);
            ptTRlat = gpsLatitude - deltaOLatDeg;
            ptTRlon = gpsLongitude - deltaOLonDeg;
        } else {
            // Camera pointing South West
            ptBLlat = gpsLatitude + deltaLatDeg;
            ptBLlon = gpsLongitude + deltaLonDeg;
            ptBRlat = gpsLatitude + METER_TO_DEGREE_CONVERSION * (ylatm + xOlatm);
            ptBRlon = gpsLongitude - METER_TO_DEGREE_CONVERSION * (xOlonm - xlonm);
            ptTLlat = gpsLatitude - METER_TO_DEGREE_CONVERSION * (yOlatm + xlatm);
            ptTLlon = gpsLongitude - METER_TO_DEGREE_CONVERSION * (yOlonm - xlonm);
            ptTRlat = gpsLatitude - deltaOLatDeg;
            ptTRlon = gpsLongitude - deltaOLonDeg;
        }
        BottomLeft = new LatLong(ptBLlat, ptBLlon);
        BottomRight = new LatLong(ptBRlat, ptBRlon);
        TopLeft = new LatLong(ptTLlat, ptTLlon);
        TopRight = new LatLong(ptTRlat, ptTRlon);

        //updateTextViews();
    }

    public LatLong getTopLeft() { return TopLeft; }
    public LatLong getTopRight() { return TopRight; }
    public LatLong getBottomLeft() { return BottomLeft; }
    public LatLong getBottomRight() { return BottomRight; }


}
