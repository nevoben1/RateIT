package com.example.rateit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Pagination variables
    private int currentPage = 1;
    private String nextPageUrl = null;
    private String previousPageUrl = null;
    private Button btnPrevious;
    private Button btnNext;
    private Button btnFilter;
    private TextView tvPageInfo;

    // Search variables
    private EditText etSearch;
    private ImageButton btnClearSearch;
    private String searchText = "";

    // Cache for storing page data
    private Map<Integer, GameResponse> pageCache = new HashMap<>();
    private View mView;
    private List<Game> mGames;

    // Filter parameters
    private String filterCategory = "All Categories";
    private String filterStartYear = "";
    private String filterEndYear = "";
    private boolean filterFavoritesOnly = false;

    // Favorites tracking
    private java.util.Set<String> favoritedGameIds = new java.util.HashSet<>();

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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_games_list, container, false);

        //dataSet = new ArrayList<>();
        recyclerView = mView.findViewById(R.id.gamesListView);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        //set default state for the lists
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize pagination controls
        btnPrevious = mView.findViewById(R.id.btnPrevious);
        btnNext = mView.findViewById(R.id.btnNext);
        btnFilter = mView.findViewById(R.id.btnFilter);
        tvPageInfo = mView.findViewById(R.id.tvPageInfo);

        // Initialize search controls
        etSearch = mView.findViewById(R.id.etSearch);
        btnClearSearch = mView.findViewById(R.id.btnClearSearch);

        // Get filter parameters from arguments (when returning from filter fragment)
        if (getArguments() != null) {
            filterCategory = getArguments().getString("category", "All Categories");
            filterStartYear = getArguments().getString("startYear", "");
            filterEndYear = getArguments().getString("endYear", "");
            filterFavoritesOnly = getArguments().getBoolean("favoritesOnly", false);
        }

        // Set up button click listeners
        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchGames(currentPage);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (nextPageUrl != null) {
                currentPage++;
                fetchGames(currentPage);
            }
        });

        btnFilter.setOnClickListener(v -> {
            // Navigate to filter fragment
            Navigation.findNavController(mView).navigate(R.id.action_gamesListFragment_to_filtesrFragment);
        });

        // Set up search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                // Show/hide clear button based on text
                btnClearSearch.setVisibility(searchText.isEmpty() ? View.GONE : View.VISIBLE);
                // Re-filter the current games list
                if (mGames != null) {
                    applyFiltersAndDisplay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set up clear search button
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
        });

        // Load favorites from Firebase
        loadFavorites();

        // Load first page
        fetchGames(currentPage);

        return mView;
    }

    private void loadFavorites() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.loadUserFavorites(favoriteIds -> {
                favoritedGameIds = favoriteIds;
                // Update adapter if it's already created
                if (adapter != null) {
                    adapter.setFavoritedGames(favoriteIds);
                }
            });
        }
    }

    private void applyFiltersAndDisplay() {
        if (mGames == null) {
            return;
        }

        // Apply filters to the games list
        List<Game> filteredGames = filterGames(mGames);

        // Update adapter with filtered games
        adapter = new CustomeAdapter(filteredGames);

        // Set favorited games
        adapter.setFavoritedGames(favoritedGameIds);

        // Set favorite click listener
        adapter.setOnFavoriteClickListener((game, isFavorited) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                if (isFavorited) {
                    mainActivity.addFavByUser(game.getID(), game.getName(), game.getImageUrl());
                } else {
                    mainActivity.removeFavByUser(game.getID());
                }
            }
        });

        adapter.setOnItemClickListener((data, position) -> {
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
    }

    private List<Game> filterGames(List<Game> games){
        List<Game> filteredGames = new ArrayList<>();

        for (Game game : games) {
            // Check search text filter
            if (!searchText.isEmpty()) {
                String gameName = game.getName();
                if (gameName == null || !gameName.toLowerCase().contains(searchText.toLowerCase())) {
                    continue;
                }
            }

            // Check category filter
            if (!filterCategory.equals("All Categories")) {
                String gameGenre = game.getGenres();
                if (gameGenre == null || !gameGenre.toLowerCase().contains(filterCategory.toLowerCase())) {
                    continue;
                }
            }

            // Check year range filter
            if (!filterStartYear.isEmpty() || !filterEndYear.isEmpty()) {
                String releaseDate = game.getReleaseDate();
                if (releaseDate != null && !releaseDate.isEmpty()) {
                    try {
                        int gameYear = Integer.parseInt(releaseDate.substring(0, 4));

                        if (!filterStartYear.isEmpty()) {
                            int startYear = Integer.parseInt(filterStartYear);
                            if (gameYear < startYear) {
                                continue;
                            }
                        }

                        if (!filterEndYear.isEmpty()) {
                            int endYear = Integer.parseInt(filterEndYear);
                            if (gameYear > endYear) {
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        // Skip games with invalid dates
                        continue;
                    }
                }
            }

            // Check favorites filter
            if (filterFavoritesOnly) {
                String gameId = game.getID();
                if (gameId == null || !favoritedGameIds.contains(gameId)) {
                    continue;
                }
            }

            filteredGames.add(game);
        }

        return filteredGames;
    }
    //The function checks if we have already have the desired page games , if we do display them
    //otherwise , we need to call the api
    private void fetchGames(int page) {
        // Check cache first
        if (pageCache.containsKey(page)) {
            Log.d("result", "Loading page " + page + " from cache");
            loadFromCache(page);
            return;
        }

        // Cache miss - fetch from API
        Log.d("result", "Fetching page " + page + " from API");

        //init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE).addConverterFactory(GsonConverterFactory.create())
                .build();
        //connect retrofit to the service
        GameApiService service = retrofit.create(GameApiService.class);
        //call the route with the page as parameter
        Call<GameResponse> call = service.getAllGames(page);

        //call async func
        call.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GameResponse gameResponse = response.body();

                    // Cache the response
                    pageCache.put(page, gameResponse);

                    // Display the data
                    displayGames(gameResponse);

                    Log.d("result", "Games loaded from API: " + gameResponse.getResults().size() + " (Page " + currentPage + ")");
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

    private void loadFromCache(int page) {
        //get the saved response and display the games
        GameResponse gameResponse = pageCache.get(page);
        if (gameResponse != null) {
            displayGames(gameResponse);
        }
    }

    private void displayGames(GameResponse gameResponse) {
        mGames = gameResponse.getResults();

        // Apply filters to the games list
        List<Game> filteredGames = filterGames(mGames);

        // Update pagination state
        nextPageUrl = gameResponse.getNext();
        previousPageUrl = gameResponse.getPrevious();

        // Update UI
        updatePaginationControls();

        adapter = new CustomeAdapter(filteredGames);

        // Set favorited games
        adapter.setFavoritedGames(favoritedGameIds);

        // Set favorite click listener
        adapter.setOnFavoriteClickListener((game, isFavorited) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                if (isFavorited) {
                    mainActivity.addFavByUser(game.getID(), game.getName(), game.getImageUrl());
                } else {
                    mainActivity.removeFavByUser(game.getID());
                }
            }
        });

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

        // Scroll to top when page changes
        recyclerView.scrollToPosition(0);
    }

    private void updatePaginationControls() {
        // Update page info text
        tvPageInfo.setText("Page " + currentPage);

        // Enable/disable Previous button
        btnPrevious.setEnabled(currentPage > 1);

        // Enable/disable Next button
        btnNext.setEnabled(nextPageUrl != null);
    }
}