package com.example.androidphotos;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.androidphotos.Adapters.PhotoAdapter;
import com.example.androidphotos.Models.Album;
import com.example.androidphotos.Models.StorePhoto;
import com.example.androidphotos.Models.AppData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import java.util.Objects;
public class AlbumDetailsActivity extends AppCompatActivity implements PhotoAdapter.PhotoClickListener {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private Album selectedAlbum;
    private AppData userSingleton;
    private static final int PICK_IMAGE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        userSingleton = AppData.getInstance();

        recyclerView = findViewById(R.id.photos_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        selectedAlbum = (Album) Objects.requireNonNull(getIntent().getSerializableExtra("selected_album"));
        selectedAlbum = userSingleton.getAlbumByName(selectedAlbum.getName());

        photoAdapter = new PhotoAdapter(selectedAlbum.getPhotos(), this);
        recyclerView.setAdapter(photoAdapter);

        FloatingActionButton fabAddPhoto = findViewById(R.id.add_photo_fab);
        fabAddPhoto.setOnClickListener(v -> pickImage());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                handleImageSelection(selectedImageUri);
            }
        }
    }

    private void handleImageSelection(Uri selectedImageUri) {
        getContentResolver().takePersistableUriPermission(
                selectedImageUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        LocalDateTime currentDateTime = LocalDateTime.now();
        StorePhoto newPhoto = new StorePhoto(selectedImageUri.toString(), currentDateTime);

        if (!photoExistsInAlbum(selectedAlbum, selectedImageUri)) {
            selectedAlbum.addPhoto(newPhoto);
            userSingleton.saveAlbumsToFile(this);
            updatePhotoList();
        } else {
            Toast.makeText(this, "Photo already in album!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean photoExistsInAlbum(Album album, Uri uri) {
        String newPhotoPath = uri.toString();
        return album.getPhotos().stream().anyMatch(photo -> photo.getPath().equals(newPhotoPath));
    }

    private void updatePhotoList() {
        photoAdapter.setPhotos(selectedAlbum.getPhotos());
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPhotoClick(StorePhoto photo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option")
                .setItems(new String[]{"DELETE", "DISPLAY", "MOVE"}, (dialog, which) -> {
                    if (which == 0) {
                        selectedAlbum.removePhoto(photo);
                        userSingleton.saveAlbumsToFile(this);
                        updatePhotoList();
                    } else if (which == 1) {
                        Intent intent = new Intent(AlbumDetailsActivity.this, DisplayPhotoActivity.class);
                        intent.putExtra("PHOTO", photo);
                        intent.putExtra("ALBUM_NAME", selectedAlbum.getName());
                        startActivity(intent);
                    } else if (which == 2) {
                        showAlbumSelectionDialog(photo);
                    }
                });
        builder.show();
    }


    private void showAlbumSelectionDialog(StorePhoto photo) {
        List<Album> otherAlbums = userSingleton.getAlbums().stream()
                .filter(album -> !album.getName().equals(selectedAlbum.getName()))
                .collect(Collectors.toList());

        if (otherAlbums.isEmpty()) {
            Toast.makeText(this, "No Other Albums", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] albumNames = otherAlbums.stream()
                .map(Album::getName)
                .toArray(String[]::new);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an Album for Photo")
                .setItems(albumNames, (dialog, which) -> {
                    Album targetAlbum = otherAlbums.get(which);
                    movePhotoToAlbum(photo, targetAlbum);
                });
        builder.show();
    }

    private void movePhotoToAlbum(StorePhoto photo, Album targetAlbum) {
        if (photoExistsInAlbum(targetAlbum, Uri.parse(photo.getPath()))) {
            Toast.makeText(this, "Same Photo Is Already in Other Album", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedAlbum.removePhoto(photo);
        targetAlbum.addPhoto(photo);
        userSingleton.saveAlbumsToFile(this);
        updatePhotoList();
        Toast.makeText(this, "Photo Moved!", Toast.LENGTH_SHORT).show();
    }
}