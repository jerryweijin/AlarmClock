package com.jerryweijin.alarmclock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
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
    private long startTime;
    private long elapsedTime;
    private Handler handler = new Handler();
    private long hour;
    private long minute;
    private long second;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            hour = (elapsedTime / 1000 / 60 / 60);
            minute = (elapsedTime / 1000 / 60 % 60);
            second = (elapsedTime / 1000 % 60);
            elapsedTimeTextView.setText("-" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
            handler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        elapsedTimeTextView = (TextView) findViewById(R.id.elapsedTimeTextView);
        restartButton = (Button) findViewById(R.id.restartButton);
        cancelButton = (ImageButton) findViewById(R.id.cancelButton);

        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restartIntent = new Intent(TimerAlarmService.ACTION_RESTART);
                sendBroadcast(restartIntent);
                //Make a toast to say 00:15 countDownTimer restarted
                handler.removeCallbacks(runnable);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dismissIntent = new Intent(TimerAlarmService.ACTION_DISMISS);
                sendBroadcast(dismissIntent);
                handler.removeCallbacks(runnable);
                finish();
            }
        });
    }
}
