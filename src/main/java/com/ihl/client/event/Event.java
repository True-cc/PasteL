package com.ihl.client.event;

public class Event {

    public Type type;
    public boolean cancelled;
    public Event(Type type) {
        this.type = type;
    }

    public void cancel() {
        cancelled = true;
    }

    public enum Type {
        PRE,
        POST,
        SEND,
        RECEIVE,
        CLICKL,
        CLICKM,
        CLICKR,
        PRESS,
        RELEASE,
        SCROLL
    }

}
