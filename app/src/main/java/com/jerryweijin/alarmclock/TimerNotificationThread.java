package com.jerryweijin.alarmclock;

import android.os.Looper;

/**
 * Created by Jerry on 6/19/17.
 */

public class TimerNotificationThread extends Thread {
    public TimerNotificationHandler handler;

    @Override
    public void run() {
        Looper.prepare();
        handler = new TimerNotificationHandler();
        Looper.loop();
    }
}
