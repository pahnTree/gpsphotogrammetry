package com.example.philip.mygpsapp.services;

import android.app.Activity;
import android.content.Context;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.mygpsapp.R;
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
 * Created by Philip on 11/10/2015.
 */
public class DroneTracker extends Activity implements TowerListener, DroneListener{
    private Context mContext;
    private ControlTower mControlTower;
    private Drone mDrone;
    private int droneType = Type.TYPE_UNKNOWN;
    private Handler handler = new Handler();

    private double altitude;
    private double speed;

    private Spinner modeSpinner;

    public DroneTracker(Context context) {
        this.mContext = context;
        mControlTower = new ControlTower(mContext);
        mDrone = new Drone(mContext);
    }

    @Override
    public void onTowerConnected() {
        mControlTower.registerDrone(mDrone, handler);
        mDrone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {

    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                alertUser("Drone connected");
                updateConnectButton(mDrone.isConnected());
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("Drone disconnected");
                updateConnectButton(mDrone.isConnected());
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

            case AttributeEvent.STATE_UPDATED:
            case AttributeEvent.STATE_ARMING:
                updateArmButton();
                break;

            case AttributeEvent.SPEED_UPDATED:
            case AttributeEvent.ALTITUDE_UPDATED:
            case AttributeEvent.HOME_UPDATED:
                getDroneData();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDroneServiceInterrupted(String errorMsg) {

    }

    public void onBtnConnectTap(View view) {
        if (mDrone.isConnected()) {
            mDrone.disconnect();
        } else {
            Bundle extraParams = new Bundle();
            extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, 57600);
            ConnectionParameter mConnectionParameter = new ConnectionParameter(ConnectionType.TYPE_USB, extraParams, null);
            mDrone.connect(mConnectionParameter);
        }
    }

    public void onFlightModeSelected(VehicleMode vm) {
        mDrone.changeVehicleMode(vm);
    }

    protected void updateVehicleModeForType(int droneType) {
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

    public void connect() {
        mControlTower.connect(this);
    }

    public void disconnectDrone() {
        if (mDrone.isConnected()) {
            mDrone.disconnect();

        }
        mControlTower.unregisterDrone(mDrone);
        mControlTower.disconnect();
    }

    public void updateConnectButton(boolean isConnected) {
        if (isConnected) {
            //btnConnect.setText("Disconnect");
        } else {
            //btnConnect.setText("Connect to Drone");
        }
    }

    public void getDroneData() {
        if (mDrone.isConnected()) {
            Altitude droneAltitude = mDrone.getAttribute(AttributeType.ATTITUDE);
            Speed droneSpeed = mDrone.getAttribute(AttributeType.SPEED);
            altitude = droneAltitude.getAltitude();
            speed = droneSpeed.getGroundSpeed();
            updateDistanceFromHome();

        }
    }

    protected void updateDistanceFromHome() {
        TextView distanceTextView = (TextView)findViewById(R.id.distanceDroneText);
        Altitude droneAltitude = mDrone.getAttribute(AttributeType.ALTITUDE);
        double vehicleAltitude = droneAltitude.getAltitude();
        Gps droneGps = mDrone.getAttribute(AttributeType.GPS);
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

    public void onArmButtonTap(View view) {
        Button thisBtn = (Button)view;
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
        Button armButton = (Button) findViewById(R.id.btnArmTakeOff);

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
    public void alertUser(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
