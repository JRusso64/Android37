package com.example.androidphotos.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidphotos.Models.StorePhoto;
import com.example.androidphotos.R;
import java.io.InputStream;
import java.util.List;
import android.util.Log;
import android.widget.TextView;
import java.io.File;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<StorePhoto> photos;
    private PhotoClickListener listener;
    public PhotoAdapter(List<StorePhoto> photos, PhotoClickListener listener)
    {
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorePhoto photo = photos.get(position);
        Log.d("PhotoAdapter", "Loading Image at Position " + position + ": " + photo.getPath());
        int spanCount = 3; // the number of columns in the grid
        int padding = holder.itemView.getContext().getResources().getDisplayMetrics().densityDpi / 160 * 4; // Convert 4dp padding to pixels
        int screenWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int availableWidth = screenWidth - (spanCount + 1) * padding; // Total padding space to subtract
        int imageSize = availableWidth / spanCount;

        ViewGroup.LayoutParams imageLayoutParams = holder.imageView.getLayoutParams();
        imageLayoutParams.width = imageSize; // Set the ImageView width to the calculated imageSize
        imageLayoutParams.height = imageSize; // Set the ImageView height to the calculated imageSize to make it square
        holder.imageView.setLayoutParams(imageLayoutParams);

        // Set the caption for the photo
        String filename = new File(photo.getPath()).getName();
        photo.setCaption(filename); // If you want to store the caption in your StorePhoto object
        holder.textViewCaption.setText(filename);

        try {
            Uri imageUri = Uri.parse(photo.getPath());
            InputStream imageStream = holder.itemView.getContext().getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            holder.imageView.setImageBitmap(selectedImage);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PhotoAdapter", "Error Loading Image", e);
            // Consider setting a placeholder image or notifying the user
        }

        holder.itemView.setOnClickListener(v -> {
            // Use the listener when an item is clicked
            if (listener != null) {
                listener.onPhotoClick(photo);
            }
        });
    }





    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewCaption;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewCaption = itemView.findViewById(R.id.textViewCaption);
        }
    }

    public void setPhotos(List<StorePhoto> newPhotos) {
        this.photos = newPhotos;
        notifyDataSetChanged();
    }

    public interface PhotoClickListener {
        void onPhotoClick(StorePhoto photo);
    }


}
