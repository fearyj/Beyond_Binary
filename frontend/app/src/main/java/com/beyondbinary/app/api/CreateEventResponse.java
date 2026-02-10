package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class CreateEventResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("eventId")
    private int eventId;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getEventId() {
        return eventId;
    }
}
