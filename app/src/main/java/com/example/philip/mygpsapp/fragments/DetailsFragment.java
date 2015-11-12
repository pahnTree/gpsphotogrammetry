package com.example.philip.mygpsapp.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.philip.mygpsapp.services.GPSTracker;
import com.example.philip.mygpsapp.activities.GeotagActivity;
import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.services.SensorTracker;

/**
 * Created by Phil on 11/11/2015.
 * This fragment is used to show details about the smartphone and the pixhawk
 * Can choose to start the process and cancel
 * Creates a gps object, a sensor object, and a geotag object
 */
public class DetailsFragment extends Fragment {

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

    private View v;

    private Button btnGatherData;
    private Button btnCancel;

    private GPSTracker gps;
    private SensorTracker sensor;
    private GeotagActivity geo;

    private Context mContext;

    private boolean run;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);
        mContext = v.getContext();
        getTextViews();
        getClick();

        return v;
    }

    public void getTextViews() {
        latitudeDegText = (TextView) v.findViewById(R.id.latitudeDegText);
        latitudeMinSecText = (TextView) v.findViewById(R.id.latitudeMinSecText);
        longitudeDegText = (TextView) v.findViewById(R.id.longitudeDegText);
        longitudeMinSecText = (TextView) v.findViewById(R.id.longitudeMinSecText);
        altitudeText = (TextView) v.findViewById(R.id.altitudeText);
        azimuthText = (TextView) v.findViewById(R.id.azimuthText);
        pitchText = (TextView) v.findViewById(R.id.pitchText);
        rollText = (TextView) v.findViewById(R.id.rollText);
        runningText = (TextView) v.findViewById(R.id.runningText);
        speedText = (TextView) v.findViewById(R.id.speedText);
        lastUpdateTimeText = (TextView) v.findViewById(R.id.lastUpdateTimeText);
    }

    // Click listeners
    public void getClick() {
        btnGatherData = (Button) v.findViewById(R.id.btnGatherData);

        gps = new GPSTracker(mContext);
        sensor = new SensorTracker(mContext);
        geo = new GeotagActivity(mContext, gps, sensor);

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
                runningText.setText("Running...");
                sensor.startSensors();
                run = true;
                thread.start();
            }
        });

        btnCancel = (Button) v.findViewById(R.id.btnCancel);
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

    /**
     * Helper class used to run tasks in the background
     * Will continuously gather gps data and smartphone orientation data until user clicks cancel
     */
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

    @Override
    public void onResume() {
        super.onResume();
        if (run) {
            sensor.startSensors();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensor.stopSensors();
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
            altitudeText.setText(String.format("%.1f", altitude) + "m");
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
