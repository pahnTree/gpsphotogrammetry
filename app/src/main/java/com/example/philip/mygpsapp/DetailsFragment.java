package com.example.philip.mygpsapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Phil on 11/11/2015.
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);
        getTextViews();

        // Code to add
        runningText.setText("Running...");
        runningText.setText("Stopped");
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
}
