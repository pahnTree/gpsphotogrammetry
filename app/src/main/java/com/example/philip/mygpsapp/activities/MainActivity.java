package com.example.philip.mygpsapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TabWidget;

import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.fragments.CameraFragment;
import com.example.philip.mygpsapp.fragments.DetailsFragment;
import com.example.philip.mygpsapp.fragments.HelpFragment;
import com.example.philip.mygpsapp.fragments.ImageFragment;
import com.example.philip.mygpsapp.fragments.SettingsFragment;

/**
 * Starts the App
 * Starts on the MainFragment page when app starts
 * Can choose between the tabs to move around app and to start activities
 */
public class MainActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);


        mTabHost.addTab(mTabHost.newTabSpec("tabSettings").setIndicator("Settings", null),
                SettingsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabDetails").setIndicator("Details", null),
                DetailsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabCamera").setIndicator("Camera", null),
                CameraFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabImage").setIndicator("Image", null),
                ImageFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tabHelp").setIndicator("Help", null),
                HelpFragment.class, null);

        TabWidget tw = (TabWidget) findViewById(android.R.id.tabs);
        LinearLayout ll = (LinearLayout) tw.getParent();
        HorizontalScrollView hs = new HorizontalScrollView(this);
        hs.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        ll.addView(hs, 0);
        ll.removeView(tw);
        hs.addView(tw);
        hs.setHorizontalScrollBarEnabled(false);
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
