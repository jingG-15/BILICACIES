package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.holanda.bilicacies.R;
import com.holanda.bilicacies.Services.TouchImageView;

public class act_Single_Photo_Fullscreen extends AppCompatActivity {

    TouchImageView img_preview;
    String media_Type, media_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo_fullscreen);

        img_preview = findViewById(R.id.imageView_Single_View);

        media_Type = getIntent().getExtras().getString("SPP_Media_Type");
        media_Data = getIntent().getExtras().getString("SPP_Media_Data");



        if(media_Type.equals("URL")){

            Glide.with(act_Single_Photo_Fullscreen.this)
                    .load(media_Data)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                    .into(img_preview);


        }



    }
}