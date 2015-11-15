package com.example.philip.mygpsapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.philip.mygpsapp.R;

/**
 * Created by Phil on 11/11/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String PREFERENCES_FILE_NAME = "preferences";
    private SharedPreferences settings;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        addPreferencesFromResource(R.xml.preferences);
        settings = mContext.getSharedPreferences(PREFERENCES_FILE_NAME,0);
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) getPreferenceManager().findPreference("checkbox_pixhawk_connect");
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                Log.d("Preference", preference.getKey() + " changed to " + newVal.toString());
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("checkbox_pixhawk_connect", (boolean)newVal);
                editor.commit();
                return true;
            }
        });
    }





}
