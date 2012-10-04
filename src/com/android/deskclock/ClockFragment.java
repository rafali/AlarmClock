/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.android.deskclock.worldclock.Cities;
import com.android.deskclock.worldclock.CitiesActivity;
import com.android.deskclock.worldclock.CityObj;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;


/**
 * TODO: Insert description here. (generated by isaackatz)
 */
public class ClockFragment extends DeskClockFragment {

    private static final String BUTTONS_HIDDEN_KEY = "buttons_hidden";

    private final static String DATE_FORMAT = "EEEE, MMMM d";

    View mButtons;
    TextView mNextAlarm;
    private TextView mDateDisplay;
    boolean mButtonsHidden = false;
    View mDigitalClock, mAnalogClock;
    WorldClockAdapter mAdapter;
    ListView mList;

    public ClockFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle icicle) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.clock_fragment, container, false);
        mButtons = v.findViewById(R.id.clock_buttons);
        mNextAlarm = (TextView)v.findViewById(R.id.nextAlarm);
        mDateDisplay = (TextView) v.findViewById(R.id.date);
        mDigitalClock = v.findViewById(R.id.digital_clock);
        mAnalogClock = v.findViewById(R.id.analog_clock);
        if (icicle != null) {
            mButtonsHidden = icicle.getBoolean(BUTTONS_HIDDEN_KEY, false);
        }
        mList = (ListView)v.findViewById(R.id.cities);
        mList.setDivider(null);
        mAdapter = new WorldClockAdapter(getActivity());
        mList.setAdapter(mAdapter);
        refreshAlarm();
        return v;
    }

    @Override
    public void onResume () {
        super.onResume();
        refreshAlarm();
  //      updateDate();   // No date at this point
        mButtons.setAlpha(mButtonsHidden ? 0 : 1);
        setClockStyle();
        // Resume can invoked after changing the cities list.
        if (mAdapter != null) {
            mAdapter.reloadData(getActivity());
        }


    }


    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putBoolean(BUTTONS_HIDDEN_KEY, mButtonsHidden);
        super.onSaveInstanceState(outState);
    }

    private void setClockStyle() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String style = sharedPref.getString(SettingsActivity.KEY_CLOCK_STYLE, "digital");
        if (style.equals("analog")) {
            mDigitalClock.setVisibility(View.GONE);
            mAnalogClock.setVisibility(View.VISIBLE);
        } else {
            mDigitalClock.setVisibility(View.VISIBLE);
            mAnalogClock.setVisibility(View.GONE);
        }
    }

    private void refreshAlarm() {
        if (mNextAlarm == null) return;

        mNextAlarm.setVisibility(View.GONE);
/* No next alarm at this point
        String nextAlarm = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.NEXT_ALARM_FORMATTED);
        if (!TextUtils.isEmpty(nextAlarm)) {
            mNextAlarm.setText(getString(R.string.control_set_alarm_with_existing, nextAlarm));
            mNextAlarm.setVisibility(View.VISIBLE);
        } else  {
            mNextAlarm.setVisibility(View.INVISIBLE);
        }*/
    }

    private void updateDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        CharSequence newDate = DateFormat.format(DATE_FORMAT, cal);
        mDateDisplay.setVisibility(View.VISIBLE);
        mDateDisplay.setText(newDate);
    }

    public void showButtons(boolean show) {
        if (mButtons == null) {
            return;
        }
        if (show && mButtonsHidden) {
            mButtons.startAnimation(
                    AnimationUtils.loadAnimation(getActivity(), R.anim.unhide));
            mButtonsHidden = false;
        } else if (!show && !mButtonsHidden) {
            mButtons.startAnimation(
                    AnimationUtils.loadAnimation(getActivity(), R.anim.hide));
            mButtonsHidden = true;
        }
    }

    private class WorldClockAdapter extends BaseAdapter {
        Object [] mCitiesList;
        LayoutInflater mInflater;
        private boolean mIs24HoursMode;
        Context mContext;

        public WorldClockAdapter(Context context) {
            super();
            mCitiesList = Cities.readCitiesFromSharedPrefs(
                    PreferenceManager.getDefaultSharedPreferences(context)).values().toArray();
            mContext = context;
            mCitiesList = addHomeCity();
            mInflater = LayoutInflater.from(context);
            set24HoursMode(context);
        }

        public void reloadData(Context context) {
            mCitiesList = Cities.readCitiesFromSharedPrefs(
                    PreferenceManager.getDefaultSharedPreferences(context)).values().toArray();
            mCitiesList = addHomeCity();
            notifyDataSetChanged();
        }

        private Object[] addHomeCity() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (sharedPref.getBoolean(SettingsActivity.KEY_AUTO_HOME_CLOCK, false)) {
                String homeTZ = sharedPref.getString(SettingsActivity.KEY_HOME_TZ, "");
                if (!TimeZone.getDefault().getID().equals(homeTZ)) {
                    CityObj c = new CityObj(
                            mContext.getResources().getString(R.string.home_label), homeTZ, null);
                    Object[] temp = new Object[mCitiesList.length + 1];
                    temp[0] = c;
                    for (int i = 0; i < mCitiesList.length; i++) {
                        temp[i + 1] = mCitiesList[i];
                    }
                    return temp;
                }
            }
            return mCitiesList;
        }

        @Override
        public int getCount() {
            // Each item in the list holds 1 or 2 clocks
            return (mCitiesList.length  + 1)/2;
        }

        @Override
        public Object getItem(int p) {
            return null;
        }

        @Override
        public long getItemId(int p) {
            return p;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // Index in cities list
            int index = position * 2;
            if (index < 0 || index >= mCitiesList.length) {
                return null;
            }

            if (view == null) {
                view = mInflater.inflate(R.layout.world_clock_list_item, parent, false);
            }
            // The world clock list item can hold two world clocks
            View leftClock = view.findViewById(R.id.city_left);
            View rightClock = view.findViewById(R.id.city_right);
            CityObj c = (CityObj)mCitiesList[index];
            ((TextView)leftClock.findViewById(R.id.city_name)).setText(c.mCityName);
            DigitalClock clock = (DigitalClock)(leftClock.findViewById(R.id.digital_clock));
            clock.setTimeZone(c.mTimeZone);
            if (index + 1 < mCitiesList.length) {
                c = (CityObj)mCitiesList[index + 1];
                rightClock.setVisibility(View.VISIBLE);
                ((TextView)rightClock.findViewById(R.id.city_name)).setText(c.mCityName);
                clock = (DigitalClock)(rightClock.findViewById(R.id.digital_clock));
                clock.setTimeZone(c.mTimeZone);
            } else {
                rightClock.setVisibility(View.INVISIBLE);
            }
            return view;
        }

        public void set24HoursMode(Context c) {
            mIs24HoursMode = Alarms.get24HourMode(c);
            notifyDataSetChanged();
        }
    }

}