package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class CreateUserResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("userId")
    private int userId;

    @SerializedName("user")
    private UserData user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getUserId() { return userId; }
    public UserData getUser() { return user; }

    public static class UserData {
        @SerializedName("id")
        private int id;

        @SerializedName("email")
        private String email;

        @SerializedName("bio")
        private String bio;

        @SerializedName("interest_tags")
        private String interestTags;

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getBio() { return bio; }
        public String getInterestTags() { return interestTags; }
    }
}
