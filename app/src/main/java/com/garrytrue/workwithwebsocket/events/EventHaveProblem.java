package com.garrytrue.workwithwebsocket.events;

public class EventHaveProblem {
    String message;


    public EventHaveProblem(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
