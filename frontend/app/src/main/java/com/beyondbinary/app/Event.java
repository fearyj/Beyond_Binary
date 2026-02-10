package com.beyondbinary.app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "events")
public class Event implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String location;  // Address string
    private String description;
    private String time;
    private int currentParticipants;
    private int maxParticipants;
    private String eventType;  // "Sports", "Social", "Reading", "Dining", "Arts", "Outdoor"

    // Cached coordinates (nullable - will be geocoded if null)
    private Double latitude;
    private Double longitude;

    private Integer creatorUserId;

    public Event(String title, String location, String description, String time,
                 int currentParticipants, int maxParticipants, String eventType) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.time = time;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.eventType = eventType;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(Integer creatorUserId) { this.creatorUserId = creatorUserId; }
}
