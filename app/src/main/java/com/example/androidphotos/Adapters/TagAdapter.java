package com.example.androidphotos.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.androidphotos.Models.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagAdapter extends BaseAdapter {

    private Context context;
    private List<Tag> tagList;

    public TagAdapter(Context context, Set<Tag> tags) {
        this.context = context;
        this.tagList = new ArrayList<>(tags);
    }

    @Override
    public int getCount() {
        return tagList.size();
    }

    @Override
    public Tag getItem(int position) {
        return tagList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Tag tag = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(tag.getKey() + ": " + tag.getVal());

        return convertView;
    }

    public void updateTags(Set<Tag> newTags) {
        tagList.clear();
        tagList.addAll(newTags);
        notifyDataSetChanged();
    }
}
