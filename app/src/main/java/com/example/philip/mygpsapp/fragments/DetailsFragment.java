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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.mygpsapp.services.DroneTracker;
import com.example.philip.mygpsapp.services.GPSTracker;
import com.example.philip.mygpsapp.activities.GeotagActivity;
import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.services.SensorTracker;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.coordinate.LatLongAlt;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.Altitude;
import com.o3dr.services.android.lib.drone.property.Gps;
import com.o3dr.services.android.lib.drone.property.Home;
import com.o3dr.services.android.lib.drone.property.Speed;
import com.o3dr.services.android.lib.drone.property.State;
import com.o3dr.services.android.lib.drone.property.Type;
import com.o3dr.services.android.lib.drone.property.VehicleMode;

import java.util.List;

/**
 * Created by Phil on 11/11/2015.
 * This fragment is used to show details about the smartphone and the pixhawk
 * Can choose to start the process and cancel
 * Creates a gps object, a sensor object, and a geotag object
 */
public class DetailsFragment extends Fragment implements TowerListener, DroneListener {

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

    private TextView altitudeDroneText;
    private TextView speedDroneText;
    private TextView latitudeDroneDegText;
    private TextView longitudeDroneDegText;
    private TextView distanceDroneText;
    private TextView azimuthDroneText;
    private TextView pitchDroneText;
    private TextView rollDroneText;

    private View v;

    private Button btnGatherData;
    private Button btnCancel;
    private Button btnConnect;
    private Button btnArm;

    private GPSTracker gps;
    private SensorTracker sensor;
    private GeotagActivity geo;
    private DroneTracker droneTracker;

    private Context mContext;
    private boolean run;

    private Handler sensorHandler = new Handler();
    private final Handler droneHandler = new Handler();
    private ControlTower mControlTower;
    private Drone mDrone;
    private int droneType = Type.TYPE_UNKNOWN;
    private Spinner modeSpinner;
    private Gps droneGPS;
    private LatLong droneLatLong;

    private final String PREFERENCE_FILE_NAME = "preferences";
    private SharedPreferences settings;
    private boolean usePixhawk;

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
        mContext = container.getContext();
        this.mControlTower = new ControlTower(mContext);
        this.mDrone = new Drone(mContext);

        this.mControlTower.connect(this);

        settings = mContext.getSharedPreferences(PREFERENCE_FILE_NAME, 0);
        usePixhawk = settings.getBoolean("checkbox_pixhawk_connect", false);

        getTextViews();
        getButtons();
        getTrackers();
        getClick();

