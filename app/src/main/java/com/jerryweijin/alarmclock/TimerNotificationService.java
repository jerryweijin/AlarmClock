package com.jerryweijin.alarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by Jerry on 6/18/17.
 */

public class TimerNotificationService extends Service {
    public static final String TAG = TimerNotificationService.class.getSimpleName();
    public static final String KEY_COUNT_TIME = "KEY_COUNT_TIME";
    private static final int REQUEST_CODE = 1;
    private static final int TIMER_NOTIFICATION = 2;
    int countTime;
    int hour;
    int minute;
    int second;
    CountDownTimer timer;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    private long elaspedTime;
    Handler handler;
    Ringtone ringtoneSound;



    @Override
    public void onCreate() {
        /*
        TimerNotificationThread thread = new TimerNotificationThread();
        thread.setName("TimerNotificationThread");
        thread.start();
        while(thread.handler == null) {}
        handler = thread.handler;
        Log.i(TAG, "onCreate is called");
        */
        handler = new Handler();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand is called");
        countTime = intent.getIntExtra(KEY_COUNT_TIME, 0);

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
        startForeground(TIMER_NOTIFICATION, builder.build());

        timer = new CountDownTimer(countTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                hour = (int) (millisUntilFinished / 1000 / 60 / 60);
                minute = (int) (millisUntilFinished / 1000 / 60 % 60);
                second = (int) (millisUntilFinished / 1000 % 60);

                builder.setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
                notificationManager.notify(TIMER_NOTIFICATION, builder.build());
            }

            @Override
            public void onFinish() {
                Intent serviceIntent = new Intent(TimerNotificationService.this, TimerAlarmService.class);
                serviceIntent.putExtra(KEY_COUNT_TIME, countTime);
                startService(serviceIntent);
                stopSelf();
                Log.i(TAG, "onFinished is called");
            }
        };
        timer.start();

        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy is called");
    }
}
