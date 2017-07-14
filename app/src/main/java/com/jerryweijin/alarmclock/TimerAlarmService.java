package com.jerryweijin.alarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Jerry on 6/25/17.
 */

public class TimerAlarmService extends Service {
    private static final int REQUEST_CODE = 1;
    private static final int HEADS_UP_NOTIFICATION = 2;
    public static final String ACTION_DISMISS = "com.jerryweijin.alarmclock.intent.action.dismiss";
    public static final String ACTION_RESTART = "com.jerryweijin.alarmclock.intent.action.restart";
    public static final String TAG = TimerAlarmService.class.getSimpleName();
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
    private RemoteViews remoteViews;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ringtoneSound.stop();
            notificationManager.cancel(HEADS_UP_NOTIFICATION);
            if (intent.getAction().equals(ACTION_RESTART)) {
                Intent serviceIntent = new Intent(TimerAlarmService.this, TimerNotificationService.class);
                serviceIntent.putExtra(TimerFragment.KEY_COUNT_TIME, countTime);
                startService(serviceIntent);

            }
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DISMISS);
        intentFilter.addAction(ACTION_RESTART);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        countTime = intent.getIntExtra(TimerFragment.KEY_COUNT_TIME, 0);

        Intent activityIntent = new Intent(this, TimerActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, activityIntent, 0);

        Intent dismissIntent = new Intent(ACTION_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent restartIntent = new Intent(ACTION_RESTART);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, restartIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtoneSound = RingtoneManager.getRingtone(this, ringtoneUri);
        if (ringtoneSound != null) {
            ringtoneSound.play();
        }

        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_heads_up_notification);
        remoteViews.setImageViewResource(R.id.notificationIcon, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.contentTitle, "Time's up up up");
        startTime = SystemClock.elapsedRealtime();
        remoteViews.setChronometer(R.id.contentText, startTime, "-%s", true);
        remoteViews.setOnClickPendingIntent(R.id.dismissButton, dismissPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.restartButton, restartPendingIntent);
        remoteViews.setTextViewText(R.id.appName, "CLOCK");

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer is up")
                .setContentText("-" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second))
                .setContentIntent(activityPendingIntent)
                .setVibrate(new long[0])
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_ALARM)
                .setFullScreenIntent(activityPendingIntent, true)
                .setCustomHeadsUpContentView(remoteViews)
                .setAutoCancel(true);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(HEADS_UP_NOTIFICATION, builder.build());

        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
    }
}
