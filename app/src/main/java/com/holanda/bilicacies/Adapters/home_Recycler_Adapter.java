package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.bumptech.glide.request.RequestOptions;
import com.holanda.bilicacies.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class home_Recycler_Adapter extends RecyclerView.Adapter<home_Recycler_Adapter.home_viewholder> {
    private Context mContext;
    private ArrayList<home_sell_item_var_link> mFBList;

    private home_Recycler_Adapter.OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public home_Recycler_Adapter(Context context, ArrayList<home_sell_item_var_link> sampleList, home_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public home_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_home_sell_view_layout, parent, false);

        return new home_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull home_Recycler_Adapter.home_viewholder holder, int position) {
        home_sell_item_var_link currentItem = mFBList.get(position);

        String p_Name = currentItem.getProductName();
        String p_Price = currentItem.getPrice();
        String p_Date_Added = currentItem.getDate();
        String p_Cover_URL = currentItem.getImageURL();

        if(p_Name.equals("Biliran_Delicacies_011593")) {

            holder.mDelicaciesLabel.setText("Biliran Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.biliran_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Naval_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Naval Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.naval_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Almeria_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Almeria Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.almeria_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Kawayan_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Kawayan Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.kawayan_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Culaba_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Culaba Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.culaba_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Caibiran_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Caibiran Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.caibiran_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Cabucgayan_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Cabucgayan Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.cabucgayan_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Maripipi_Delicacies_011593")){

            holder.mDelicaciesLabel.setText("Maripipi Delicacies");
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.getLayoutParams().height = 500;
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.drawable.maripipi_icon))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.mProductCover);



        }else if(p_Name.equals("Spacer_011593")){

            holder.mDelicaciesLabel.setVisibility(View.GONE);
            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.setVisibility(View.GONE);
            holder.mCardContainer.getLayoutParams().height = 100;
            holder.mCardContainer.setBackgroundColor(Color.TRANSPARENT);



        }else if(p_Name.equals("Product_Like_1_011593")){

            holder.mDelicaciesLabel.setVisibility(View.VISIBLE);
            holder.mDelicaciesLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.mDelicaciesLabel.setText("Products you");

            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.setVisibility(View.GONE);
            holder.mCardContainer.setBackgroundColor(Color.TRANSPARENT);


            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.mCardContainer.getLayoutParams();
            layoutParams.setMargins(2, 2, 0, 2);
            holder.mCardContainer.setLayoutParams(layoutParams);



        }else if(p_Name.equals("Product_Like_2_011593")){

            holder.mDelicaciesLabel.setVisibility(View.VISIBLE);
            holder.mDelicaciesLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.mDelicaciesLabel.setText("might like.");

            holder.mPesoSign.setVisibility(View.GONE);
            holder.mProductPrice.setVisibility(View.GONE);
            holder.mProductName.setVisibility(View.GONE);
            holder.mDateAdded.setVisibility(View.GONE);
            holder.mProductCover.setVisibility(View.GONE);
            holder.mCardContainer.setBackgroundColor(Color.TRANSPARENT);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.mCardContainer.getLayoutParams();
            layoutParams.setMargins(0, 2, 2, 2);
            holder.mCardContainer.setLayoutParams(layoutParams);



        }else{

            holder.mDelicaciesLabel.setVisibility(View.GONE);
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


        }


        holder.mProductPrice.setText(p_Price);










    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class home_viewholder extends RecyclerView.ViewHolder {

        public ImageView mProductCover;
        public TextView mProductName;
        public TextView mProductPrice;
        public TextView mDateAdded;
        public TextView mDelicaciesLabel;
        public TextView mPesoSign;
        public CardView mCardContainer;


        public home_viewholder(@NonNull View itemView) {
            super(itemView);

            mProductCover = itemView.findViewById(R.id.home_view_img_Product);
            mProductName = itemView.findViewById(R.id.home_view_txt_Product_Name);
            mProductPrice = itemView.findViewById(R.id.home_view_txt_Price);
            mDateAdded = itemView.findViewById(R.id.home_view_txt_Date_Added);
            mDelicaciesLabel = itemView.findViewById(R.id.home_view_txt_Delicacy_Name);
            mPesoSign = itemView.findViewById(R.id.home_view_txt_Peso_Sign);
            mCardContainer = itemView.findViewById(R.id.home_view_Card_Container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListener.onItemClick(getAdapterPosition());




                }
            });






        }
    }



}
