package com.example.philip.mygpsapp.fragments;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.mygpsapp.services.DroneTracker;
import com.example.philip.mygpsapp.services.GPSTracker;
import com.example.philip.mygpsapp.activities.GeotagActivity;
import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.services.SensorTracker;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.VehicleMode;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Phil on 11/11/2015.
 * This fragment is used to show details about the smartphone and the pixhawk
 * Can choose to start the process and cancel
 * Creates a gps object, a sensor object, and a geotag object
 */
public class DetailsFragment extends Fragment {

    public static final String PREFERENCES_FILE_NAME = "preferences";

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

    private boolean usePixhawk;
    private TextView altitudeDroneText;
    private TextView speedDroneText;
    private TextView latitudeDroneDegText;
    private TextView longitudeDroneDegText;
    private TextView distanceDroneText;

    private View v;

    private Button btnGatherData;
    private Button btnCancel;
    private Button btnConnect;
    private Button btnArm;

    private GPSTracker gps;
    private SensorTracker sensor;
    private GeotagActivity geo;
    private DroneTracker pixhawk;

    private Context mContext;
    private boolean run;

    private Handler handler = new Handler();
    private Spinner modeSpinner;

    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);
        mContext = getActivity();
        settings = mContext.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        usePixhawk = settings.getBoolean("checkbox_pixhawk_connect", false);

        getButtons();
        getTrackerObjects();
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

        altitudeDroneText = (TextView) v.findViewById(R.id.altitudeDroneText);
        latitudeDroneDegText = (TextView) v.findViewById(R.id.latitudeDroneDegText);
        longitudeDroneDegText = (TextView) v.findViewById(R.id.longitudeDroneDegText);
        distanceDroneText = (TextView) v.findViewById(R.id.distanceDroneText);
    }

    private void getButtons() {
        btnGatherData = (Button) v.findViewById(R.id.btnGatherData);
        btnConnect = (Button) v.findViewById(R.id.btnConnect);
        btnArm = (Button) v.findViewById(R.id.btnArmTakeOff);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);


    }

    private void getTrackerObjects() {
        gps = new GPSTracker(mContext);
        sensor = new SensorTracker(mContext);
        geo = new GeotagActivity(mContext, gps, sensor);
        pixhawk = new DroneTracker(mContext);
        modeSpinner = (Spinner) v.findViewById(R.id.modeSpinner);
    }

    // Click listeners
    public void getClick() {
       btnGatherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                // Each time the Show Location button is pressed
                // a new thread is started. No way of restarting an old thread
                // Helps with bugs associated with multiple clicks to Show Location
                LocationThread thread = new LocationThread(handler);
                Log.d("Started thread", "Started thread");
                runningText.setText("Running...");
                sensor.startSensors();
                if (usePixhawk) {
                    pixhawk.connectTower();
                    pixhawk.connectDrone();
                }
                run = true;
                thread.start();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Thread stopped", "Thread stopped");
                gps.stopUsingGPS();
                sensor.stopSensors();
                run = false;
                pixhawk.disconnectDrone();
                runningText.setText("Stopped");
            }
        });


        modeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onFlightModeSelected(view);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usePixhawk = settings.getBoolean("checkbox_pixhawk_connect", false);
                if (usePixhawk) {
                    pixhawk.onBtnConnectTap(view);
                    updateConnectButton(pixhawk.isDroneConnected());
                } else {
                    alertUser("Update settings");
                }
            }
        });

        btnArm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pixhawk.onArmButtonTap(view);
                updateArmButton();
            }
        });

    }

    public void updateConnectButton(boolean isConnected) {
        if (isConnected) {
            btnConnect.setText("Disconnect");
        } else {
            btnConnect.setText("Connect to Drone");
        }
    }

    public void onFlightModeSelected(View view) {
        VehicleMode vehicleMode = (VehicleMode)modeSpinner.getSelectedItem();
        pixhawk.onFlightModeSelected(vehicleMode);
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
                    Log.d("Thread sleep", "Pause for 100ms");
                    Thread.sleep(100);
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
                        if (usePixhawk)
                            getPixhawkData();
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
            if (usePixhawk) {
                pixhawk.connectDrone();
                pixhawk.connectTower();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        sensor.stopSensors();
        pixhawk.disconnectDrone(); // Disconnects tower and drone
    }

    public void getOnBoardGPS() {
        // check if GPS enabled
        if(gps.canGetLocation()){
            //Log.d("GPS", "Can get location");
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

    public void getPixhawkData() {
        if (pixhawk.isDroneConnected() && usePixhawk) {
            double altitude = pixhawk.getAltitude();
            double groundSpeed = pixhawk.getSpeed();
            pixhawk.updateDistanceFromHome();
            double distance = pixhawk.getDistanceFromHome();
            altitudeDroneText.setText(String.format(".%3.1f", altitude) + "m");
            speedDroneText.setText(String.format("3.1f", groundSpeed) + "m/s");
            distanceDroneText.setText(String.format("3.1f", distance) + "m");

        }
    }

    public void getOnBoardAngles() {
        if (sensor.getAccelerometerIsAvailable() && sensor.getMagneticFieldIsAvailable()) {
            //Log.d("Angles", "Can get angles");
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

    public void alertUser(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    protected void updateArmButton() {
        State vehicleState = pixhawk.getVehicleState();
        Button armButton = (Button) v.findViewById(R.id.btnArmTakeOff);

        if (!pixhawk.isDroneConnected()) {
            armButton.setVisibility(View.INVISIBLE);
        } else {
            armButton.setVisibility(View.VISIBLE);
        }

        if (vehicleState.isFlying()) {
            // Land
            armButton.setText("LAND");
        } else if (vehicleState.isArmed()) {
            // Take off
            armButton.setText("TAKE OFF");
        } else if (vehicleState.isConnected()) {
            // Not armed
            armButton.setText("ARM");
        }
    }
}
