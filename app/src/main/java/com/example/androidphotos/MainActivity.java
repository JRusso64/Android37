package com.example.androidphotos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private List<Album> albums;
    private AlbumAdapter albumAdapter;

    private void openAlbum(Album album) {
        // Handle opening the album
        // For example:
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("album_name", album.getName());
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button createAlbumButton = findViewById(R.id.createAlbumButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        albums = new ArrayList<>();
        albumAdapter = new AlbumAdapter(albums, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album album) {
                // Handle opening the album
                // For example:
                openAlbum(album);
            }
        });



        recyclerView.setAdapter(albumAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle creating a new album
                // For example:
                String albumName = "New Album";
                Album newAlbum = new Album(albumName);
                albums.add(newAlbum);
                albumAdapter.notifyDataSetChanged();
            }
        });
    }
}
