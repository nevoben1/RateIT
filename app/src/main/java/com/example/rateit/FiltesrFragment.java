package com.example.rateit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.SwitchCompat;

/**
 * A fragment for filtering games by category, year range, and favorites.
 */
public class FiltesrFragment extends Fragment {

    private Spinner spinnerCategory;
    private EditText etStartYear;
    private EditText etEndYear;
    private SwitchCompat switchFavorites;
    private Button btnApply;

    public FiltesrFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filtesr, container, false);

        // Initialize views
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etStartYear = view.findViewById(R.id.etStartYear);
        etEndYear = view.findViewById(R.id.etEndYear);
        switchFavorites = view.findViewById(R.id.switchFavorites);
        btnApply = view.findViewById(R.id.btnApply);

        // Set up category spinner
        setupCategorySpinner();

        // Set up apply button click listener
        btnApply.setOnClickListener(v -> {
            // Get filter values
            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            String startYear = etStartYear.getText().toString();
            String endYear = etEndYear.getText().toString();
            boolean showFavoritesOnly = switchFavorites.isChecked();

            // Create bundle to pass filter data back to GamesListFragment
            Bundle bundle = new Bundle();
            bundle.putString("category", selectedCategory);
            bundle.putString("startYear", startYear);
            bundle.putString("endYear", endYear);
            bundle.putBoolean("favoritesOnly", showFavoritesOnly);

            // Navigate back to GamesListFragment with filter data
            Navigation.findNavController(view).navigate(
                    R.id.action_filtesrFragment_to_gamesListFragment,
                    bundle
            );
        });

        return view;
    }

    private void setupCategorySpinner() {
        // Define categories for games
        String[] categories = {
                "All Categories",
                "Action",
                "Adventure",
                "RPG",
                "Strategy",
                "Shooter",
                "Sports",
                "Racing",
                "Puzzle",
                "Simulation",
                "Platformer",
                "Fighting",
                "Indie"
        };

        // Create adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }
}