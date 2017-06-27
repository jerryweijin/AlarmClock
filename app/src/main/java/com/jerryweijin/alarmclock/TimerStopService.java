package com.jerryweijin.alarmclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

/**
 * Created by Jerry on 6/27/17.
 */

public class TimerStopService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
