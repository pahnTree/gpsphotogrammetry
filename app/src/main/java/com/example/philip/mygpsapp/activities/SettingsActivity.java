package com.example.philip.mygpsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.fragments.SettingsFragment;

import java.util.List;

/**
 * Created by Phil on 11/14/2015.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
