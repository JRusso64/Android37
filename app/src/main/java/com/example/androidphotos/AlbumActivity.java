package com.example.androidphotos;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get the album name from the intent extras
        String albumName = getIntent().getStringExtra("album_name");

        // Display the album name in a TextView
        TextView albumNameTextView = findViewById(R.id.albumNameTextView);
        albumNameTextView.setText(albumName);

        // Add more code here to display other album contents
    }
}
