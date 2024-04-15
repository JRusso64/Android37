package com.example.androidphotos;


import java.util.ArrayList;
import java.util.List;
public class Album {
    private ArrayList<Photo> photos = new ArrayList<Photo>();
    private String name;

    public Album(String name){
        this.name = name;
    }
    //Return the photos arraylist
    public ArrayList<Photo> getPhotos(){
        return this.photos;
    }
    //Add photo to album
    public void addPhoto(Photo photo){
        this.photos.add(photo);
    }

    public String getName() {
        return this.name;
    }

    public void removePhoto(Photo photo){
        this.photos.remove(photo);
    }

}
