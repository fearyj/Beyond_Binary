package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class SendInviteResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("messageId")
    private int messageId;

    @SerializedName("message")
    private InviteMessage message;

    public boolean isSuccess() { return success; }
    public int getMessageId() { return messageId; }
    public InviteMessage getMessage() { return message; }

    public static class InviteMessage {
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
        private int eventId;

        @SerializedName("event_title")
        private String eventTitle;

        @SerializedName("event_time")
        private String eventTime;

        @SerializedName("event_location")
        private String eventLocation;

        @SerializedName("event_type")
        private String eventType;

        public int getId() { return id; }
        public int getSenderId() { return senderId; }
        public int getReceiverId() { return receiverId; }
        public String getText() { return text; }
        public String getType() { return type; }
        public int getEventId() { return eventId; }
        public String getEventTitle() { return eventTitle; }
        public String getEventTime() { return eventTime; }
        public String getEventLocation() { return eventLocation; }
        public String getEventType() { return eventType; }
    }
}
