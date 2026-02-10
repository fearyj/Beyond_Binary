package com.beyondbinary.app.data.models;

public class User {
    private int id;
    private String bio;

    public User() {}

    public User(int id, String bio) {
        this.id = id;
        this.bio = bio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
