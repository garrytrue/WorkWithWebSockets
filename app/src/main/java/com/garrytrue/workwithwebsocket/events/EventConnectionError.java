package com.garrytrue.workwithwebsocket.a.events;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public class EventConnectionError {
    private String mMessage;
    public EventConnectionError(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
