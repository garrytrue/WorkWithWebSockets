package com.garrytrue.workwithwebsocket.a.events;

/**
 * Created by TorbaIgor (garrytrue@yandex.ru) on 14.11.15.
 */
public class EventHaveProblem {
    String message;


    public EventHaveProblem(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
