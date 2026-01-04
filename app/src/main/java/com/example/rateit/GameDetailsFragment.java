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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String gameName, gameReleaseDate, gameRating, gameGenre,
            gameImage, gameesrbRating, gameTrailer , gameID;

    private ImageView gameDetailImage;
    private TextView gameDetailName, gameDetailRating, gameDetailReleaseDate,
            gameDetailGenre, gameDetailEsrbRating;
    private Button gameDetailTrailerButton;

    private VideoView mVideoView;



    public GameDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameDetailsFragment newInstance(String param1, String param2) {
        GameDetailsFragment fragment = new GameDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        gameTrailer = trailerObj.getData().getMaxQuality();
                        if(gameTrailer == null){
                            gameTrailer = trailerObj.getData().getLowQuality();
                        }
                    }
                    if(gameTrailer != null && !gameTrailer.isEmpty()){
                        MediaController mediaController = new MediaController(requireContext());
                        mediaController.setAnchorView(mVideoView);
                        mVideoView.setMediaController(mediaController);

// Set the video URI
                        mVideoView.setVideoURI(Uri.parse(gameTrailer));

// Optional: Auto-play when ready
                        mVideoView.setOnPreparedListener(mp -> {
                            mp.start();
                        });
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
        mVideoView = view.findViewById(R.id.gameDetailVideoView);
        //gameDetailTrailerButton = view.findViewById(R.id.gameDetailTrailerButton);

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