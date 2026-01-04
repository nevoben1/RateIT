package com.example.rateit;

import com.google.gson.annotations.SerializedName;

public class TrailerData {
    public String getLowQuality() {
        return lowQuality;
    }

    public String getMaxQuality() {
        return maxQuality;
    }

    @SerializedName("480")
    private String lowQuality;
    @SerializedName("max")
    private String maxQuality;
}
