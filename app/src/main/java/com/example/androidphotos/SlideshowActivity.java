package com.example.androidphotos;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import android.net.Uri;

import com.example.androidphotos.Models.StorePhoto;
import com.example.androidphotos.Models.Tag;

import java.util.List;

public class SlideshowActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private TextView captionTextView;
    private LinearLayout tagsContainer;
    private List<StorePhoto> photos;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        photoImageView = findViewById(R.id.photoImageView);
        captionTextView = findViewById(R.id.captionTextView);
        tagsContainer = findViewById(R.id.tagsContainer);
        Button previousButton = findViewById(R.id.previousButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());
        previousButton.setOnClickListener(v -> showPhoto(-1));
        nextButton.setOnClickListener(v -> showPhoto(1));

        photos = (List<StorePhoto>) getIntent().getSerializableExtra("photos");
        currentIndex = getIntent().getIntExtra("current_index", 0);

        showPhoto(0);
    }

    private void showPhoto(int offset) {
        currentIndex = (currentIndex + offset + photos.size()) % photos.size();
        StorePhoto photo = photos.get(currentIndex);

        Bitmap bitmap = loadImage(photo.getPath());
        photoImageView.setImageBitmap(bitmap);

        captionTextView.setText(photo.getCaption());

        loadTags(photo);
    }

    private Bitmap loadImage(String path) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.parse(path));
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadTags(StorePhoto photo) {
        tagsContainer.removeAllViews();
        for (Tag tag : photo.getTags()) {
            TextView tagView = new TextView(this);
            tagView.setText(tag.getKey() + ": " + tag.getVal());
            tagsContainer.addView(tagView);
        }
    }

}