        return v;
    }

    private void getTextViews() {
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
        speedDroneText = (TextView) v.findViewById(R.id.speedDroneText);
        azimuthDroneText = (TextView) v.findViewById(R.id.azimuthDroneText);
        pitchDroneText = (TextView) v.findViewById(R.id.pitchDroneText);
        rollDroneText = (TextView) v.findViewById(R.id.rollDroneText);
    }

    private void getButtons() {
        btnGatherData = (Button) v.findViewById(R.id.btnGatherData);
        btnConnect = (Button) v.findViewById(R.id.btnConnect);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnArm = (Button) v.findViewById(R.id.btnArmTakeOff);
        modeSpinner = (Spinner) v.findViewById(R.id.modeSpinner);
    }

    private void getTrackers() {
        gps = new GPSTracker(mContext);
        sensor = new SensorTracker(mContext);
        geo = new GeotagActivity(mContext, gps, sensor);
        droneTracker = new DroneTracker(mContext);
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
                LocationThread thread = new LocationThread(sensorHandler);
                Log.d("Started thread", "Started thread");
                runningText.setText("Running...");
                sensor.startSensors();
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
                disconnectDrone();
                runningText.setText("Stopped");
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usePixhawk) {
                    onBtnConnectTap(view);
                    if (!mDrone.isConnected()) {
                        alertUser("Error: Unable to connect");
                    }
                } else {
                    alertUser("Error: Change settings");
                }
            }
        });

        btnArm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onArmButtonTap(view);
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
    }

    public void onBtnConnectTap(View view) {
        connectDrone();
    }

    public void updateConnectButton(boolean isConnected) {
        if (isConnected) {
            btnConnect.setText("Disconnect");
        } else {
            btnConnect.setText("Connect to Drone");
        }
    }

    public void onArmButtonTap(View view) {
        State vehicleState = mDrone.getAttribute(AttributeType.STATE);

        if (vehicleState.isFlying()) {
            // Land
            mDrone.changeVehicleMode(VehicleMode.COPTER_LAND);
        } else if (vehicleState.isArmed()) {
            // Take off
            mDrone.doGuidedTakeoff(10); // Default take off altitude is 10m
        } else if (!vehicleState.isConnected()) {
            // Connect
            alertUser("Connect to a drone first");
        } else if (vehicleState.isConnected() && !vehicleState.isArmed()){
            // Connected but not Armed
            mDrone.arm(true);
        }
    }

    protected void updateArmButton() {
        State vehicleState = mDrone.getAttribute(AttributeType.STATE);
        Button armButton = (Button) v.findViewById(R.id.btnArmTakeOff);

        if (!mDrone.isConnected()) {
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

    // ------------------- Drone Tracker related items
    public void connectDrone() {
        if (this.mDrone.isConnected()) {
            disconnectDrone();
        }
        Bundle extraParams = new Bundle();
        extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, 57600);
        ConnectionParameter mConnectionParameter = new ConnectionParameter(ConnectionType.TYPE_USB, extraParams, null);
        mDrone.connect(mConnectionParameter);
    }

    public void disconnectDrone() {
        if (mDrone.isConnected()) {
            mDrone.disconnect();
            alertUser("Drone: Disconnected");
        }
    }

    public void connectTower() {
        if (!mControlTower.isTowerConnected()) {
            mControlTower.connect(this);
            mControlTower.registerDrone(mDrone, droneHandler);
            mDrone.registerDroneListener(this);
        }
    }

    public void disconnectTower() {
        if (mControlTower.isTowerConnected()) {
            disconnectDrone();
            mControlTower.disconnect();
        }
    }

    @Override
    public void onTowerConnected() {
        Log.d("3DR Services", "Connected to tower");
        alertUser("3DR Services connected");
        mControlTower.registerDrone(mDrone, droneHandler);
        mDrone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {
        alertUser("3DR Services Interrupted");
    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult result) {
        Log.d("Error: ", result.getErrorMessage());
    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        Log.d("Drone Event", "Updated");
        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                alertUser("State: Drone connected");
                updateConnectButton(mDrone.isConnected());
                updateArmButton();
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("State: Drone disconnected");
                updateConnectButton(mDrone.isConnected());
                updateArmButton();
                break;

            case AttributeEvent.STATE_VEHICLE_MODE:
                updateVehicleMode();
                break;

            case AttributeEvent.TYPE_UPDATED:
                Type newDroneType = mDrone.getAttribute(AttributeType.TYPE);
                if (newDroneType.getDroneType() != droneType) {
                    droneType = newDroneType.getDroneType();
                    updateVehicleModeForType(droneType);
                }
                break;

            case AttributeEvent.GPS_POSITION:
                updateGPSPosition();
                break;


            case AttributeEvent.STATE_UPDATED:
            case AttributeEvent.STATE_ARMING:
                updateArmButton();
                break;

            case AttributeEvent.SPEED_UPDATED:
                updateSpeed();
                //updateGPSPosition();
                break;

            case AttributeEvent.ALTITUDE_UPDATED:
                updateAltitude();
                //updateGPSPosition();
                break;


            case AttributeEvent.HOME_UPDATED:
                updateDistanceFromHome();
                break;

            default:
                //alertUser("Update out of bounds: " + event);
                break;
        }
    }

    @Override
    public void onDroneServiceInterrupted(String errorMsg) {
        Log.d("Error: ", errorMsg);
    }

    public void onFlightModeSelected(View view) {
        VehicleMode vehicleMode = (VehicleMode)modeSpinner.getSelectedItem();
        mDrone.changeVehicleMode(vehicleMode);
    }

    protected void updateVehicleModeForType(int droneType) {
        alertUser("Obtained vehicle modes");
        List<VehicleMode> vehicleModes = VehicleMode.getVehicleModePerDroneType(droneType);
        ArrayAdapter<VehicleMode> vehicleModeArrayAdapter = new ArrayAdapter<VehicleMode>(mContext, android.R.layout.simple_spinner_item, vehicleModes);
        vehicleModeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(vehicleModeArrayAdapter);
    }

    protected void updateVehicleMode() {
        State vehicleState = mDrone.getAttribute(AttributeType.STATE);
        VehicleMode vehicleMode = vehicleState.getVehicleMode();
        ArrayAdapter arrayAdapter = (ArrayAdapter) modeSpinner.getAdapter();
        modeSpinner.setSelection(arrayAdapter.getPosition(vehicleMode));
    }

    //----------------------- Starts
    @Override
    public void onStart() {
        super.onStart();
        sensor.startSensors();
        mControlTower.connect(this);
        updateVehicleModeForType(this.droneType);
    }

    @Override
    public void onResume() {
        super.onResume();
        usePixhawk = settings.getBoolean("checkbox_pixhawk_connect", false);
        if (run) {
            connectTower();
            sensor.startSensors();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!run) {
            sensor.stopSensors();
            if (mDrone.isConnected()) {
                disconnectDrone();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sensor.stopSensors();
        if (mDrone.isConnected()) {
            disconnectDrone();
        }
        disconnectTower();
    }

    // ---------------------- DATA GATHERING
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
                        //if (usePixhawk && mDrone.isConnected()) {
                        //    getDroneGPSData();
                        //}
                    }
                });
            }
        }
    }


    // ---------------- PHONE DATA
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

    public void getOnBoardAngles() {
        if (sensor.getAccelerometerIsAvailable() && sensor.getMagneticFieldIsAvailable()) {
            //Log.d("Angles", "Can get angles");
            float azimuth = sensor.getAzimuth();
            float pitch = -1*sensor.getPitch();
            float roll = sensor.getRoll();

            azimuthText.setText("" + Math.round(azimuth) + "\u00b0");
            pitchText.setText("" + Math.round(pitch) + "\u00b0");
            rollText.setText("" + Math.round(roll) + "\u00b0");
        } else {
            azimuthText.setText("Error");
            pitchText.setText("Error");
            rollText.setText("Error");
        }
    }

    // ------------------- DRONE DATA
    protected void updateSpeed() {
        Speed droneSpeed = mDrone.getAttribute(AttributeType.SPEED);
        speedDroneText.setText(String.format("%3.1f", droneSpeed.getGroundSpeed()) + "m/s");
    }

    protected void updateAltitude() {
        Altitude droneAltitude = (Altitude)this.mDrone.getAttribute(AttributeType.ALTITUDE);
        altitudeDroneText.setText(String.format("%3.1f", droneAltitude.getAltitude()) + "m");
        updateGPSPosition();
    }

    protected void updateDistanceFromHome() {
        TextView distanceTextView = (TextView)v.findViewById(R.id.distanceDroneText);
        Altitude droneAltitude = this.mDrone.getAttribute(AttributeType.ALTITUDE);
        double vehicleAltitude = droneAltitude.getAltitude();
        Gps droneGps = this.mDrone.getAttribute(AttributeType.GPS);
        LatLong vehiclePosition = droneGps.getPosition();

        double distanceFromHome =  0;

        if (droneGps.isValid()) {
            LatLongAlt vehicle3DPosition = new LatLongAlt(vehiclePosition.getLatitude(), vehiclePosition.getLongitude(), vehicleAltitude);
            Home droneHome = mDrone.getAttribute(AttributeType.HOME);
            distanceFromHome = distanceBetweenPoints(droneHome.getCoordinate(), vehicle3DPosition);
        } else {
            distanceFromHome = 0;
        }

        distanceTextView.setText(String.format("%3.1f", distanceFromHome) + "m");
    }

    protected double distanceBetweenPoints(LatLongAlt pointA, LatLongAlt pointB) {
        if (pointA == null || pointB == null) {
            return 0;
        }
        double dx = pointA.getLatitude() - pointB.getLatitude();
        double dy  = pointA.getLongitude() - pointB.getLongitude();
        double dz = pointA.getAltitude() - pointB.getAltitude();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    protected void updateGPSPosition() {
        Log.d("Drone GPS", "Updated with Location");
        droneGPS = mDrone.getAttribute(AttributeType.GPS);
        droneLatLong = droneGPS.getPosition();
        latitudeDroneDegText.setText("" + droneLatLong.getLatitude() + "\u00b0");
        longitudeDroneDegText.setText("" + droneLatLong.getLongitude() + "\u00b0");
    }

    public void getDroneGPSData() {
        Log.d("Drone GPS", "Updated with MAVLink");
        LatLong latlong = droneTracker.getLastDronePosition();
        latitudeDroneDegText.setText(String.format("%.5f", latlong.getLatitude()) + "\u00b0");
        longitudeDroneDegText.setText(String.format("%.5f", latlong.getLongitude()) + "\u00b0");
    }



    public void alertUser(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

}
