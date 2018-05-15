package com.jackpot.follow_init.model;

import android.os.Handler;

import com.jackpot.follow_init.statemachine.HandlerFactory;
import com.jackpot.follow_init.statemachine.IHandler;
import com.jackpot.follow_init.statemachine.ImmutableMessage;
import com.jackpot.follow_init.statemachine.Message;
import com.jackpot.follow_init.statemachine.MessageHandler;

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
