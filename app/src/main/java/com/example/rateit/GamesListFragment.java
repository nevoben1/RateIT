package com.example.rateit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesListFragment extends Fragment {
    private ArrayList<Game> dataSet;

    private RecyclerView recyclerView;

    public RecyclerView getRecyclerView(){
        return  recyclerView;
    }

    private LinearLayoutManager layoutManager;
    private CustomeAdapter adapter;
    public static final String BASE = "https://api.rawg.io/api/";



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mView;
    private List<Game> mGames;

    public GamesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesListFragment newInstance(String param1, String param2) {
        GamesListFragment fragment = new GamesListFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_games_list, container, false);

        dataSet = new ArrayList<>();
        recyclerView = mView.findViewById(R.id.gamesListView);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        //set default state for the lists
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fetchGames();

        return mView;
    }

    private void filterGames(){
        List<Game> preFilter  = List.copyOf(mGames);
        for (Game game:preFilter) {
            //if game doesnt match the filter , remove
        }
        //setAdapter to the recycler view
    }

    private void fetchGames() {
        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE).addConverterFactory(GsonConverterFactory.create())
                .build();
        //connect retrofit to the service
        GameApiService service = retrofit.create(GameApiService.class);

        Call<GameResponse> call = service.getAllGames();

        //call async func
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mGames = response.body().getResults();

                    adapter = new CustomeAdapter(mGames);
                    adapter.setOnItemClickListener((data, position) -> {
                        //Navigate to the same fragment for all items

                        //Bundle is an object which contains keyValue pairs of data , the bundle will be passed to the next fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("game_name", data.getName() + "");
                        bundle.putString("game_genre", data.getGenres());
                        bundle.putString("game_image", data.getImageUrl());
                        bundle.putString("game_releaseDate", data.getReleaseDate());
                        bundle.putString("game_trailer", data.getTrailerUrl());
                        bundle.putString("game_esrbRating", data.getEsrbRating());
                        bundle.putString("game_rating" , data.getRating()+"");
                        bundle.putString("game_id"  , data.getID());
                        Navigation.findNavController(mView).navigate(R.id.action_gamesListFragment_to_gameDetailsFragment, bundle);
                    });
                    recyclerView.setAdapter(adapter);

                    Log.d("result", "Games loaded: " + mGames.size());
                } else {
                    Log.d("result", "Response code: " + response.code());
                    Log.d("result", "Response message: " + response.message());

                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.d("result" ,"API called failed " + t.getMessage());
            }
        });

    }
}