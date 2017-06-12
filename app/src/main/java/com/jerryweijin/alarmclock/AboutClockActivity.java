package com.jerryweijin.alarmclock;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jerry on 6/7/2017.
 */

public class AboutClockActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_clock);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.aboutClockPageTitle);
    }
}
