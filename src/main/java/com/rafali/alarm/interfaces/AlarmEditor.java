package com.rafali.alarm.interfaces;

import com.rafali.alarm.model.AlarmChangeData;

import org.immutables.value.Value;

import io.reactivex.functions.Consumer;

@Value.Immutable
public abstract class AlarmEditor implements AlarmChangeData {
    public abstract Consumer<AlarmChangeData> callback();

    public void commit() {
        try {
            callback().accept(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
