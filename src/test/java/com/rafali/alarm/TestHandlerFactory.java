package com.rafali.alarm;

import com.rafali.alarm.statemachine.HandlerFactory;
import com.rafali.alarm.statemachine.IHandler;
import com.rafali.alarm.statemachine.ImmutableMessage;
import com.rafali.alarm.statemachine.Message;
import com.rafali.alarm.statemachine.MessageHandler;

import io.reactivex.Scheduler;

/**
 * Created by Yuriy on 25.06.2017.
 */
class TestHandlerFactory implements HandlerFactory {
    private Scheduler testScheduler;

    public TestHandlerFactory(Scheduler scheduler) {
        this.testScheduler = scheduler;
    }

    @Override
    public IHandler create(final MessageHandler messageHandler) {
        return new IHandler() {
            @Override
            public void sendMessageAtFrontOfQueue(Message message) {
                sendMessage(message);
            }

            @Override
            public void sendMessage(final Message message) {
                testScheduler.scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        messageHandler.handleMessage(message);
                    }
                });
            }

            @Override
            public ImmutableMessage obtainMessage(int what, Object obj) {
                return ImmutableMessage.builder().what(what).handler(this).obj(obj).build();
            }

            @Override
            public ImmutableMessage obtainMessage(int what) {
                return ImmutableMessage.builder().what(what).handler(this).build();
            }
        };
    }
}