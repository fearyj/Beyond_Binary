package com.beyondbinary.app.api;

import com.beyondbinary.app.Event;
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
