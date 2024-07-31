package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.holanda.bilicacies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class search_Recycler_Adapter extends RecyclerView.Adapter<search_Recycler_Adapter.search_viewholder> {
    private Context mContext;
    private ArrayList<search_sell_item_var_link> mFBList;

    private OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public search_Recycler_Adapter(Context context, ArrayList<search_sell_item_var_link> sampleList, search_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public search_Recycler_Adapter.search_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_search_sell_view_layout, parent, false);

        return new search_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull search_Recycler_Adapter.search_viewholder holder, int position) {
        search_sell_item_var_link currentItem = mFBList.get(position);

        String p_Name = currentItem.getProductName();
        String p_Price = currentItem.getPrice();
        String p_Date_Added = currentItem.getDate();
        String p_Cover_URL = currentItem.getImageURL();

        holder.mPesoSign.setVisibility(View.VISIBLE);
        holder.mProductPrice.setVisibility(View.VISIBLE);
        holder.mProductName.setVisibility(View.VISIBLE);
        holder.mDateAdded.setVisibility(View.VISIBLE);
        holder.mProductCover.setVisibility(View.VISIBLE);
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                200, mContext.getResources().getDisplayMetrics()
        );
        holder.mProductCover.getLayoutParams().height = px;


        holder.mProductName.setText(p_Name);




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

        int px1 = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20, mContext.getResources().getDisplayMetrics()
        );

        Glide.with(mContext)
                .load(p_Cover_URL)
                .transform(new RoundedCorners(px1))
                .into(holder.mProductCover);

        holder.mProductPrice.setText(p_Price);










    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class search_viewholder extends RecyclerView.ViewHolder {

        public ImageView mProductCover;
        public TextView mProductName;
        public TextView mProductPrice;
        public TextView mDateAdded;
        public TextView mPesoSign;
        public CardView mCardContainer;


        public search_viewholder(@NonNull View itemView) {
            super(itemView);

            mProductCover = itemView.findViewById(R.id.search_view_img_Product);
            mProductName = itemView.findViewById(R.id.search_view_txt_Product_Name);
            mProductPrice = itemView.findViewById(R.id.search_view_txt_Price);
            mDateAdded = itemView.findViewById(R.id.search_view_txt_Date_Added);
            mPesoSign = itemView.findViewById(R.id.search_view_txt_Peso_Sign);
            mCardContainer = itemView.findViewById(R.id.search_view_Card_Container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mListener.onItemClick(getAdapterPosition());

                }
            });






        }
    }
}
