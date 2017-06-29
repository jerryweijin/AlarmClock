package com.jerryweijin.alarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Chronometer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jerry on 6/25/17.
 */

public class TimerAlarmService extends Service implements Chronometer.OnChronometerTickListener{
    private static final int REQUEST_CODE = 1;
    private static final int HEADS_UP_NOTIFICATION = 2;
    public static final String ACTION_DISMISS = "com.jerryweijin.alarmclock.intent.action.dismiss";
    private Ringtone ringtoneSound;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int countTime;
    private int hour;
    private int minute;
    private int second;
    private long startTime;
    private Handler handler;
    private long elaspedTime;
    String action;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            elaspedTime = SystemClock.uptimeMillis() - startTime;
            hour = (int) (elaspedTime / 1000 / 60 / 60);
            minute = (int) (elaspedTime / 1000 / 60 % 60);
            second = (int) (elaspedTime / 1000 % 60);

            builder.setContentText("-" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
            notificationManager.notify(HEADS_UP_NOTIFICATION, builder.build());

            handler.postDelayed(this, 1000);
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ringtoneSound.stop();
            //handler.removeCallbacks(runnable);
            notificationManager.cancel(HEADS_UP_NOTIFICATION);
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        //handler = new Handler();
        registerReceiver(receiver, new IntentFilter(ACTION_DISMISS));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        countTime = intent.getIntExtra(TimerNotificationService.KEY_COUNT_TIME, 0);

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, activityIntent, 0);

        Intent dismissIntent = new Intent(ACTION_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, dismissIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action dismissAction = new NotificationCompat.Action.Builder(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent).build();

        Intent restartIntent = new Intent(this, TimerNotificationService.class);
        restartIntent.putExtra(TimerNotificationService.KEY_COUNT_TIME, countTime);
        PendingIntent restartPendingIntent = PendingIntent.getService(this, REQUEST_CODE, restartIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action restartAction = new NotificationCompat.Action.Builder(R.drawable.ic_restart, "Restart", restartPendingIntent).build();

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtoneSound = RingtoneManager.getRingtone(this, ringtoneUri);
        if (ringtoneSound != null) {
            ringtoneSound.play();
        }

        Chronometer chronometer = new Chronometer(this);
        chronometer.setOnChronometerTickListener(this);
        chronometer.start();

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer is up")
                .setContentText("-" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[0])
                .addAction(dismissAction)
                .addAction(restartAction)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setContent(chronometer);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(HEADS_UP_NOTIFICATION, builder.build());
        startTime = SystemClock.uptimeMillis();
        //handler.postDelayed(runnable, 1000);

        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        elaspedTime = SystemClock.uptimeMillis() - startTime;
        hour = (int) (elaspedTime / 1000 / 60 / 60);
        minute = (int) (elaspedTime / 1000 / 60 % 60);
        second = (int) (elaspedTime / 1000 % 60);

        builder.setContentText("-" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
        notificationManager.notify(HEADS_UP_NOTIFICATION, builder.build());
    }
}
