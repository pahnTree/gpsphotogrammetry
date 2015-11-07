package com.example.philip.mygpsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnShowLocation;
    private Button btnCancel;
    private CheckBox externalGPSCheckBox;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView altitudeText;
    private TextView azimuthText;
    private TextView pitchText;
    private TextView rollText;


    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);
        altitudeText = (TextView) findViewById(R.id.altitudeText);
        azimuthText = (TextView) findViewById(R.id.azimuthText);
        pitchText = (TextView) findViewById(R.id.pitchText);
        rollText = (TextView) findViewById(R.id.rollText);

        externalGPSCheckBox = (CheckBox) findViewById(R.id.externalGPSCheckBox);
        if (externalGPSCheckBox.isChecked()) {
            externalGPSCheckBox.setChecked(false);
            gps.isExternalGPSEnabled = true;
        }
        if (!externalGPSCheckBox.isChecked()) {
            externalGPSCheckBox.setChecked(true);
            gps.isExternalGPSEnabled = false;
        }
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

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
                    if (externalGPSCheckBox.isChecked() && gps.isExternalGPSEnabled == false) {
                        gps.showSettingsAlert();
                    }

                }

            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps.stopUsingGPS();
            }
        });

    }
}
