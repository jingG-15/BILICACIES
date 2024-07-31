package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.holanda.bilicacies.R;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<Uri> arrayList;

    public MyAdapter(Context context, ArrayList<Uri> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setArrayList(ArrayList<Uri> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (mInflater != null) {
            convertView = mInflater.inflate(R.layout.rec_item_list_photo_per_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView_prev);


        Glide.with(context)
                .load(arrayList.get(position))
                .into(imageView);

        return convertView;
    }

    
}