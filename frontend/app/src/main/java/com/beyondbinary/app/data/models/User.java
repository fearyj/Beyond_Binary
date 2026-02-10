package com.beyondbinary.app.data.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class User {
    private int id;
    private String email;
    private String bio;
    private String interestTags;
    private String username;
    private String dob;
    private String address;
    private String caption;
    private String profilePicturePath;

    public User() {}

    public User(int id, String bio) {
        this.id = id;
        this.bio = bio;
    }

    public User(int id, String bio, String interestTags) {
        this.id = id;
        this.bio = bio;
        this.interestTags = interestTags;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getInterestTags() { return interestTags; }
    public void setInterestTags(String interestTags) { this.interestTags = interestTags; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }

    public List<String> getInterestTagsAsList() {
        if (interestTags == null || interestTags.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(interestTags.split(","));
    }
}
