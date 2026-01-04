package com.example.rateit;

import com.google.gson.annotations.SerializedName;

public class TrailerObj {
    public TrailerData getData() {
        return data;
    }

    @SerializedName("data")
    private TrailerData data;
}
