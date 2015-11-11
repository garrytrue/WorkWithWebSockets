package com.garrytrue.workwithwebsocket.a.events;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 11.11.15.
 */
public class EventConnectionClosed {
    private String mReason;

    public EventConnectionClosed(String reason) {
        mReason = reason;
    }
    public String getReason() {
        return mReason;
    }
}
