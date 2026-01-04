package com.example.rateit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersResponse {
    public TrailerObj getResults() {
        return (results != null && !results.isEmpty()) ? results.get(0) : null;
    }

    @SerializedName("results")
    private List<TrailerObj> results;
}
