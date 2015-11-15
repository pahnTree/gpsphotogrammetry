package com.example.philip.mygpsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.philip.mygpsapp.R;
import com.example.philip.mygpsapp.fragments.CameraFragment;
import com.example.philip.mygpsapp.fragments.DetailsFragment;
import com.example.philip.mygpsapp.fragments.HelpFragment;
import com.example.philip.mygpsapp.fragments.ImageFragment;

/**
 * Starts the App
 * Starts on the MainFragment page when app starts
 * Can choose between the tabs to move around app and to start activities
 */
public class MainActivity extends AppCompatActivity {


    private FragmentTabHost mTabHost;
    private Button btnSettings;
    private boolean doubleBackPressToExitOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        toolbar.showOverflowMenu();




        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //mTabHost.addTab(mTabHost.newTabSpec("tabSettings").setIndicator("Settings", null),
        //        SettingsFragment.class, null);
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

        //btnSettings = (Button) findViewById(R.id.btnSettings);
        //btnSettings.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).addToBackStack("settings").commit();
        //    }
        //});

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackPressToExitOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackPressToExitOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackPressToExitOnce = false;
            }
        }, 2000);
    }

    /*
     * Menu toolbar methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_help:
                setContentView(R.layout.fragment_help);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
