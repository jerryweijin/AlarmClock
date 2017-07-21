package com.jerryweijin.alarmclock;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;



/**
 * Created by Jerry on 6/11/17.
 */

public class TimerFragment extends Fragment {
    public static final String TAG = TimerFragment.class.getSimpleName();
    private static final String KEY_TOTAL_COUNT_TIME = "KEY_TOTAL_COUNT_TIME";
    private static final String KEY_REMAIN_COUNT_TIME = "KEY_REMAIN_COUNT_TIME";
    private NumberPicker hourPicker, minutePicker, secondPicker;
    private Button startButton, pauseButton, cancelButton;
    private TextView countDownTextView, hourMinuteSeparator, minuteSecondSeparator, hourLabel, minuteLabel, secondLabel;
    private CountDownTimer countDownTimer;
    private int totalCountTime, hour, minute, second, remainingTime;
    private Context context;
    private TimerNotificationService timerNotificationService;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerNotificationService.LocalBinder localBinder = (TimerNotificationService.LocalBinder) service;
            timerNotificationService = localBinder.getService();
            int time = timerNotificationService.remainTime;
            if (time != 0) {
                startCountDownTimer(time);
                displayCountDown(time);
                startButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
            }
            Log.i(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startCountDownTimer(totalCountTime);
            displayCountDown(totalCountTime);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        hourPicker = (NumberPicker) view.findViewById(R.id.hour);
        minutePicker = (NumberPicker) view.findViewById(R.id.minute);
        secondPicker = (NumberPicker) view.findViewById(R.id.second);
        startButton = (Button) view.findViewById(R.id.startButton);
        pauseButton = (Button) view.findViewById(R.id.pauseButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        countDownTextView = (TextView) view.findViewById(R.id.countDownTextView);
        hourMinuteSeparator = (TextView) view.findViewById(R.id.hourMinuteSeparator);
        minuteSecondSeparator = (TextView) view.findViewById(R.id.minuteSecondSeparator);
        hourLabel = (TextView) view.findViewById(R.id.hourLabel);
        minuteLabel = (TextView) view.findViewById(R.id.minuteLabel);
        secondLabel = (TextView) view.findViewById(R.id.secondLabel);
        context = getContext();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second = secondPicker.getValue();
                minute = minutePicker.getValue();
                hour = hourPicker.getValue();
                totalCountTime = hour*60*60*1000 + minute*60*1000 + second*1000;
                if (totalCountTime != 0) {
                    startCountDownTimer(totalCountTime);
                    displayCountDown(totalCountTime);

                    Intent intent = new Intent(context, TimerNotificationService.class);
                    intent.putExtra(TimerNotificationService.KEY_TOTAL_COUNT_TIME, totalCountTime);
                    intent.putExtra(TimerNotificationService.KEY_REMAIN_COUNT_TIME, totalCountTime);
                    context.startService(intent);
                    startButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);;
                    editor.putInt(KEY_TOTAL_COUNT_TIME, totalCountTime);
                    editor.putInt(KEY_REMAIN_COUNT_TIME, 0);
                    editor.commit();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = (String) pauseButton.getText();
                if (buttonText.equals("PAUSE")) {
                    startButton.setVisibility(View.INVISIBLE);
                    pauseButton.setText("RESUME");
                    stopCountDownTimer();
                    timerNotificationService.stopTimer();
                    timerNotificationService.stopSelf();
                    editor.putInt(KEY_REMAIN_COUNT_TIME, remainingTime);
                    editor.commit();
                } else {
                    pauseButton.setText("PAUSE");
                    startCountDownTimer(remainingTime);
                    //timerNotificationService.startTimer(remainingTime);
                    Intent intent = new Intent(context, TimerNotificationService.class);
                    intent.putExtra(TimerNotificationService.KEY_TOTAL_COUNT_TIME, totalCountTime);
                    intent.putExtra(TimerNotificationService.KEY_REMAIN_COUNT_TIME, remainingTime);
                    context.startService(intent);
                    editor.putInt(KEY_REMAIN_COUNT_TIME, 0);
                    editor.commit();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                pauseButton.setText("PAUSE");
                stopCountDownTimer();
                hideCountDown();
                timerNotificationService.stopTimer();
                timerNotificationService.stopSelf();
                editor.putInt(KEY_REMAIN_COUNT_TIME, 0);
                editor.commit();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TimerAlarmService.ACTION_RESTART);
        context.registerReceiver(receiver, intentFilter);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        totalCountTime = preferences.getInt(KEY_TOTAL_COUNT_TIME, 0);
        remainingTime = preferences.getInt(KEY_REMAIN_COUNT_TIME, 0);
        configurePickers(totalCountTime);
        if (remainingTime != 0) {
            displayCountDown(remainingTime);
            startButton.setVisibility(View.INVISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            pauseButton.setText("RESUME");
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.i(TAG, "Fragment onStart is called");
        //Bind to the service
        Intent intent = new Intent(context, TimerNotificationService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_ADJUST_WITH_ACTIVITY);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Log.i(TAG, "On Resume is called");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.i(TAG, "On Pause is called");
    }

    @Override
    public void onStop() {
        super.onStop();
        stopCountDownTimer();
        context.unbindService(serviceConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }

    private void configurePickers(int time) {
        hour = time / 1000 / 60 / 60;
        minute = time / 1000 / 60 % 60;
        second = time / 1000 % 60;

        String[] hours = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(hours.length-1);
        hourPicker.setDisplayedValues(hours);
        hourPicker.setValue(hour);

        String[] minutes = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(minutes.length-1);
        minutePicker.setDisplayedValues(minutes);
        minutePicker.setValue(minute);

        String[] seconds = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(seconds.length-1);
        secondPicker.setDisplayedValues(seconds);
        secondPicker.setValue(second);
    }

    private void startCountDownTimer(int time) {
        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeString = convertTimeToString(millisUntilFinished);
                countDownTextView.setText(timeString);
                remainingTime = (int) millisUntilFinished;
            }

            @Override
            public void onFinish() {
                hideCountDown();
                startButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
            }
        };
        countDownTimer.start();
    }

    private String convertTimeToString(long time) {
        hour = (int) (time / 1000 / 60 / 60);
        minute = (int) (time / 1000 / 60 % 60);
        second = (int) (time / 1000 % 60);
        return String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second);
    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void hideCountDown() {
        countDownTextView.setVisibility(View.INVISIBLE);
        hourPicker.setVisibility(View.VISIBLE);
        minutePicker.setVisibility(View.VISIBLE);
        secondPicker.setVisibility(View.VISIBLE);
        hourMinuteSeparator.setVisibility(View.VISIBLE);
        minuteSecondSeparator.setVisibility(View.VISIBLE);
        hourLabel.setVisibility(View.VISIBLE);
        minuteLabel.setVisibility(View.VISIBLE);
        secondLabel.setVisibility(View.VISIBLE);
    }

    private void displayCountDown(int time) {
        String timeString = convertTimeToString(time);
        countDownTextView.setText(timeString);
        countDownTextView.setVisibility(View.VISIBLE);
        hourPicker.setVisibility(View.INVISIBLE);
        minutePicker.setVisibility(View.INVISIBLE);
        secondPicker.setVisibility(View.INVISIBLE);
        hourMinuteSeparator.setVisibility(View.INVISIBLE);
        minuteSecondSeparator.setVisibility(View.INVISIBLE);
        hourLabel.setVisibility(View.INVISIBLE);
        minuteLabel.setVisibility(View.INVISIBLE);
        secondLabel.setVisibility(View.INVISIBLE);
    }

}
