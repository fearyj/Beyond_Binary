package com.beyondbinary.eventapp.api;

import com.beyondbinary.eventapp.Event;
import com.google.gson.annotations.SerializedName;

public class EventResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("event")
    private Event event;

    public boolean isSuccess() {
        return success;
    }

    public Event getEvent() {
        return event;
    }
}
