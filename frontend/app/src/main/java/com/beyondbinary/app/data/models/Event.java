package com.beyondbinary.app.data.models;

public class Event {
    private int id;
    private String title;
    private String category;
    private String description;
    private String mediaUrl;
    private String location;

    public Event() {}

    public Event(int id, String title, String category, String description, String mediaUrl, String location) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.location = location;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
