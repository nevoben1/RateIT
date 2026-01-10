package com.example.rateit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// com.example.myapplicationrv.R;
//import com.example.myapplicationrv.models.Data;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.MyViewHolder> {

    private List<Game> arr;
    //create an OnClick listener
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    // Set to track which games are favorited
    private Set<String> favoritedGameIds = new HashSet<>();

    public CustomeAdapter(List<Game> arr) {

        this.arr = arr;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView gameNameText;
        TextView genreText;
        TextView releaseDateText;
        TextView rateText;
        ImageView imageView;
        ImageButton favoriteButton;

        public MyViewHolder ( View itemView){
            super(itemView);
            gameNameText = itemView.findViewById(R.id.gameNameText);
            genreText = itemView.findViewById(R.id.genreText);
            releaseDateText = itemView.findViewById(R.id.releaseDateText);
            rateText = itemView.findViewById(R.id.rateText);
            imageView = itemView.findViewById(R.id.imageView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }

    }
    //create the click interface
    public interface OnItemClickListener {
        void onItemClick(Game data, int position);
    }
    //set the interface
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    //create the favorite click interface
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Game game, boolean isFavorited);
    }
    //set the favorite interface
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteListener = listener;
    }

    //method to update favorite status
    public void setFavoritedGames(Set<String> favoritedGameIds) {
        this.favoritedGameIds = favoritedGameIds;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view , parent , false ) ;

        MyViewHolder myViewHolder = new MyViewHolder(view);

       return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Game data = arr.get(position);
        holder.gameNameText.setText(data.getName());
        holder.rateText.setText(data.getRating()+"");
        holder.releaseDateText.setText(data.getReleaseDate());
        holder.genreText.setText(data.getGenres());
        Glide.with(holder.itemView.getContext())
                .load(data.getImageUrl())
                        .into(holder.imageView);

        // Set favorite button state
        boolean isFavorited = favoritedGameIds.contains(data.getID());
        holder.favoriteButton.setSelected(isFavorited);

        // Handle favorite button click
        holder.favoriteButton.setOnClickListener(v -> {
            boolean newFavoriteState = !holder.favoriteButton.isSelected();
            holder.favoriteButton.setSelected(newFavoriteState);

            // Update local set
            if (newFavoriteState) {
                favoritedGameIds.add(data.getID());
            } else {
                favoritedGameIds.remove(data.getID());
            }

            // Notify listener
            if (favoriteListener != null) {
                favoriteListener.onFavoriteClick(data, newFavoriteState);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(data, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }


}
