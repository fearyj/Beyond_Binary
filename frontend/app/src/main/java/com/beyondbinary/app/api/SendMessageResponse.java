package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class SendMessageResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("messageId")
    private int messageId;

    public boolean isSuccess() { return success; }
    public int getMessageId() { return messageId; }
}
