package com.beyondbinary.app.api;

import com.google.gson.annotations.SerializedName;

public class UploadPhotoResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("photoId")
    private int photoId;

    @SerializedName("image_url")
    private String imageUrl;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getPhotoId() { return photoId; }
    public String getImageUrl() { return imageUrl; }
}
