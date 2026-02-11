package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InteractionsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("interactions")
    private List<Interaction> interactions;

    public boolean isSuccess() { return success; }
    public List<Interaction> getInteractions() { return interactions; }

    public static class Interaction {
        @SerializedName("id")
        private int id;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("event_id")
        private int eventId;

        @SerializedName("interaction_type")
        private String interactionType;

        @SerializedName("title")
        private String title;

        @SerializedName("eventType")
        private String eventType;

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getEventId() { return eventId; }
        public String getInteractionType() { return interactionType; }
        public String getTitle() { return title; }
        public String getEventType() { return eventType; }
    }
}
