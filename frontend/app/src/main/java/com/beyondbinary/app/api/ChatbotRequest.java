package com.beyondbinary.app.api;

import java.util.List;

public class ChatbotRequest {
    private String message;
    private Integer userId;
    private List<ConversationEntry> conversationHistory;

    public ChatbotRequest(String message, Integer userId, List<ConversationEntry> conversationHistory) {
        this.message = message;
        this.userId = userId;
        this.conversationHistory = conversationHistory;
    }

    public String getMessage() { return message; }
    public Integer getUserId() { return userId; }
    public List<ConversationEntry> getConversationHistory() { return conversationHistory; }

    public static class ConversationEntry {
        private String role;
        private String content;

        public ConversationEntry(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }
}
