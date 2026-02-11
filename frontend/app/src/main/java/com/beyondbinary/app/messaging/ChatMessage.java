package com.beyondbinary.app.messaging;

public class ChatMessage {
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_EVENT_INVITE = "event_invite";

    private String text;
    private boolean isSentByMe;
    private String type;
    private int eventId;
    private String eventTitle;
    private String eventTime;
    private String eventLocation;
    private String eventType;
    private int currentParticipants;
    private int maxParticipants;

    // Text message constructor
    public ChatMessage(String text, boolean isSentByMe) {
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.type = TYPE_TEXT;
    }

    // Event invite constructor
    public ChatMessage(String text, boolean isSentByMe, int eventId,
                       String eventTitle, String eventTime, String eventLocation,
                       String eventType, int currentParticipants, int maxParticipants) {
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.type = TYPE_EVENT_INVITE;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventType = eventType;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
    }

    public String getText() { return text; }
    public boolean isSentByMe() { return isSentByMe; }
    public String getType() { return type; }
    public boolean isEventInvite() { return TYPE_EVENT_INVITE.equals(type); }
    public int getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getEventTime() { return eventTime; }
    public String getEventLocation() { return eventLocation; }
    public String getEventType() { return eventType; }
    public int getCurrentParticipants() { return currentParticipants; }
    public int getMaxParticipants() { return maxParticipants; }
}
