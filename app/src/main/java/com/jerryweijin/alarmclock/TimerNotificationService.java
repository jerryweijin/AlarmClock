package com.jerryweijin.alarmclock;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by Jerry on 6/18/17.
 */

public class TimerNotificationService extends IntentService {
    public static final String TAG = TimerNotificationService.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private static final int TIMER_NOTIFICATION = 2;
    int countTime;
    int hour;
    int minute;
    int second;
    CountDownTimer timer;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    public TimerNotificationService() {
        super("TimeNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent is called");
        countTime = intent.getIntExtra(TimerFragment.KEY_COUNTER_TIME, 0);
        hour = (int) (countTime / 1000 / 60 / 60);
        minute = (int) (countTime / 1000 / 60 % 60);
        second = (int) (countTime / 1000 % 60);
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, activityIntent, 0);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer")
                .setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second))
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TIMER_NOTIFICATION, builder.build());

        timer = new CountDownTimer(countTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                hour = (int) (millisUntilFinished / 1000 / 60 / 60);
                minute = (int) (millisUntilFinished / 1000 / 60 % 60);
                second = (int) (millisUntilFinished / 1000 % 60);
                builder.setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
                notificationManager.notify(TIMER_NOTIFICATION, builder.build());
                Log.i(TAG, "onTick is called");
            }

            @Override
            public void onFinish() {
                Intent serviceIntent = new Intent(TimerNotificationService.this, TimerNotificationService.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(TimerNotificationService.this, REQUEST_CODE, serviceIntent, 0);
                NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_action_name, "Dismiss", pendingIntent).build();
                builder.setContentText("00 : 00 : 00")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .addAction(action)
                        .setPriority(Notification.PRIORITY_MAX);

                notificationManager.notify(TIMER_NOTIFICATION, builder.build());
                Log.i(TAG, "onFinished is called");
            }
        };
        timer.start();

    }
}
