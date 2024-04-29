package com.example.androidphotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidphotos.Models.StorePhoto;
import com.example.androidphotos.Models.Tag;
import com.example.androidphotos.Adapters.TagAdapter;
import java.util.List;
import java.io.InputStream;
import java.util.HashSet;
import com.example.androidphotos.Models.Album;
import com.example.androidphotos.Models.AppData;
import java.util.ArrayList;
import android.widget.TextView;
import java.util.Objects;

public class DisplayPhotoActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private ListView tagsListView;
    private Button backButton, addTagButton, removeTagButton, slideshowButton;
    private EditText inputTagValue;
    private TextView captionTextView;
    private TagAdapter tagAdapter;
    private StorePhoto selectedPhoto;
    private String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        initializeViews();

        selectedPhoto = (StorePhoto) Objects.requireNonNull(getIntent().getSerializableExtra("PHOTO"));
        albumName = getIntent().getStringExtra("ALBUM_NAME");

        if (selectedPhoto != null) {
            loadPhoto(selectedPhoto.getPath());
            captionTextView.setText(selectedPhoto.getCaption());
            tagAdapter = new TagAdapter(this, selectedPhoto.getTags() != null ? selectedPhoto.getTags() : new HashSet<>());
            tagsListView.setAdapter(tagAdapter);
        } else {
            Toast.makeText(this, "Photo data not available!", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupButtonListeners();
    }

    private void initializeViews() {
        photoImageView = findViewById(R.id.photoImageView);
        tagsListView = findViewById(R.id.tagsListView);
        backButton = findViewById(R.id.backButton);
        addTagButton = findViewById(R.id.addTagButton);
        removeTagButton = findViewById(R.id.removeTagButton);
        slideshowButton = findViewById(R.id.slideshowButton);
        inputTagValue = new EditText(this);
        captionTextView = findViewById(R.id.captionTextView);
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(view -> finish());
        addTagButton.setOnClickListener(view -> showAddTagDialog());
        removeTagButton.setOnClickListener(view -> showDeleteTagDialog());
        slideshowButton.setOnClickListener(view -> startSlideshow());
    }

    private void loadPhoto(String imagePath) {
        try {
            Bitmap bitmap;
            if (imagePath.startsWith("content://")) {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(imagePath));
                bitmap = BitmapFactory.decodeStream(inputStream);
            } else {
                bitmap = BitmapFactory.decodeFile(imagePath);
            }

            if (bitmap != null) {
                photoImageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Unable to load photo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading photo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Tag Type");

        String[] tagTypes = {"PERSON", "LOCATION"};
        builder.setItems(tagTypes, (dialog, which) -> {
            String selectedType = tagTypes[which];
            showEnterTagValueDialog(selectedType);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEnterTagValueDialog(String tagType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Value for " + tagType + " Tag");

        builder.setView(inputTagValue);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String tagValue = inputTagValue.getText().toString().trim();

            if (!tagValue.isEmpty()) {
                Tag newTag = new Tag(tagType, tagValue);
                if (!tagAlreadyExists(newTag)) {
                    selectedPhoto.addTag(newTag);
                    tagAdapter.updateTags(selectedPhoto.getTags());
                    saveChanges();
                } else {
                    Toast.makeText(DisplayPhotoActivity.this, "Tag already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DisplayPhotoActivity.this, "Tag value can't be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private boolean tagAlreadyExists(Tag tag) {
        for (Tag existingTag : selectedPhoto.getTags()) {
            if (existingTag.getKey().equals(tag.getKey()) && existingTag.getVal().equals(tag.getVal())) {
                return true;
            }
        }
        return false;
    }

    private void showDeleteTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Tag to Delete");

        Tag[] tagArray = selectedPhoto.getTags().toArray(new Tag[0]);
        String[] tagStrings = new String[tagArray.length];
        for (int i = 0; i < tagArray.length; i++) {
            tagStrings[i] = tagArray[i].getKey() + ": " + tagArray[i].getVal();
        }

        builder.setItems(tagStrings, (dialog, which) -> {
            Tag selectedTag = tagArray[which];
            selectedPhoto.removeTags(selectedTag);
            tagAdapter.updateTags(selectedPhoto.getTags());
            saveChanges();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveChanges() {
        Album album = AppData.getInstance().getAlbumByName(albumName);
        if (album != null) {
            album.updatePhoto(selectedPhoto);
            AppData.getInstance().saveAlbumsToFile(this);
        } else {
            Toast.makeText(this, "ERROR: Album not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSlideshow() {
        Album album = AppData.getInstance().getAlbumByName(albumName);
        if (album != null) {
            List<StorePhoto> allPhotosInAlbum = album.getPhotos();
            Intent slideshowIntent = new Intent(DisplayPhotoActivity.this, SlideshowActivity.class);
            slideshowIntent.putExtra("photos", new ArrayList<>(allPhotosInAlbum));

            int currentIndex = -1;
            for (int i = 0; i < allPhotosInAlbum.size(); i++) {
                if (selectedPhoto.getPath().equals(allPhotosInAlbum.get(i).getPath())) {
                    currentIndex = i;
                    break;
                }
            }
            slideshowIntent.putExtra("current_index", currentIndex);

            startActivity(slideshowIntent);
        } else {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
        }
    }
}
