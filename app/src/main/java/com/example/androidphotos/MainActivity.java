package com.example.androidphotos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidphotos.Adapters.AlbumAdapter;
import com.example.androidphotos.Models.Album;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.androidphotos.Models.AppData;
import java.util.List;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;
import com.example.androidphotos.Models.Tag;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import android.widget.AdapterView;

import android.widget.AutoCompleteTextView;

import com.example.androidphotos.Models.StorePhoto;

import java.util.ArrayList;
import java.util.HashSet;

import java.util.Set;


public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnAlbumListener {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private List<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        AppData appData = AppData.getInstance();
        appData.loadAlbumsFromFile(this);

        albumList = appData.getAlbums();
        recyclerView = findViewById(R.id.albums_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        albumAdapter = new AlbumAdapter(this, albumList, this);
        recyclerView.setAdapter(albumAdapter);

        FloatingActionButton fab = findViewById(R.id.add_album_fab);
        fab.setOnClickListener(view -> showAlbumCreationDialog());

        FloatingActionButton searchFab = findViewById(R.id.search_fab);
        searchFab.setOnClickListener(view -> showSearchDialog());
    }

    private void showSearchDialog() {
        String[] options = {"1 Tag", "2 Tags"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Search Option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showSingleTagInputDialog();
                    } else {
                        showConjunctionDisjunctionChoice();
                    }
                });
        builder.show();
    }

    private void showSingleTagInputDialog() {
        askForSingleTagDetails();
    }

    private void showConjunctionDisjunctionChoice() {
        String[] conjunctionOptions = {"Conjunction (AND)", "Disjunction (OR)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Condition")
                .setItems(conjunctionOptions, (dialog, which) -> {
                    boolean isConjunction = (which == 0);
                    showDoubleTagInputDialog(isConjunction);
                });
        builder.show();
    }

    private void showDoubleTagInputDialog(boolean isConjunction) {
        askForDoubleTagDetails(isConjunction);
    }

    private void askForSingleTagDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Tag Details");

        View customView = getLayoutInflater().inflate(R.layout.tag_input_dialog, null);
        builder.setView(customView);

        AutoCompleteTextView tagValueInput = customView.findViewById(R.id.tag_value_input);
        Spinner tagTypeSpinner = customView.findViewById(R.id.tag_type_spinner);

        setupTagTypeSpinner(tagTypeSpinner);
        setupAutoCompleteTextView(tagValueInput, tagTypeSpinner);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String tagType = tagTypeSpinner.getSelectedItem().toString();
            String tagValue = tagValueInput.getText().toString().trim();

            if (!tagValue.isEmpty()) {
                Tag newTag = new Tag(tagType, tagValue);
                performSearch(newTag, null, false);
            } else {
                Toast.makeText(MainActivity.this, "Tag value cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void askForDoubleTagDetails(boolean isConjunction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Tags Details");

        View customView = getLayoutInflater().inflate(R.layout.double_tag_input_dialog, null);
        builder.setView(customView);

        AutoCompleteTextView tagValueInput1 = customView.findViewById(R.id.tag_value_input1);
        Spinner tagTypeSpinner1 = customView.findViewById(R.id.tag_type_spinner1);
        AutoCompleteTextView tagValueInput2 = customView.findViewById(R.id.tag_value_input2);
        Spinner tagTypeSpinner2 = customView.findViewById(R.id.tag_type_spinner2);

        setupTagTypeSpinner(tagTypeSpinner1);
        setupTagTypeSpinner(tagTypeSpinner2);
        setupAutoCompleteTextView(tagValueInput1, tagTypeSpinner1);
        setupAutoCompleteTextView(tagValueInput2, tagTypeSpinner2);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String tagType1 = tagTypeSpinner1.getSelectedItem().toString();
            String tagValue1 = tagValueInput1.getText().toString().trim();
            String tagType2 = tagTypeSpinner2.getSelectedItem().toString();
            String tagValue2 = tagValueInput2.getText().toString().trim();

            if (!tagValue1.isEmpty() && !tagValue2.isEmpty()) {
                Tag newTag1 = new Tag(tagType1, tagValue1);
                Tag newTag2 = new Tag(tagType2, tagValue2);
                performSearch(newTag1, newTag2, isConjunction);
            } else {
                Toast.makeText(MainActivity.this, "Tag values can not be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setupTagTypeSpinner(Spinner tagTypeSpinner) {
        String[] tagTypes = {"PERSON", "LOCATION"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeSpinner.setAdapter(adapter);
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView tagValueInput, Spinner tagTypeSpinner) {
        ArrayAdapter<String> tagValueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        tagValueInput.setAdapter(tagValueAdapter);

        tagTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTagType = (String) parent.getItemAtPosition(position);
                List<String> tagSuggestions = getTagSuggestions(selectedTagType);
                tagValueAdapter.clear();
                tagValueAdapter.addAll(tagSuggestions);
                tagValueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private List<String> getTagSuggestions(String tagType) {
        Set<String> uniqueTagValues = new HashSet<>();
        AppData appData = AppData.getInstance();
        List<Album> albums = appData.getAlbums();

        for (Album album : albums) {
            for (StorePhoto photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getKey().equalsIgnoreCase(tagType)) {
                        uniqueTagValues.add(tag.getVal());
                    }
                }
            }
        }

        return new ArrayList<>(uniqueTagValues);
    }

    private void performSearch(Tag firstTag, Tag secondTag, boolean isConjunction) {
        ArrayList<StorePhoto> searchResults = new ArrayList<>();
        HashSet<String> addedPaths = new HashSet<>();
        AppData appData = AppData.getInstance();

        for (Album album : appData.getAlbums()) {
            for (StorePhoto photo : album.getPhotos()) {
                if (matchesSearchCriteria(photo, firstTag, secondTag, isConjunction)) {
                    String photoPath = photo.getPath();
                    if (!addedPaths.contains(photoPath)) {
                        searchResults.add(photo);
                        addedPaths.add(photoPath);
                    }
                }
            }
        }

        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
        intent.putExtra("search_results", searchResults);
        startActivity(intent);
    }

    private boolean matchesSearchCriteria(StorePhoto photo, Tag firstTag, Tag secondTag, boolean isConjunction) {
        boolean firstTagMatch = photo.getTags().contains(firstTag);
        boolean secondTagMatch = secondTag != null && photo.getTags().contains(secondTag);

        if (isConjunction) {
            return firstTagMatch && secondTagMatch;
        } else {
            return firstTagMatch || secondTagMatch;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppData appData = AppData.getInstance();
        appData.loadAlbumsFromFile(this);
        albumList = appData.getAlbums();

        if (albumAdapter != null) {
            albumAdapter.setAlbums(albumList);
            albumAdapter.notifyDataSetChanged();
        }
    }

    private void showAlbumCreationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.album_creation);
        dialog.setTitle("Create New Album");

        final EditText editTextAlbumName = dialog.findViewById(R.id.editTextAlbumName);
        Button buttonSave = dialog.findViewById(R.id.buttonSave);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(v -> {
            String albumName = editTextAlbumName.getText().toString().trim();

            if (!albumName.isEmpty()) {
                AppData appData = AppData.getInstance();
                if (!albumNameExists(albumName, appData.getAlbums())) {
                    Album newAlbum = new Album(albumName);
                    appData.getAlbums().add(newAlbum);
                    appData.saveAlbumsToFile(MainActivity.this);
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "An album with that name already exists.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Album name textbox can't be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean albumNameExists(String albumName, List<Album> albums) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAlbumClick(int position) {
        Album selectedAlbum = albumList.get(position);
        Intent intent = new Intent(this, AlbumDetailsActivity.class);
        intent.putExtra("selected_album", selectedAlbum);
        startActivity(intent);
    }

    @Override
    public void onAlbumLongClick(int position) {
        Album selectedAlbum = albumList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Album Options")
                .setItems(new CharSequence[]{"DELETE", "RENAME"}, (dialog, which) -> handleAlbumOption(selectedAlbum, which))
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void handleAlbumOption(Album selectedAlbum, int option) {
        switch (option) {
            case 0:
                showDeleteConfirmationDialog(selectedAlbum);
                break;
            case 1:
                showRenameAlbumDialog(selectedAlbum);
                break;
        }
    }

    private void showDeleteConfirmationDialog(final Album albumToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Album")
                .setMessage("Confirm if you want to delete album.")
                .setPositiveButton("DELETE", (dialog, which) -> {
                    AppData.getInstance().getAlbums().remove(albumToDelete);
                    albumAdapter.notifyDataSetChanged();
                    AppData.getInstance().saveAlbumsToFile(MainActivity.this);
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void showRenameAlbumDialog(final Album albumToRename) {
        final EditText editText = new EditText(this);
        editText.setText(albumToRename.getName());

        new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setView(editText)
                .setPositiveButton("RENAME", (dialog, which) -> {
                    String newAlbumName = editText.getText().toString();
                    albumToRename.setName(newAlbumName);
                    albumAdapter.notifyDataSetChanged();
                    AppData.getInstance().saveAlbumsToFile(MainActivity.this);
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

}