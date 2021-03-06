package com.jerryweijin.alarmclock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by Jerry on 6/18/17.
 */

public class TimerNotificationService extends Service {
    public static final String TAG = TimerNotificationService.class.getSimpleName();
    public static final String KEY_TOTAL_COUNT_TIME = "KEY_TOTAL_COUNT_TIME";
    public static final String KEY_REMAIN_COUNT_TIME = "KEY_REMAIN_COUNT_TIME";
    private boolean isServiceForeground = true;
    private static final int REQUEST_CODE = 1;
    public static final int TIMER_NOTIFICATION = 2;
    int totalCountTime, remainTime;
    int hour;
    int minute;
    int second;
    CountDownTimer timer;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    private TimerNotificationService.LocalBinder localBinder = new LocalBinder();

    @Override
    public void onCreate() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, activityIntent, 0);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer")
                .setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second))
                .setContentIntent(pendingIntent);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i(TAG, "onCreate is called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(TAG, "onStartCommand is called");
        totalCountTime = intent.getIntExtra(KEY_TOTAL_COUNT_TIME, 0);
        remainTime = intent.getIntExtra(KEY_REMAIN_COUNT_TIME, 0);
        startTimer(remainTime);

        if (isServiceForeground) {
            startForeground(TIMER_NOTIFICATION, builder.build());
        }


        return Service.START_REDELIVER_INTENT;
    }

    public class LocalBinder extends Binder {
        public TimerNotificationService getService() {
            return TimerNotificationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        isServiceForeground = false;
        Log.i(TAG, "onBind is called");
        return localBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        isServiceForeground = false;
        Log.i(TAG, "onRebind is called");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        startForeground(TIMER_NOTIFICATION, builder.build());
        isServiceForeground = true;
        Log.i(TAG, "onUnbind is called");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service onDestroy is called");
    }

    public void startTimer(int time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isServiceForeground) {
                    hour = (int) (millisUntilFinished / 1000 / 60 / 60);
                    minute = (int) (millisUntilFinished / 1000 / 60 % 60);
                    second = (int) (millisUntilFinished / 1000 % 60);

                    builder.setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
                    notificationManager.notify(TIMER_NOTIFICATION, builder.build());
                }
                remainTime = (int) millisUntilFinished;
                //Log.i(TAG, "" + SystemClock.uptimeMillis());
            }

            @Override
            public void onFinish() {
                Intent serviceIntent = new Intent(TimerNotificationService.this, TimerAlarmService.class);
                serviceIntent.putExtra(KEY_TOTAL_COUNT_TIME, totalCountTime);
                startService(serviceIntent);
                stopSelf();
                //Log.i(TAG, "onFinished is called");
                //Log.i(TAG, "isScreenOff =" + isScreenOff);
            }
        };
        timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


}
