package com.example.philip.mygpsapp;

import android.app.Activity;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;


public class MainActivity extends Activity {

    private Button btnShowLocation;
    private Button btnCancel;
    private CheckBox externalGPSCheckBox;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView altitudeText;
    private TextView azimuthText;
    private TextView pitchText;
    private TextView rollText;
    private TextView runningText;
    private AngleThread angleThread;
    private GPSThread gpsThread;
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
            gps.setExternalGPSEnabled(true);
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
                Handler locationHandler = new Handler();
                //Handler gpsHandler = new Handler();
                angleThread = new AngleThread(locationHandler);
                //gpsThread = new GPSThread(gpsHandler);
                Log.d("Started thread", "Angles");
                Log.d("Started thread", "GPS location");
                runningText.setText("Running...");
                run = true;
                sensor.startSensors();
                angleThread.start();
                //gpsThread.start();

            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Thread stopped", "Thread stopped");
                gps.stopUsingGPS();
                run = false;
                runningText.setText("Stopped");
                onPause();
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
        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);
        altitudeText = (TextView) findViewById(R.id.altitudeText);
        azimuthText = (TextView) findViewById(R.id.azimuthText);
        pitchText = (TextView) findViewById(R.id.pitchText);
        rollText = (TextView) findViewById(R.id.rollText);
        runningText = (TextView) findViewById(R.id.runningText);
    }



    class AngleThread extends Thread implements Runnable {
        private final Handler mHandler;
        AngleThread(Handler handler) {
            mHandler = handler;
        }
        @Override
        public void run() {
            while (run) {
                try {
                    Log.d("Thread sleep", "Pause for 100ms");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getAngles();
                        //Log.d("Angles", "updated");
                    }
                });
            }
        }
    }

    class GPSThread extends Thread implements Runnable {
        private final Handler mHandler;
        GPSThread(Handler handler) { mHandler = handler;}
        @Override
        public void run() {
            while (run) {
                try {
                    Log.d("Thread sleep", "Pause for 100ms");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getGPS();
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
            // Updates the text
            latitudeText.setText("" + latitude);
            longitudeText.setText("" + longitude);
            altitudeText.setText("" + altitude);

            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            if (externalGPSCheckBox.isChecked() && gps.getExternalGPSEnabled()) {
                gps.showSettingsAlert();
            }
        }
    }

    public void getAngles() {
        if (sensor.getAccelerometerIsAvailable() && sensor.getMagneticFieldIsAvailable()) {
            Log.d("Angles", "Can get angles");
            float azimuth = sensor.getAzimuth();
            float pitch = sensor.getPitch();
            float roll = sensor.getRoll();

            azimuthText.setText("" + Math.round(azimuth) + " degrees from north");
            pitchText.setText("" + Math.round(pitch) + " degrees from horizontal");
            rollText.setText("" + Math.round(roll) + " degrees side to side");
        } else {
            azimuthText.setText("Error");
            pitchText.setText("Error");
            rollText.setText("Error");
        }
    }


}
