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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Date;


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
    //private long elaspedTime;
    Handler handler;
    //Ringtone ringtoneSound;
    //PowerManager powerManager;
    //PowerManager.WakeLock wakeLock;
    boolean isScreenOff = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOff = true;
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOff = false;
            }
        }
    };


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
        //powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyWakeLock");
        //wakeLock.acquire();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(TAG, "onStartCommand is called");
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
        //notificationManager.notify(TIMER_NOTIFICATION, builder.build());

        //final DateFormat formatter = DateFormat.getTimeInstance();
        timer = new CountDownTimer(countTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                hour = (int) (millisUntilFinished / 1000 / 60 / 60);
                minute = (int) (millisUntilFinished / 1000 / 60 % 60);
                second = (int) (millisUntilFinished / 1000 % 60);

                builder.setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
                notificationManager.notify(TIMER_NOTIFICATION, builder.build());
                //Date now = new Date();
                //Log.i(TAG, formatter.format(now));
                Log.i(TAG, "" + SystemClock.uptimeMillis());
            }

            @Override
            public void onFinish() {
                Intent serviceIntent = new Intent(TimerNotificationService.this, TimerAlarmService.class);
                serviceIntent.putExtra(KEY_COUNT_TIME, countTime);
                startService(serviceIntent);
                if (isScreenOff) {
                    Intent activityIntent = new Intent(TimerNotificationService.this, TimerActivity.class);
                    startActivity(activityIntent);
                }
                unregisterReceiver(receiver);
                stopSelf();
                //wakeLock.release();
                Log.i(TAG, "onFinished is called");
                Log.i(TAG, "isScreenOff =" + isScreenOff);
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
        //Log.i(TAG, "onDestroy is called");
    }
}
