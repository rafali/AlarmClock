package com.rafali.alarm.model;

import android.os.Handler;

import com.rafali.alarm.statemachine.HandlerFactory;
import com.rafali.alarm.statemachine.IHandler;
import com.rafali.alarm.statemachine.ImmutableMessage;
import com.rafali.alarm.statemachine.Message;
import com.rafali.alarm.statemachine.MessageHandler;

/**
 * Created by Yuriy on 01.05.2017.
 */
public class MainLooperHandlerFactory implements HandlerFactory {
    @Override
    public IHandler create(final MessageHandler handler) {
        final Handler realHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {
                handler.handleMessage((Message) msg.obj);
                return true;
            }
        });

        return new IHandler() {
            @Override
            public void sendMessageAtFrontOfQueue(Message message) {
                realHandler.sendMessageAtFrontOfQueue(realHandler.obtainMessage(1, message));
            }

            @Override
            public void sendMessage(Message message) {
                realHandler.sendMessage(realHandler.obtainMessage(1, message));
            }

            @Override
            public ImmutableMessage obtainMessage(int what, Object obj) {
                return obtainMessage(what).withObj(obj);
            }

            @Override
            public ImmutableMessage obtainMessage(int what) {
                return ImmutableMessage.builder().what(what).handler(this).build();
            }
        };
    }
}
