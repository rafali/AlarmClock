package com.rafali.alarm.model;

import com.rafali.alarm.configuration.Prefs;
import com.rafali.alarm.configuration.Store;
import com.rafali.alarm.logger.Logger;
import com.rafali.alarm.statemachine.HandlerFactory;

/**
 * Created by Yuriy on 09.08.2017.
 */

public class AlarmCoreFactory {
    private final Logger logger;
    private final IAlarmsScheduler alarmsScheduler;
    private final AlarmCore.IStateNotifier broadcaster;
    private final HandlerFactory handlerFactory;
    private final Prefs prefs;
    private final Store store;
    private final Calendars calendars;

    public AlarmCoreFactory(Logger logger, IAlarmsScheduler alarmsScheduler, AlarmCore.IStateNotifier broadcaster, HandlerFactory handlerFactory, Prefs prefs, Store store, Calendars calendars) {
        this.logger = logger;
        this.alarmsScheduler = alarmsScheduler;
        this.broadcaster = broadcaster;
        this.handlerFactory = handlerFactory;
        this.prefs = prefs;
        this.store = store;
        this.calendars = calendars;
    }

    public AlarmCore create(AlarmContainer container) {
        return new AlarmCore(container, logger, alarmsScheduler, broadcaster, handlerFactory, prefs, store, calendars);
    }
}
