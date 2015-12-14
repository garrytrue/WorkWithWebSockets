package com.garrytrue.workwithwebsocket.events;


public class EventConnectionError {
    private final String mMessage;
    public EventConnectionError(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
