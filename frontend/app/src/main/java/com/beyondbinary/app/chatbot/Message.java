package com.beyondbinary.app.chatbot;

import com.beyondbinary.app.Event;
import java.util.List;

public class Message {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_EVENTS = 1;

    private String text;
    private boolean isSentByUser;
    private int messageType;
    private List<Event> events;

    // Constructor for text messages
    public Message(String text, boolean isSentByUser) {
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.messageType = TYPE_TEXT;
    }

    // Constructor for event messages
    public Message(List<Event> events, String text) {
        this.events = events;
        this.text = text;
        this.isSentByUser = false;
        this.messageType = TYPE_EVENTS;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public int getMessageType() {
        return messageType;
    }

    public List<Event> getEvents() {
        return events;
    }
}
