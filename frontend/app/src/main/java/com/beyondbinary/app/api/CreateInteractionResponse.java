package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class CreateInteractionResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("interactionId")
    private int interactionId;

    public boolean isSuccess() { return success; }
    public int getInteractionId() { return interactionId; }
}
