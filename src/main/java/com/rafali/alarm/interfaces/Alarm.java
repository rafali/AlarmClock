package com.rafali.alarm.interfaces;

import android.net.Uri;

import java.util.Calendar;

public interface Alarm {
    void enable(boolean enable);

    void snooze();

    void snooze(int hourOfDay, int minute);

    void dismiss();

    void delete();

    ImmutableAlarmEditor edit();

    boolean isSilent();

    Uri getAlert();

    int getId();

    String getLabelOrDefault();

    /**
     * @deprecated
     * @return
     */
    @Deprecated
    public Calendar getSnoozedTime();

}
