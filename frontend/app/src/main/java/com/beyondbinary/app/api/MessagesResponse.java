package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessagesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("messages")
    private List<Message> messages;

    public boolean isSuccess() { return success; }
    public List<Message> getMessages() { return messages; }

    public static class Message {
        @SerializedName("id")
        private int id;

        @SerializedName("sender_id")
        private int senderId;

        @SerializedName("receiver_id")
        private int receiverId;

        @SerializedName("text")
        private String text;

        @SerializedName("type")
        private String type;

        @SerializedName("event_id")
        private Integer eventId;

        @SerializedName("event_title")
        private String eventTitle;

        @SerializedName("event_time")
        private String eventTime;

        @SerializedName("event_location")
        private String eventLocation;

        @SerializedName("event_type")
        private String eventType;

        @SerializedName("current_participants")
        private int currentParticipants;

        @SerializedName("max_participants")
        private int maxParticipants;

        @SerializedName("created_at")
        private String createdAt;

        public int getId() { return id; }
        public int getSenderId() { return senderId; }
        public int getReceiverId() { return receiverId; }
        public String getText() { return text; }
        public String getType() { return type; }
        public Integer getEventId() { return eventId; }
        public String getEventTitle() { return eventTitle; }
        public String getEventTime() { return eventTime; }
        public String getEventLocation() { return eventLocation; }
        public String getEventType() { return eventType; }
        public int getCurrentParticipants() { return currentParticipants; }
        public int getMaxParticipants() { return maxParticipants; }
        public String getCreatedAt() { return createdAt; }
    }
}
