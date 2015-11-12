package com.example.philip.mygpsapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.os.Handler;


public class MainActivity extends Activity {

    private Button btnGatherData;
    private Button btnCancel;
    private Button btnDataDetails;
    private Button btnSettings;

    private boolean run;

    GPSTracker gps;
    SensorTracker sensor;
    GeotagActivity geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGatherData = (Button) findViewById(R.id.btnGatherData);

        gps = new GPSTracker(MainActivity.this);
        sensor = new SensorTracker(MainActivity.this);
        geo = new GeotagActivity(MainActivity.this, gps, sensor);



        btnGatherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                // Each time the Show Location button is pressed
                // a new thread is started. No way of restarting an old thread
                // Helps with bugs associated with multiple clicks to Show Location
                Handler handler = new Handler();
                LocationThread thread = new LocationThread(handler);
                Log.d("Started thread", "Started thread");

                sensor.startSensors();
                run = true;
                thread.start();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Thread stopped", "Thread stopped");
                gps.stopUsingGPS();
                sensor.stopSensors();
                run = false;

            }
        });
    }

    public void selectFragment(View view) {
        Fragment fr;
        if (view == findViewById(R.id.btnGatherData)) {
            fr = new DetailsFragment();
        } else if (view == findViewById(R.id.btnSettings)) {
            fr = new SettingsFragment();
        } else {
            fr = new MainFragment();
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, fr);
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (run) {
            sensor.startSensors();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensor.stopSensors();
    }



    class LocationThread extends Thread implements Runnable {
        private final Handler mHandler;
        LocationThread(Handler handler) {
            mHandler = handler;
        }
        @Override
        public void run() {
            while (run) {
                try {
                    Log.d("Thread sleep", "Pause for 50ms");
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getOnBoardGPS();
                        //Log.d("GPS location", "Updated");
                        getOnBoardAngles();
                        //Log.d("Angles", "updated");
                        //getPixhawkData();
                    }
                });
            }
        }
    }

    public void getOnBoardGPS() {
        // check if GPS enabled
        if(gps.canGetLocation()){
            Log.d("GPS", "Can get location");
            // If the GPS if working
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            double altitude = gps.getAltitude();
            String latMinSec = gps.getLatMinSec();
            String lonMinSec = gps.getLonMinSec();
            altitude = altitude - gps.getGEOID(0); // GEOID 0 for Cresskill, 1 for Rutgers, other for Webster Field
            double speed = gps.getSpeed()*2.23694; // Convert from m/s to mph
            // 5th decimal place is about 0.8627m resolution at 38N Latitutde (Webster Field)
            latitudeDegText.setText(String.format("%.5f", latitude));
            latitudeMinSecText.setText(latMinSec);
            longitudeDegText.setText(String.format("%.5f", longitude));
            longitudeMinSecText.setText(lonMinSec);
            altitudeText.setText("" + altitude + "m");
            speedText.setText((int)speed + "mph");
            lastUpdateTimeText.setText(gps.getLastUpdateTime());

            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            if (gps.getIsExternalGPSEnabled()) {
                gps.showSettingsAlert();
            }
        }
    }

    public void getOnBoardAngles() {
        if (sensor.getAccelerometerIsAvailable() && sensor.getMagneticFieldIsAvailable()) {
            Log.d("Angles", "Can get angles");
            float azimuth = sensor.getAzimuth();
            float pitch = -1*sensor.getPitch();
            float roll = sensor.getRoll();

            azimuthText.setText("" + Math.round(azimuth) + "\u00b0 (from North)");
            pitchText.setText("" + Math.round(pitch) + "\u00b0");
            rollText.setText("" + Math.round(roll) + "\u00b0");
        } else {
            azimuthText.setText("Error");
            pitchText.setText("Error");
            rollText.setText("Error");
        }
    }
}
