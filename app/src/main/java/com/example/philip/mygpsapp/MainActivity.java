package com.example.philip.mygpsapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.Fragment;

public class MainActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tabMain").setIndicator("Main", null),
                MainFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabSettings").setIndicator("Settings", null),
                SettingsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabDetails").setIndicator("Details", null),
                DetailsFragment.class, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

}
