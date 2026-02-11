package com.beyondbinary.app.api;

import com.beyondbinary.app.Event;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("count")
    private int count;

    @SerializedName("events")
    private List<Event> events;

    public boolean isSuccess() {
        return success;
    }

    public int getCount() {
        return count;
    }

    public List<Event> getEvents() {
        return events;
    }
}
