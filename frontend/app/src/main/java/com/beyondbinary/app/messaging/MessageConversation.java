package com.beyondbinary.app.messaging;

public class MessageConversation {
    private String name;
    private String lastMessage;
    private String time;
    private String profileEmoji;

    public MessageConversation(String name, String lastMessage, String time, String profileEmoji) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.profileEmoji = profileEmoji;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTime() {
        return time;
    }

    public String getProfileEmoji() {
        return profileEmoji;
    }
}
