package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.holanda.bilicacies.Activities.act_Photo_Product_Fullscreen;
import com.holanda.bilicacies.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<HashMap<String, String>> URL_List;
    LayoutInflater layoutInflater;


    public ViewPagerAdapter(Context context, ArrayList<HashMap<String, String>> Image_URL_List) {
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

        ImageView imageView = itemView.findViewById(R.id.imageView_prev);

        HashMap<String, String> currentRow = URL_List.get(position);

        String URL_curr = currentRow.get("URL");

        Glide.with(context)
                .load(URL_curr)
                .error(R.drawable.ic_baseline_error_24)
                .centerInside()
                .into(imageView);

        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();


                Intent ViewSpecificProduct = new Intent(context, act_Photo_Product_Fullscreen.class);

                ViewSpecificProduct.putExtra("phfs_array_size", String.valueOf(URL_List.size()));
                ViewSpecificProduct.putExtra("phfs_clicked_item", String.valueOf(position + 1));

                for(int i = 0; i < URL_List.size(); i++){
                    HashMap<String, String> currRow = URL_List.get(i);

                    String URL_pass = currRow.get("URL");
                    ViewSpecificProduct.putExtra("phfs_URL_" + i, URL_pass);

                }

                context.startActivity(ViewSpecificProduct);


            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
