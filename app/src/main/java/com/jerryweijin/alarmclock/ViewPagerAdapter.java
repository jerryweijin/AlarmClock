package com.jerryweijin.alarmclock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by Jerry on 6/11/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.alarmFragmentTitle);
            case 1:
                return mContext.getString(R.string.worldClockFragmentTitle);
            case 2:
                return mContext.getString(R.string.stopwatchFragmentTitle);
            case 3:
                return mContext.getString(R.string.timerFragmentTitle);
            default:
                return null;
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AlarmFragment();
            case 1:
                return new WorldClockFragment();
            case 2:
                return new StopwatchFragment();
            case 3:
                return new TimerFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
