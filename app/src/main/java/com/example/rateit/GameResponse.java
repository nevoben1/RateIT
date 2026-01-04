package com.example.rateit;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GameResponse {
    @SerializedName("count")
    private int count;

    @SerializedName("next")
    private String next;

    @SerializedName("previous")
    private String previous;

    @SerializedName("results")
    private List<Game> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<Game> getResults() {
        return results;
    }
}
