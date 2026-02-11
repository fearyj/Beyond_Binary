package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserEventsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("events")
    private List<UserEvent> events;

    public boolean isSuccess() { return success; }
    public List<UserEvent> getEvents() { return events; }

    public static class UserEvent {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("location")
        private String location;

        @SerializedName("description")
        private String description;

        @SerializedName("time")
        private String time;

        @SerializedName("currentParticipants")
        private int currentParticipants;

        @SerializedName("maxParticipants")
        private int maxParticipants;

        @SerializedName("eventType")
        private String eventType;

        @SerializedName("interaction_type")
        private String interactionType;

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getLocation() { return location; }
        public String getDescription() { return description; }
        public String getTime() { return time; }
        public int getCurrentParticipants() { return currentParticipants; }
        public int getMaxParticipants() { return maxParticipants; }
        public String getEventType() { return eventType; }
        public String getInteractionType() { return interactionType; }

        public boolean isHost() {
            return "created".equals(interactionType);
        }
    }
}
