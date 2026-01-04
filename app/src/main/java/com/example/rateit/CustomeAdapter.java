package com.example.rateit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// com.example.myapplicationrv.R;
//import com.example.myapplicationrv.models.Data;

import com.bumptech.glide.Glide;

import java.util.List;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.MyViewHolder> {

    private List<Game> arr;
    //create an OnClick listener
    private OnItemClickListener listener;

    public CustomeAdapter(List<Game> arr) {

        this.arr = arr;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView gameNameText;
        TextView genreText;
        TextView releaseDateText;
        TextView rateText;
        ImageView imageView;

        public MyViewHolder ( View itemView){
            super(itemView);
            gameNameText = itemView.findViewById(R.id.gameNameText);
            genreText = itemView.findViewById(R.id.genreText);
            releaseDateText = itemView.findViewById(R.id.releaseDateText);
            rateText = itemView.findViewById(R.id.rateText);
            imageView = itemView.findViewById(R.id.imageView);
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
