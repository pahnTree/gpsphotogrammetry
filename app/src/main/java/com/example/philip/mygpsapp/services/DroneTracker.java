package com.example.philip.mygpsapp.services;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.MAVLink.common.msg_global_position_int;
import com.example.philip.mygpsapp.R;
import com.o3dr.android.client.MavlinkObserver;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.mavlink.MavlinkMessageWrapper;

import java.security.Provider;

/**
 * Created by Philip on 11/17/2015.
 */
public class DroneTracker extends Activity implements LocationListener {

    // https://github.com/DroidPlanner/Tower/blob/develop/Android/src/org/droidplanner/android/activities/LocatorActivity.java

    private MavlinkObserver mavlinkObserver;
    private Context mContext;
    private LatLong lastDronePosition;
    private double lastDroneAzimuth;
    private msg_global_position_int selectedMsg;

    private double latitude;
    private double longitude;

    public DroneTracker(Context context) {
        mContext = context;
        mavlinkObserver = new MavlinkObserver() {
            @Override
            public void onMavlinkMessageReceived(MavlinkMessageWrapper mavlinkMessageWrapper) {
                selectedMsg = (msg_global_position_int) mavlinkMessageWrapper.getMavLinkMessage();
                setSelectedMsg(selectedMsg);
            }
        };

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastDronePosition = new LatLong(location.getLatitude(),location.getLongitude());
        lastDroneAzimuth = location.getBearing();
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

    public void setSelectedMsg(msg_global_position_int msg) {
        selectedMsg = msg;

        if (msg != null)
            lastDronePosition = coordFromMsgGlobalPositionInt(selectedMsg);
        else
            lastDronePosition = new LatLong(0, 0);
    }

    private LatLong coordFromMsgGlobalPositionInt(msg_global_position_int msg) {
        double lat = msg.lat;
        lat /= 1E7;
        this.latitude = lat;
        TextView droneLat = (TextView) findViewById(R.id.latitudeDroneDegText);
        droneLat.setText(String.format("%.5f", lat) + "\u00b0");

        double lon = msg.lon;
        lon /= 1E7;
        this.longitude = lon;
        TextView droneLon = (TextView) findViewById(R.id.longitudeDroneDegText);
        droneLon.setText(String.format("%.5f", lon) + "\u00b0");

        return new LatLong(lat, lon);
    }

    public LatLong getLastDronePosition() { return lastDronePosition; }

    public double getLastDroneAzimuth() { return lastDroneAzimuth; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

}
