package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.holanda.bilicacies.R;
import com.holanda.bilicacies.Services.TouchImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewPageAdapter_FullscreenPrev extends PagerAdapter {
    Context context;
    ArrayList<HashMap<String, String>> URL_List;
    LayoutInflater layoutInflater;


    public ViewPageAdapter_FullscreenPrev(Context context, ArrayList<HashMap<String, String>> Image_URL_List) {
        this.context = context;
        this.URL_List = Image_URL_List;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return URL_List.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.pv_item, container, false);

        TouchImageView imageView = itemView.findViewById(R.id.imageView_prev);

        HashMap<String, String> currentRow = URL_List.get(position);

        String URL_curr = currentRow.get("URL");

        Glide.with(context)
                .load(URL_curr)
                .error(R.drawable.ic_baseline_error_24)
                .centerInside()
                .into(imageView);



        container.addView(itemView);



        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
