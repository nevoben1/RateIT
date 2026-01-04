package com.example.rateit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GameApiService {

    @GET("games?key=219ef37506bd4d06b40222e162377cb4&page_size=20")
    Call<GameResponse> getAllGames(@Query("page") int page);

    @GET("games/{gameId}/movies?key=219ef37506bd4d06b40222e162377cb4")
    Call<TrailersResponse> getTrailers(@Path("gameId") String gameId);
}
