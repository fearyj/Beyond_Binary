package com.beyondbinary.app.data.models;

import java.util.Arrays;
import java.util.List;

public class User {
    private int id;
    private String bio;
    private String interestTags;

    public User() {}

    public User(int id, String bio, String interestTags) {
        this.id = id;
        this.bio = bio;
        this.interestTags = interestTags;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getInterestTags() { return interestTags; }
    public void setInterestTags(String interestTags) { this.interestTags = interestTags; }

    public List<String> getInterestTagsAsList() {
        if (interestTags == null || interestTags.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(interestTags.split(","));
    }
}
