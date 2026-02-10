package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("user")
    private UserData user;

    public boolean isSuccess() { return success; }
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

        @SerializedName("username")
        private String username;

        @SerializedName("dob")
        private String dob;

        @SerializedName("address")
        private String address;

        @SerializedName("caption")
        private String caption;

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getBio() { return bio; }
        public String getInterestTags() { return interestTags; }
        public String getUsername() { return username; }
        public String getDob() { return dob; }
        public String getAddress() { return address; }
        public String getCaption() { return caption; }
    }
}
