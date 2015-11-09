package com.example.philip.mygpsapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.os.Handler;


public class MainActivity extends Activity {

    private Button btnShowLocation;
    private Button btnCancel;
    private CheckBox externalGPSCheckBox;
    private TextView latitudeMinSecText;
    private TextView latitudeDegText;
    private TextView longitudeMinSecText;
    private TextView longitudeDegText;
    private TextView altitudeText;
    private TextView azimuthText;
    private TextView pitchText;
    private TextView rollText;
    private TextView runningText;
    private TextView speedText;
    private TextView lastUpdateTimeText;
    private LocationThread locationThread;
    private boolean run;



    GPSTracker gps;
    SensorTracker sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTextViews();

        externalGPSCheckBox = (CheckBox) findViewById(R.id.externalGPSCheckBox);
        if (externalGPSCheckBox.isChecked()) {
            externalGPSCheckBox.setChecked(false);
            gps.setIsExternalGPSEnabled(true);
            gps.setUseExternalGPS(true);
        }

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);


        gps = new GPSTracker(MainActivity.this);
        sensor = new SensorTracker(MainActivity.this);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                // Each time the Show Location button is pressed
                // a new thread is started. No way of restarting an old thread
                // Helps with bugs associated with multiple clicks to Show Location
                Handler handler = new Handler();
                LocationThread thread = new LocationThread(handler);
                Log.d("Started thread", "Started thread");
                runningText.setText("Running...");
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
                runningText.setText("Stopped");
            }
        });

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

    public void getTextViews() {
        latitudeDegText = (TextView) findViewById(R.id.latitudeDegText);
        latitudeMinSecText = (TextView) findViewById(R.id.latitudeMinSecText);
        longitudeDegText = (TextView) findViewById(R.id.longitudeDegText);
        longitudeMinSecText = (TextView) findViewById(R.id.longitudeMinSecText);
        altitudeText = (TextView) findViewById(R.id.altitudeText);
        azimuthText = (TextView) findViewById(R.id.azimuthText);
        pitchText = (TextView) findViewById(R.id.pitchText);
        rollText = (TextView) findViewById(R.id.rollText);
        runningText = (TextView) findViewById(R.id.runningText);
        speedText = (TextView) findViewById(R.id.speedText);
        lastUpdateTimeText = (TextView) findViewById(R.id.lastUpdateTimeText);
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
                        getGPS();
                        //Log.d("GPS location", "Updated");
                        getAngles();
                        //Log.d("Angles", "updated");
                    }
                });
            }
        }
    }

    public void getGPS() {
        // check if GPS enabled
        if(gps.canGetLocation()){
            Log.d("GPS", "Can get location");
            // If the GPS if working
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            double altitude = gps.getAltitude();
            altitude = altitude - gps.getGEOID(0); // GEOID 0 for Cresskill, 1 for Rutgers, other for Webster Field
            double speed = gps.getSpeed()*2.23694; // Convert from m/s to mph
            // Updates the text
            /*
            latitudeText.setText(String.format("%.3f", latitude));
            longitudeText.setText(String.format("%.3f", longitude));
            */
            // 5th decimal place is about 1.1m resolution
            latitudeDegText.setText(String.format("%.5f", latitude));
            latitudeMinSecText.setText(convertDecimalToMinutes(latitude));
            longitudeDegText.setText(String.format("%.5f", longitude));
            longitudeMinSecText.setText(convertDecimalToMinutes(longitude));
            altitudeText.setText("" + altitude + "m");
            speedText.setText((int)speed + "mph");
            lastUpdateTimeText.setText(gps.getLastUpdateTime());

            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            if (externalGPSCheckBox.isChecked() && gps.getIsExternalGPSEnabled()) {
                gps.showSettingsAlert();
            }
        }
    }

    public String convertDecimalToMinutes(double decimal) {
        int degree = (int) decimal;
        int minutes = (int)(60 * ( decimal - degree));
        int seconds = (int)(3600 * ((decimal - degree) - minutes/60.0));
        return degree + "\u00b0" + minutes + "\'" + seconds + "\"";
    }


    public void getAngles() {
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
