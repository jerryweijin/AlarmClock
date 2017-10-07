package com.jerryweijin.alarmclock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerryweijin.alarmclock.adapters.AlarmAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 6/11/17.
 */

public class AlarmFragment extends Fragment {
    private List<String> mAlarms = new ArrayList<>();
    private RecyclerView mAlarmRecyclerView;
    private AlarmAdapter mAlarmAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        mAlarmRecyclerView = (RecyclerView) view.findViewById(R.id.alarmRecyclerView);

        for (int i = 0; i< 10; i++) {
            mAlarms.add("item " + i);
        }

        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAlarmAdapter = new AlarmAdapter(mAlarms);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);

        return view;
    }
}
