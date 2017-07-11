package com.jerryweijin.alarmclock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Jerry on 7/1/17.
 */

public class TimerActivity extends AppCompatActivity {
    private TextView elapsedTimeTextView;
    private Button restartButton;
    private ImageButton cancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyWakeLock");
        //wakeLock.acquire();

        elapsedTimeTextView = (TextView) findViewById(R.id.elapsedTimeTextView);
        restartButton = (Button) findViewById(R.id.restartButton);
        cancelButton = (ImageButton) findViewById(R.id.cancelButton);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restartIntent = new Intent(TimerAlarmService.ACTION_RESTART);
                sendBroadcast(restartIntent);
                //Make a toast to say 00:15 countDownTimer restarted

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dismissIntent = new Intent(TimerAlarmService.ACTION_DISMISS);
                sendBroadcast(dismissIntent);
                finish();
            }
        });
    }
}
