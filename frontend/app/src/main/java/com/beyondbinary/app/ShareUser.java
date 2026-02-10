package com.beyondbinary.app;

public class ShareUser {
    private int id;
    private String name;
    private String bio;
    private String profileEmoji;

    public ShareUser(int id, String name, String bio, String profileEmoji) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.profileEmoji = profileEmoji;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileEmoji() {
        return profileEmoji;
    }
}
