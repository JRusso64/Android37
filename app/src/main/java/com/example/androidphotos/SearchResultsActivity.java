package com.example.androidphotos;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidphotos.Adapters.PhotoAdapter;
import com.example.androidphotos.Models.StorePhoto;

import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.androidphotos.Models.Album;
import com.example.androidphotos.Models.AppData;
import java.util.List;
import android.content.Intent;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private ArrayList<StorePhoto> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.search_results_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        Button homeButton = findViewById(R.id.home_button);
        Button createAlbumButton = findViewById(R.id.create_album_button);

        searchResults = (ArrayList<StorePhoto>) getIntent().getSerializableExtra("search_results");
        photoAdapter = new PhotoAdapter(searchResults, this::onPhotoClicked);
        recyclerView.setAdapter(photoAdapter);

        homeButton.setOnClickListener(v -> finish());
        createAlbumButton.setOnClickListener(v -> createAlbumFromSearchResults());
    }

    private void onPhotoClicked(StorePhoto photo) {
    }

    private void createAlbumFromSearchResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Album Name");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("CREATE", (dialog, which) -> {
            String albumName = input.getText().toString().trim();

            if (!albumName.isEmpty()) {
                AppData appData = AppData.getInstance();
                if (!albumNameExists(albumName, appData.getAlbums())) {
                    Album newAlbum = new Album(albumName);
                    newAlbum.getPhotos().addAll(searchResults);
                    appData.getAlbums().add(newAlbum);
                    appData.saveAlbumsToFile(this);

                    Toast.makeText(this, "Album created.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SearchResultsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "An album with this name already exists.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Album name textbox can't be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private boolean albumNameExists(String albumName, List<Album> albums) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(albumName)) {
                return true;
            }
        }
        return false;
    }

}
