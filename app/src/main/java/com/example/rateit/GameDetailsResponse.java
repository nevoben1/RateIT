package com.example.rateit;

import com.google.gson.annotations.SerializedName;

public class GameDetailsResponse {
    @SerializedName("description")
    private String description;

    @SerializedName("description_raw")
    private String descriptionRaw;

    public String getDescription() {
        return description;
    }

    public String getDescriptionRaw() {
        return descriptionRaw;
    }
}
