package com.rafali.alarm;

import com.rafali.alarm.model.AlarmSetter;
import com.rafali.alarm.model.AlarmsScheduler;

/**
 * Created by Yuriy on 25.06.2017.
 */
class TestAlarmSetter implements AlarmSetter {
    @Override
    public void removeRTCAlarm() {

    }

    @Override
    public void setUpRTCAlarm(AlarmsScheduler.ScheduledAlarm alarm) {

    }

    @Override
    public void fireNow(AlarmsScheduler.ScheduledAlarm firedInThePastAlarm) {
//nothing for now
    }
}
