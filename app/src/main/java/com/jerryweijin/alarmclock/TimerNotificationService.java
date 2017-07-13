package com.jerryweijin.alarmclock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private static final int REQUEST_CODE = 1;
    public static final int TIMER_NOTIFICATION = 2;
    int countTime = 0;
    int hour;
    int minute;
    int second;
    CountDownTimer timer;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
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
    private TimerNotificationService.LocalBinder localBinder = new LocalBinder();
    public boolean isServiceForground = false;

    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(TAG, "onStartCommand is called");
        countTime = intent.getIntExtra(TimerFragment.KEY_COUNT_TIME, 0);

        hour = (int) (countTime / 1000 / 60 / 60);
        minute = (int) (countTime / 1000 / 60 % 60);
        second = (int) (countTime / 1000 % 60);

        timer = new CountDownTimer(countTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isServiceForground) {
                    hour = (int) (millisUntilFinished / 1000 / 60 / 60);
                    minute = (int) (millisUntilFinished / 1000 / 60 % 60);
                    second = (int) (millisUntilFinished / 1000 % 60);

                    builder.setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
                    notificationManager.notify(TIMER_NOTIFICATION, builder.build());
                }
                countTime = (int) millisUntilFinished;
                //Log.i(TAG, "" + SystemClock.uptimeMillis());
            }

            @Override
            public void onFinish() {
                Intent serviceIntent = new Intent(TimerNotificationService.this, TimerAlarmService.class);
                serviceIntent.putExtra(TimerFragment.KEY_COUNT_TIME, countTime);
                startService(serviceIntent);
                if (isScreenOff) {
                    Intent activityIntent = new Intent(TimerNotificationService.this, TimerActivity.class);
                    startActivity(activityIntent);
                }
                stopSelf();
                //Log.i(TAG, "onFinished is called");
                //Log.i(TAG, "isScreenOff =" + isScreenOff);
            }
        };
        timer.start();

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
        return localBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i(TAG, "Service onDestroy is called");
    }

    public void createNotification() {
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, activityIntent, 0);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Timer")
                .setContentText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second))
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(TIMER_NOTIFICATION, builder.build());
    }

}
