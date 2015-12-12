package com.garrytrue.workwithwebsocket.events;

public class EventConnectionClosed {
    private String mReason;

    public EventConnectionClosed(String reason) {
        mReason = reason;
    }
    public String getReason() {
        return mReason;
    }
}
