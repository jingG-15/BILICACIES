package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.holanda.bilicacies.Adapters.ViewPageAdapter_FullscreenPrev;
import com.holanda.bilicacies.Adapters.ViewPagerAdapter;
import com.holanda.bilicacies.R;

import java.util.ArrayList;
import java.util.HashMap;

public class act_Photo_Product_Fullscreen extends AppCompatActivity {

    String URL_count;
    String item_clicked;

    ArrayList<HashMap<String, String>> Image_URL_List;

    ViewPager viewPager;
    ViewPageAdapter_FullscreenPrev myCustomPagerAdapter;

    TextView txtSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_product_fullscreen);

        txtSeq = findViewById(R.id.phfs_txt_seq);

        URL_count = getIntent().getExtras().getString("phfs_array_size");
        item_clicked = getIntent().getExtras().getString("phfs_clicked_item");

        txtSeq.setText(item_clicked + "/" + URL_count);

        Image_URL_List = new ArrayList<>();

        for(int i = 0; i < Integer.parseInt(URL_count); i++){
            String curr_photo_seq = String.valueOf(i + 1);
            HashMap<String, String> result_arr = new HashMap<>();

            result_arr.put("URL", getIntent().getExtras().getString("phfs_URL_" + i));

            Image_URL_List.add(result_arr);

        }

        viewPager = findViewById(R.id.viewPager_1);
        myCustomPagerAdapter = new ViewPageAdapter_FullscreenPrev(this, Image_URL_List);
        viewPager.setAdapter(myCustomPagerAdapter);

        PageListener pageListener = new PageListener();
        viewPager.addOnPageChangeListener(pageListener);
        viewPager.setCurrentItem(Integer.parseInt(item_clicked) - 1);

    }


    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            String pageseq = (position + 1) + "/" + URL_count;
            txtSeq.setText(pageseq);
        }
    }
}