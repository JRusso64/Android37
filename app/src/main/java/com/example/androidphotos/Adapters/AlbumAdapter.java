package com.example.androidphotos.Adapters;

import com.example.androidphotos.Models.Album;
import com.example.androidphotos.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<Album> albumList;
    private Context context;
    private OnAlbumListener onAlbumListener;

    public AlbumAdapter(Context context, List<Album> albumList, OnAlbumListener onAlbumListener) {
        this.context = context;
        this.albumList = albumList;
        this.onAlbumListener = onAlbumListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view, onAlbumListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.albumNameTextView.setText(album.getName());
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView albumNameTextView;
        private OnAlbumListener onAlbumListener;

        public ViewHolder(View itemView, OnAlbumListener onAlbumListener) {
            super(itemView);
            albumNameTextView = itemView.findViewById(R.id.albumNameTextView);
            this.onAlbumListener = onAlbumListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAlbumListener.onAlbumClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onAlbumListener.onAlbumLongClick(getAdapterPosition());
            return true;
        }
    }
    public void setAlbums(List<Album> albums) {
        this.albumList = albums;
    }

    public interface OnAlbumListener {
        void onAlbumClick(int position);
        void onAlbumLongClick(int position);
    }

    // Additional methods for managing the dataset, like adding or removing items
}
