package com.example.androidphotos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Album> albums;
    private AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button createAlbumButton = findViewById(R.id.createAlbumButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        albums = new ArrayList<>();
        albumAdapter = new AlbumAdapter(albums);
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
