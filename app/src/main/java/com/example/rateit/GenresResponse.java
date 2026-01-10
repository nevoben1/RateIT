package com.example.rateit;

import java.util.List;

public class GenresResponse {
    private List<Genre> results;

    public GenresResponse() {
    }

    public GenresResponse(List<Genre> results) {
        this.results = results;
    }

    public List<Genre> getResults() {
        return results;
    }

    public void setResults(List<Genre> results) {
        this.results = results;
    }
}
