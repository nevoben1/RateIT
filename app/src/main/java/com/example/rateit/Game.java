package com.example.rateit;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Game {
    @SerializedName("id")
    private final String ID;
    @SerializedName("name")
    private final String NAME;

    @SerializedName("background_image")
    private final String IMAGE_URL;

    @SerializedName("rating")
    private final double RATING;

    @SerializedName("platforms")
    private final List<Platform> PLATFORMS;

    @SerializedName("genres")
    private final List<Genre> GENRES;

    @SerializedName("clip")  // The API uses "clip" for video content
    private final String TRAILER_URL;

    @SerializedName("released")  // Changed from "release" to "released"
    private final String RELEASE_DATE;

    @SerializedName("esrb_rating")
    private final EsrbRating ESRB_RATING;

    public Game(String NAME, String IMAGE_URL, double RATING, List<Platform> PLATFORMS, List<Genre> GENRES,
                String TRAILER_URL, String RELEASE_DATE, EsrbRating ESRB_RATING , String ID) {
        this.NAME = NAME;
        this.IMAGE_URL = IMAGE_URL;
        this.RATING = RATING;
        this.PLATFORMS = PLATFORMS;
        this.GENRES = GENRES;
        this.TRAILER_URL = TRAILER_URL;
        this.RELEASE_DATE = RELEASE_DATE;
        this.ESRB_RATING = ESRB_RATING;
        this.ID = ID;
    }

    // Getters
    public String getName() {
        return NAME;
    }

    public String getID(){
        return ID;
    }

    public String getImageUrl() {
        return IMAGE_URL;
    }

    public double getRating() {
        return RATING;
    }

    public List<Platform> getPlatforms() {
        return PLATFORMS;
    }

    public String getGenres() {
        return GENRES.get(0).getName();
    }

    public String getTrailerUrl() {
        return TRAILER_URL;
    }

    public String getReleaseDate() {
        return RELEASE_DATE;
    }
    //some games dont have esrb rating , we should handle that
    public String getEsrbRating() {
        return ESRB_RATING != null ? ESRB_RATING.getName() : "none";
    }

    // Simplified ESRB Rating class - directly maps to the object
    public static class EsrbRating {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    // Nested class for Platform
    public static class Platform {
        @SerializedName("platform")
        private PlatformInfo platform;

        public PlatformInfo getPlatform() {
            return platform;
        }

        public static class PlatformInfo {
            @SerializedName("name")
            private String name;

            public String getName() {
                return name;
            }
        }
    }

    // Nested class for Genre
    public static class Genre {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }
}