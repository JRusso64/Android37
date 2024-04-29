package com.example.androidphotos.Models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public class AppData {
    private static AppData instance = null;
    private List<Album> albums;

    private AppData() {
        // Initialize albums list
        albums = new ArrayList<>();
    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    // Method to get an album by its name
    public Album getAlbumByName(String name) {
        for (Album album : albums) {
            if (name != null && name.equals(album.getName())) {
                return album;
            }
        }
        return null;
    }

    // Method to add an album to the list
    public void addAlbum(Album album) {
        if (!albums.contains(album)) {
            albums.add(album);
        }
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public void saveAlbumsToFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("albums_data.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(albums);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAlbumsFromFile(Context context) {
        try {
            FileInputStream fis = context.openFileInput("albums_data.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (List<Album>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            albums = new ArrayList<>(); // Initialize an empty list if the file does not exist or can't be read
        }
    }
}

