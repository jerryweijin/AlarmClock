package com.jerryweijin.alarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    NumberPicker secondPicker;
    Button startButton;
    TextView countDownTextView;
    CountDownTimer timer;
    TextView hourMinuteSeparator;
    TextView minuteSecondSeparator;
    TextView hourLabel;
    TextView minuteLabel;
    TextView secondLabel;
    int countTime;
    int hour;
    int minute;
    int second;
    Context context;
    boolean isTimerSet = false;
    boolean isTimerServiceRunning = false;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        hourPicker = (NumberPicker) view.findViewById(R.id.hour);
        minutePicker = (NumberPicker) view.findViewById(R.id.minute);
        secondPicker = (NumberPicker) view.findViewById(R.id.second);
        startButton = (Button) view.findViewById(R.id.startButton);
        countDownTextView = (TextView) view.findViewById(R.id.countDownTextView);
        hourMinuteSeparator = (TextView) view.findViewById(R.id.hourMinuteSeparator);
        minuteSecondSeparator = (TextView) view.findViewById(R.id.minuteSecondSeparator);
        hourLabel = (TextView) view.findViewById(R.id.hourLabel);
        minuteLabel = (TextView) view.findViewById(R.id.minuteLabel);
        secondLabel = (TextView) view.findViewById(R.id.secondLabel);
        context = getContext();

        String[] hours = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(hours.length-1);
        hourPicker.setDisplayedValues(hours);

        String[] minutes = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(minutes.length-1);
        minutePicker.setDisplayedValues(minutes);

        String[] seconds = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(seconds.length-1);
        secondPicker.setDisplayedValues(seconds);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTimerSet = true;
                second = secondPicker.getValue();
                minute = minutePicker.getValue();
                hour = hourPicker.getValue();
                countTime = hour*60*60*1000 + minute*60*1000 + second*1000;
                timer = new CountDownTimer(countTime, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (isTimerServiceRunning == false) {
                            hour = (int) (millisUntilFinished / 1000 / 60 / 60);
                            minute = (int) (millisUntilFinished / 1000 / 60 % 60);
                            second = (int) (millisUntilFinished / 1000 % 60);
                            countDownTextView.setText("" + String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
                            countTime = (int) millisUntilFinished;
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (isTimerServiceRunning == false) {
                            countDownTextView.setText("00 : 00 : 00");

                            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            Ringtone ringtoneSound = RingtoneManager.getRingtone(getActivity(), ringtoneUri);

                            if (ringtoneSound != null) {
                                ringtoneSound.play();
                            }
                            countDownTextView.setVisibility(View.INVISIBLE);
                            hourPicker.setVisibility(View.VISIBLE);
                            minutePicker.setVisibility(View.VISIBLE);
                            secondPicker.setVisibility(View.VISIBLE);
                            hourMinuteSeparator.setVisibility(View.VISIBLE);
                            minuteSecondSeparator.setVisibility(View.VISIBLE);
                            hourLabel.setVisibility(View.VISIBLE);
                            minuteLabel.setVisibility(View.VISIBLE);
                            secondLabel.setVisibility(View.VISIBLE);
                            isTimerSet = false;

                            builder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Timer")
                                    .setContentText("Time is up")
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setDefaults(Notification.DEFAULT_ALL);
                            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(11, builder.build());

                        }
                    }
                };
                timer.start();
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
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "On Start is called");
        //Stop the service
        Intent intent = new Intent(context, TimerNotificationService.class);
        context.stopService(intent);
        //Update counter and display the right time.
        isTimerServiceRunning = false;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "On Resume is called");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "On Pause is called");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "On Stop is called");
        //Get time from timer
        if (isTimerSet) {
            Intent intent = new Intent(context, TimerNotificationService.class);
            intent.putExtra(TimerNotificationService.KEY_COUNT_TIME, countTime);
            //Start the service
            context.startService(intent);
            isTimerServiceRunning = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "On Destroy View is called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "On Destroy is called");
    }
}
