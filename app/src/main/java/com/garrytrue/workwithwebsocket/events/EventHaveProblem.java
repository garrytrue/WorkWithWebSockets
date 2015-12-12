package com.garrytrue.workwithwebsocket.events;

public class EventHaveProblem {
    String mMessage;

    public EventHaveProblem(String message) {
        mMessage = message;
    }
    public String getMessage() {
        return mMessage;
    }
}
