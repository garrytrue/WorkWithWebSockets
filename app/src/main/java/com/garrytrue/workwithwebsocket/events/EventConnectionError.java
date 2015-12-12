package com.garrytrue.workwithwebsocket.events;


public class EventConnectionError {
    private String mMessage;
    public EventConnectionError(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
