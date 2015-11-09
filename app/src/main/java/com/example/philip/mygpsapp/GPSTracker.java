package com.example.philip.mygpsapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Philip on 11/5/2015.
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;

    // Flag for GPS status
    private boolean isGPSEnabled = false;

    // Flag for network status
    private boolean isNetworkEnabled = false;

    // Flag for external gps status
    private boolean isExternalGPSEnabled = false;
    private boolean useExternalGPS = false;
    private final String EXTERNAL_GPS_PROVIDER = "external_gps";
    private LocationProvider customProvider;

    private boolean canGetLocation = false;

    private Location location;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private String lastUpdateTime;
    private String latMinSec;
    private String lonMinSec;

    private double gpsResolution;

    private final double GEOID_WEBSTER_FIELD = -34.923;
    private final double GEOID_CRESSKILL_NJ = -31.589;
    private final double GEOID_RUTGERS_ENG_D = -32.984;

    // Minimum distance change to update
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters

    // Minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100; // 10Hz

    // Declaring the location manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        setLocationManager();
        getLocation();
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        updateLocations();
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void setLocationManager() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (isExternalGPSEnabled && locationManager.getProvider(EXTERNAL_GPS_PROVIDER) == null) {
            locationManager.addTestProvider(EXTERNAL_GPS_PROVIDER, false, false, false, false, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
            locationManager.setTestProviderEnabled(EXTERNAL_GPS_PROVIDER, true);
        }
        // Getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isExternalGPSEnabled = locationManager.isProviderEnabled(EXTERNAL_GPS_PROVIDER);

        customProvider = locationManager.getProvider(EXTERNAL_GPS_PROVIDER);
    }


    public Location getLocation() {

        try {
            if (!isGPSEnabled && !isNetworkEnabled) {
                // No GPS or Network, do nothing
                Log.d("Could not get GPS", "Could not get GPS");
            } else {
                this.canGetLocation = true;
                // Get location from network provider
                /*
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            altitude = location.getAltitude();
                        }
                    }
                }
                */
                // If GPS is enabled, get using GPS services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                    }
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            updateLocations();
                        }
                    }
                }

                if (isExternalGPSEnabled && useExternalGPS) {

                    if (location == null) {
                        locationManager.requestLocationUpdates(customProvider.getName(), MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("External GPS enabled", "External GPS enabled");
                    }
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(customProvider.getName());
                        if (location != null) {
                            updateLocations();
                        }
                    }
                }
                calculateGpsResolution((int)location.getLatitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void updateLocations() {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        speed = location.getSpeed();
        latMinSec = location.convert(latitude, location.FORMAT_SECONDS);
        lonMinSec = location.convert(longitude, location.FORMAT_SECONDS);
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getLatMinSec() { return latMinSec; }

    public String getLonMinSec() { return lonMinSec; }

    public double getAltitude() { return altitude; }

    public double getSpeed() {
        return speed;
    }

    public void calculateGpsResolution(int lat) {
        // Linear interpolation from data at https://en.wikipedia.org/wiki/Decimal_degrees
        // Resolution decimal degrees 5 places, 0.00001 (Individual trees)
        // Can use this with known distances to get new GPS coordinates
        // X lat change / Distance in m = 0.00001 / gpsResolution
        gpsResolution = (lat - 23)*( (0.7871-1.0247)/(45-23) ) + 1.0247;
        // At Webster Field gpsResolution = 0.8627m
    }

    public void setUseExternalGPS(boolean value) {
        useExternalGPS = value;
    }

    public void setIsExternalGPSEnabled(boolean value) {
        isExternalGPSEnabled = value;
    }

    public boolean getIsExternalGPSEnabled() {
        return isExternalGPSEnabled;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public double getGEOID(int c) {
        switch (c) {
            case 0:
                return GEOID_CRESSKILL_NJ;
            case 1:
                return GEOID_RUTGERS_ENG_D;
            default:
                return GEOID_WEBSTER_FIELD;
        }
    }

    public double getGpsResolution() {
        return gpsResolution;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting dialog text
        alertDialog.setTitle("GPS settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting icon to dialog
        // On press Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // On Cancel press
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
