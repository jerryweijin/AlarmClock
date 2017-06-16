package com.jerryweijin.alarmclock;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jerry on 6/11/17.
 */

public class StopwatchFragment extends Fragment {
    private long elaspedTime;
    private long totalElaspedTime;
    private long timeBuffer;
    private long startTime;
    private int seconds;
    private int minutes;
    private int milliSeconds;
    private ArrayList<String> lapTime = new ArrayList<String>();
    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button lapButton;
    private Button resetButton;
    private TextView totalElaspedTimeTextView;
    private Handler handler;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            elaspedTime = SystemClock.uptimeMillis() - startTime;
            totalElaspedTime = timeBuffer + elaspedTime;
            minutes = (int) (totalElaspedTime / 1000 / 60);
            seconds = (int) (totalElaspedTime / 1000) % 60;
            milliSeconds = (int) (totalElaspedTime % 1000) / 10;
            totalElaspedTimeTextView.setText("" + String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%02d", milliSeconds));
            handler.postDelayed(this, 0);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        totalElaspedTimeTextView = (TextView) view.findViewById(R.id.elapsedTimeTextView);
        startButton = (Button) view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
            }
        });
        handler = new Handler();

        pauseButton = (Button) view.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeBuffer = totalElaspedTime;
                handler.removeCallbacks(runnable);
            }
        });

        resumeButton = (Button) view.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
            }
        });
        resetButton = (Button) view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elaspedTime = 0;
                timeBuffer = 0;
                startTime = 0;
                seconds = 0;
                minutes = 0;
                milliSeconds = 0;
                lapTime.clear();
                adapter.notifyDataSetChanged();
                handler.removeCallbacks(runnable);
                totalElaspedTimeTextView.setText("00:00:00");
            }
        });

        lapButton = (Button) view.findViewById(R.id.lapButton);
        lapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeString = totalElaspedTimeTextView.getText().toString();
                lapTime.add(timeString);
                adapter.notifyDataSetChanged();
            }
        });

        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lapTime);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
