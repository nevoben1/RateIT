package com.example.rateit;

public class DataModel {
    private String gameName;

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGamePlatforms(String gamePlatforms) {
        this.gamePlatforms = gamePlatforms;
    }

    public void setGameReleaseDate(String gameReleaseDate) {
        this.gameReleaseDate = gameReleaseDate;
    }

    public void setGameRating(String gameRating) {
        this.gameRating = gameRating;
    }

    private String gamePlatforms;

    private String data;

    public String getGameName() {
        return gameName;
    }

    public String getGamePlatforms() {
        return gamePlatforms;
    }

    public String getGameReleaseDate() {
        return gameReleaseDate;
    }

    public String getGameRating() {
        return gameRating;
    }

    private String gameReleaseDate;
    private String gameRating;

    public void setData(String data) {
        this.data = data;
    }
    public String getData(){
        return data;
    }


    public void setImage(int image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    private int image;
    private String id;



    public int getImage() {
        return image;
    }

    public String getId() {
        return id;
    }



    public DataModel(String gameName, String gamePlatforms,String gameReleaseDate , String gameRating, int image, String id , String data) {
        this.gameName = gameName;
        this.gamePlatforms = gamePlatforms;
        this.gameReleaseDate = gameReleaseDate;
        this.gameRating = gameRating;
        this.image = image;
        this.id = id;
        this.data = data;
    }



}
