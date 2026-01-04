package com.example.rateit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GameApiService {

    @GET("games?key=219ef37506bd4d06b40222e162377cb4&")
    Call<GameResponse> getAllGames();

    @GET("games/{gameId}/movies?key=219ef37506bd4d06b40222e162377cb4")
    Call<TrailersResponse> getTrailers(@Path("gameId") String gameId);
}
