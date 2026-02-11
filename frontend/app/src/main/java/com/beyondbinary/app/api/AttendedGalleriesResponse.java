package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttendedGalleriesResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("galleries")
    private List<EventGallery> galleries;

    public boolean isSuccess() { return success; }
    public List<EventGallery> getGalleries() { return galleries; }

    public static class EventGallery {
        @SerializedName("eventId")
        private int eventId;

        @SerializedName("title")
        private String title;

        @SerializedName("eventType")
        private String eventType;

        @SerializedName("time")
        private String time;

        @SerializedName("location")
        private String location;

        @SerializedName("imageUrls")
        private List<String> imageUrls;

        public int getEventId() { return eventId; }
        public String getTitle() { return title; }
        public String getEventType() { return eventType; }
        public String getTime() { return time; }
        public String getLocation() { return location; }
        public List<String> getImageUrls() { return imageUrls; }
    }
}
