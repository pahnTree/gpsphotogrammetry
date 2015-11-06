package com.example.philip.mygpsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnShowLocation;
    CheckBox externalGPSCheckBox;
    TextView latitudeText;
    TextView longitudeText;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);
        externalGPSCheckBox = (CheckBox) findViewById(R.id.externalGPSCheckBox);
        if (externalGPSCheckBox.isChecked()) {
            externalGPSCheckBox.setChecked(false);
            gps.isExternalGPSEnabled = true;
        }
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();


                    latitudeText.setText("Lat: " + latitude);
                    longitudeText.setText("Lon: " + longitude);


                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else if (gps.isExternalGPSEnabled){
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings

                }

            }
        });

    }
}
