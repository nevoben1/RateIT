package com.example.rateit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GameApiService {
    //the main request , will be called with a query param of which page do we want to get
    //the call will happen only once for each page as we save it in a hashmap
    @GET("games?key=219ef37506bd4d06b40222e162377cb4&page_size=20")
    Call<GameResponse> getAllGames(@Query("page") int page);
    //whenever we choose a game we want to get its details
    //we would like to see if there is trailer for it
    @GET("games/{gameId}/movies?key=219ef37506bd4d06b40222e162377cb4")
    Call<TrailersResponse> getTrailers(@Path("gameId") String gameId);
}
