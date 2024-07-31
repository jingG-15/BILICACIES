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

public class convo_Recycler_Adapter extends RecyclerView.Adapter<convo_Recycler_Adapter.convo_viewholder> {
    private Context mContext;
    private ArrayList<convo_variable_links> mFBList;

    private convo_Recycler_Adapter.OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public convo_Recycler_Adapter(Context context, ArrayList<convo_variable_links> sampleList, convo_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public convo_Recycler_Adapter.convo_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_convo_itemlist_layout, parent, false);

        return new convo_Recycler_Adapter.convo_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull convo_Recycler_Adapter.convo_viewholder holder, int position) {
        convo_variable_links currentItem = mFBList.get(position);

        String cn_Convo_ID = currentItem.cn_get_Convo_ID();
        String cn_Seller_ID = currentItem.cn_get_Seller_ID();
        String cn_Buyer_ID = currentItem.cn_get_Buyer_ID();
        String cn_Convo_Updated = currentItem.cn_get_Convo_Updated();
        String cn_First_Message = currentItem.cn_get_First_Message();
        String cn_Seller_Fullname = currentItem.cn_get_Seller_Fullname();
        String cn_Buyer_Fullname = currentItem.cn_get_Buyer_Fullname();
        String cn_Logged_Username = currentItem.cn_get_Logged_Username();


        if(cn_Logged_Username.equals(cn_Seller_ID)){
            holder.convolayout_Supplier_Name.setText(cn_Buyer_Fullname);
            holder.convolayout_First_Message.setText(cn_First_Message);

            try {
                String timeStamp = cn_Convo_Updated;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                String month = temp1.format(dateFormat.parse(timeStamp));

                holder.convolayout_Date_Updated.setText(month);
            } catch (ParseException e) {
                e.printStackTrace();

            }


            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" + cn_Buyer_ID + "_profile.png")
                    .circleCrop()
                    .into(holder.convolayout_Profile_Photo);


        }else{

            holder.convolayout_Supplier_Name.setText(cn_Seller_Fullname);
            holder.convolayout_First_Message.setText(cn_First_Message);

            try {
                String timeStamp = cn_Convo_Updated;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                String month = temp1.format(dateFormat.parse(timeStamp));

                holder.convolayout_Date_Updated.setText(month);
            } catch (ParseException e) {
                e.printStackTrace();

            }


            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" + cn_Seller_ID + "_profile.png")
                    .circleCrop()
                    .into(holder.convolayout_Profile_Photo);


        }




    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class convo_viewholder extends RecyclerView.ViewHolder {

        public ImageView convolayout_Profile_Photo;
        public TextView convolayout_Supplier_Name;
        public TextView convolayout_First_Message;
        public TextView convolayout_Date_Updated;



        public convo_viewholder(@NonNull View itemView) {
            super(itemView);

            convolayout_Profile_Photo = itemView.findViewById(R.id.convo_itemlist_img_Profile_Photo);
            convolayout_Supplier_Name = itemView.findViewById(R.id.convo_itemlist_txt_Supplier_Name);
            convolayout_First_Message = itemView.findViewById(R.id.convo_itemlist_txt_First_Message);
            convolayout_Date_Updated = itemView.findViewById(R.id.convo_itemlist_txt_Convo_Date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mListener.onItemClick(getAdapterPosition());



                }
            });


        }
    }



}