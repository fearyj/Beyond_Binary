package com.beyondbinary.app.messaging;

public class MessageConversation {
    private String name;
    private String lastMessage;
    private String time;
    private String profileEmoji;
    private String avatarUrl;

    public MessageConversation(String name, String lastMessage, String time, String profileEmoji) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.profileEmoji = profileEmoji;
        this.avatarUrl = null;
    }

    public MessageConversation(String name, String lastMessage, String time, String profileEmoji, String avatarUrl) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.profileEmoji = profileEmoji;
        this.avatarUrl = avatarUrl;
    }

    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public String getProfileEmoji() { return profileEmoji; }
    public String getAvatarUrl() { return avatarUrl; }
}
