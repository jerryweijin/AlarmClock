package com.jerryweijin.alarmclock.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jerryweijin.alarmclock.R;

import java.util.List;

/**
 * Created by jerry on 10/6/2017.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private List<String> mAlarms;

    public AlarmAdapter(List<String> alarms) {

        mAlarms = alarms;

    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.alarm_row_layout, parent, false);
        AlarmViewHolder holder = new AlarmViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.alarmTime.setText(mAlarms.get(position));
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private TextView alarmTime;

        public AlarmViewHolder(View itemView) {
            super(itemView);

            alarmTime = (TextView) itemView.findViewById(R.id.alarmTime);
        }
    }
}
