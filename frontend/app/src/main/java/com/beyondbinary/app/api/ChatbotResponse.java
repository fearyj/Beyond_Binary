package com.beyondbinary.app.api;

import com.beyondbinary.app.Event;

import java.util.List;

public class ChatbotResponse {
    private String type;
    private String message;
    private List<Event> events;
    private List<Suggestion> suggestions;

    public String getType() { return type; }
    public String getMessage() { return message; }
    public List<Event> getEvents() { return events; }
    public List<Suggestion> getSuggestions() { return suggestions; }

    public static class Suggestion {
        private String eventType;
        private int maxParticipants;
        private String descriptionHint;

        public String getEventType() { return eventType; }
        public int getMaxParticipants() { return maxParticipants; }
        public String getDescriptionHint() { return descriptionHint; }
    }
}
