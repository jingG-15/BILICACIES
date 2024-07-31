package com.holanda.bilicacies.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.holanda.bilicacies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class prod_Recycler_Adapter extends RecyclerView.Adapter<prod_Recycler_Adapter.prod_viewholder> {
    private Context mContext;
    private ArrayList<prod_sell_item_var_link> mFBList;

    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public prod_Recycler_Adapter(Context context, ArrayList<prod_sell_item_var_link> sampleList, OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;
    }

    @NonNull
    @Override
    public prod_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_prod_sell_view_layout, parent, false);

        return new prod_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull prod_viewholder holder, int position) {
        prod_sell_item_var_link currentItem = mFBList.get(position);

        String p_Name = currentItem.getProductName();
        String p_Price = currentItem.getPrice();
        String p_Date_Added = currentItem.getDate();
        String p_Cover_URL = currentItem.getImageURL();


        holder.mProductName.setText(p_Name);
        holder.mProductPrice.setText(p_Price);
        try {
            String timeStamp = p_Date_Added;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
            SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy", Locale.getDefault());
            String month = temp1.format(dateFormat.parse(timeStamp));

            holder.mDateAdded.setText(month);
        } catch (ParseException e) {
            e.printStackTrace();

        }

        Glide.with(mContext)
                .load(p_Cover_URL)
                .into(holder.mProductCover);




    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class prod_viewholder extends RecyclerView.ViewHolder {

        public ImageView mProductCover;
        public TextView mProductName;
        public TextView mProductPrice;
        public TextView mDateAdded;


        public prod_viewholder(@NonNull View itemView) {
            super(itemView);

            mProductCover = itemView.findViewById(R.id.prod_view_img_cover_photo);
            mProductName = itemView.findViewById(R.id.prod_view_txt_Product_Name);
            mProductPrice = itemView.findViewById(R.id.prod_view_txt_Price);
            mDateAdded = itemView.findViewById(R.id.prod_view_txt_Date_Added);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListener.onItemClick(getAdapterPosition());

                }
            });






        }
    }

}
