package com.beyondbinary.app.chatbot;

import com.beyondbinary.app.Event;
import java.util.List;

public class Message {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_EVENTS = 1;
    public static final int TYPE_SUGGESTIONS = 2;

    private String text;
    private boolean isSentByUser;
    private int messageType;
    private List<Event> events;
    private List<String> eventTypeSuggestions;
    private int suggestedMaxParticipants;
    private String descriptionHint;
    private String userContext;

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

    // Constructor for event suggestions
    public Message(List<String> eventTypeSuggestions, int maxParticipants, String descriptionHint, String userContext) {
        this.eventTypeSuggestions = eventTypeSuggestions;
        this.suggestedMaxParticipants = maxParticipants;
        this.descriptionHint = descriptionHint;
        this.userContext = userContext;
        this.isSentByUser = false;
        this.messageType = TYPE_SUGGESTIONS;
        this.text = "Event suggestions";
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

    public List<String> getEventTypeSuggestions() {
        return eventTypeSuggestions;
    }

    public int getSuggestedMaxParticipants() {
        return suggestedMaxParticipants;
    }

    public String getDescriptionHint() {
        return descriptionHint;
    }

    public String getUserContext() {
        return userContext;
    }
}
