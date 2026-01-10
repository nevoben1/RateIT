package com.example.rateit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameDetailsFragment extends Fragment {

    private String gameName, gameReleaseDate, gameRating, gameGenre,
            gameImage, gameesrbRating, gameTrailer , gameID;

    private ImageView gameDetailImage;
    private TextView gameDetailName, gameDetailRating, gameDetailReleaseDate,
            gameDetailGenre, gameDetailEsrbRating, gameDetailDescription;
    private Button gameDetailTrailerButton;

    private VideoView mVideoView;

    public GameDetailsFragment() {
        // Required empty public constructor
    }

    public static GameDetailsFragment newInstance(String param1, String param2) {
        GameDetailsFragment fragment = new GameDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get all the data passed from the gameList fragment
        if (getArguments() != null) {
            gameName = getArguments().getString("game_name");
            gameReleaseDate = getArguments().getString("game_releaseDate");
            gameRating = getArguments().getString("game_rating");
            gameGenre = getArguments().getString("game_genre");
            gameImage = getArguments().getString("game_image");

            gameesrbRating = getArguments().getString("game_esrbRating");
            gameTrailer = null;
            gameID = getArguments().getString("game_id");
        }

        fetchVideos();
        fetchDescription();
    }

    private void fetchVideos() {
        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GamesListFragment.BASE).addConverterFactory(GsonConverterFactory.create())
                .build();
        //connect retrofit to the service
        GameApiService service = retrofit.create(GameApiService.class);

        Call<TrailersResponse> call = service.getTrailers(gameID);

        //call async func
        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TrailerObj trailerObj = response.body().getResults();
                    if(trailerObj != null )
                    {
                        //try get the max quality trailer
                        gameTrailer = trailerObj.getData().getMaxQuality();
                        if(gameTrailer == null){
                            //if we can not find the max quality try to get the min quality
                            gameTrailer = trailerObj.getData().getLowQuality();
                        }
                    }
                    //only if we found a trailer display it otherwise display a toast
                    if(gameTrailer != null && !gameTrailer.isEmpty()){
                        MediaController mediaController = new MediaController(requireContext());
                        mediaController.setAnchorView(mVideoView);
                        mVideoView.setMediaController(mediaController);

                        // Set the video URI
                        mVideoView.setVideoURI(Uri.parse(gameTrailer));

                        //Auto-play when ready
                        mVideoView.setOnPreparedListener(mp -> {
                            mp.start();
                        });
                    }
                    else{
                        Toast.makeText(getContext() , "no trailer found" , Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.d("result", "Response code: " + response.code());
                    Log.d("result", "Response message: " + response.message());

                }
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                Log.d("result" ,"API called failed " + t.getMessage());
            }
        });
    }

    private void fetchDescription() {
        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GamesListFragment.BASE).addConverterFactory(GsonConverterFactory.create())
                .build();
        //connect retrofit to the service
        GameApiService service = retrofit.create(GameApiService.class);

        Call<GameDetailsResponse> call = service.getDescription(gameID);

        //call async func
        call.enqueue(new Callback<GameDetailsResponse>() {
            @Override
            public void onResponse(Call<GameDetailsResponse> call, Response<GameDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameDetailsResponse gameDetails = response.body();
                    String description = gameDetails.getDescriptionRaw();

                    // Update the description TextView if it's initialized and description is not null
                    if (gameDetailDescription != null && description != null && !description.isEmpty()) {
                        gameDetailDescription.setText(description);
                    } else if (gameDetailDescription != null) {
                        gameDetailDescription.setText("No description available");
                    }
                } else {
                    Log.d("result", "Response code: " + response.code());
                    Log.d("result", "Response message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GameDetailsResponse> call, Throwable t) {
                Log.d("result", "API call failed " + t.getMessage());
                if (gameDetailDescription != null) {
                    gameDetailDescription.setText("Failed to load description");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_details, container, false);
        // Initialize views
        gameDetailImage = view.findViewById(R.id.gameDetailImage);
        gameDetailName = view.findViewById(R.id.gameDetailName);
        gameDetailRating = view.findViewById(R.id.gameDetailRating);
        gameDetailReleaseDate = view.findViewById(R.id.gameDetailReleaseDate);
        gameDetailGenre = view.findViewById(R.id.gameDetailGenre);
        gameDetailEsrbRating = view.findViewById(R.id.gameDetailEsrbRating);
        gameDetailDescription = view.findViewById(R.id.gameDetailDescription);
        mVideoView = view.findViewById(R.id.gameDetailVideoView);

        // Set data
        gameDetailName.setText(gameName);
        gameDetailRating.setText(gameRating);
        gameDetailReleaseDate.setText(gameReleaseDate);
        gameDetailGenre.setText(gameGenre);
        gameDetailEsrbRating.setText(gameesrbRating);

        // Load image with Glide
        Glide.with(this)
                .load(gameImage)
                .into(gameDetailImage);

        return view;
    }
}