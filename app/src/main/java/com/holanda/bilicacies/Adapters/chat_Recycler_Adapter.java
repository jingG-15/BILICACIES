package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.holanda.bilicacies.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class chat_Recycler_Adapter extends RecyclerView.Adapter<chat_Recycler_Adapter.chat_viewholder> {
    private Context mContext;
    private ArrayList<chat_variable_links> mFBList;

    private chat_Recycler_Adapter.OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position, String Type_Clicked, String Univ_Var);

    }

    public chat_Recycler_Adapter(Context context, ArrayList<chat_variable_links> sampleList, chat_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public chat_Recycler_Adapter.chat_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_chat_itemlist_layout, parent, false);

        return new chat_Recycler_Adapter.chat_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull chat_Recycler_Adapter.chat_viewholder holder, int position) {
        chat_variable_links currentItem = mFBList.get(position);

        String ch_Chat_ID = currentItem.ch_get_Chat_ID();
        String ch_Message_Type = currentItem.ch_get_Message_Type();
        String ch_Message_Content = currentItem.ch_get_Message_Content();
        String ch_Date_Sent = currentItem.ch_get_Date_Sent();
        String ch_Convo_ID = currentItem.ch_get_Convo_ID();
        String ch_Product_ID = currentItem.ch_get_Prod_ID();
        String ch_Product_Name = currentItem.ch_get_Product_Name();
        String ch_Product_Price = currentItem.ch_get_Product_Price();
        String ch_Prod_Date_Added = currentItem.ch_get_Prod_Date_Added();
        String ch_Seller_ID = currentItem.ch_get_Seller_ID();
        String ch_Buyer_ID = currentItem.ch_get_Buyer_ID();
        String ch_Logged_Username = currentItem.ch_get_Logged_ID();
        int px1 = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20, mContext.getResources().getDisplayMetrics()
        );


        if (ch_Message_Type.equals("Sender_Order_Link")) {
            holder.ch_const_receiver_message_string.setVisibility(View.GONE);
            holder.ch_const_sender_message_string.setVisibility(View.GONE);

            holder.ch_const_receiver_message_photo.setVisibility(View.GONE);
            holder.ch_const_sender_message_photo.setVisibility(View.GONE);

            holder.ch_const_receiver_Order_Link.setVisibility(View.GONE);
            holder.ch_const_sender_Order_Link.setVisibility(View.VISIBLE);

            DecimalFormat decim = new DecimalFormat("#,###.##");

            holder.ch_ordlnk_sender_txt_Product_Name.setText(ch_Product_Name);
            holder.ch_ordlnk_sender_txt_Price.setText(decim.format(Integer.parseInt(ch_Product_Price)));
            holder.ch_ordlnk_sender_txt_Date_Added.setText(ch_Prod_Date_Added);

            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/product_photos/" + ch_Product_ID + "_1.png")
                    .into(holder.ch_ordlnk_sender_img_Product_Cover);


            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Logged_Username + "_profile.png")
                    .circleCrop()
                    .into(holder.ch_ordlnk_sender_img_Profile_Photo);


        } else if (ch_Message_Type.equals("Receiver_Order_Link")) {

            holder.ch_const_receiver_message_string.setVisibility(View.GONE);
            holder.ch_const_sender_message_string.setVisibility(View.GONE);

            holder.ch_const_receiver_message_photo.setVisibility(View.GONE);
            holder.ch_const_sender_message_photo.setVisibility(View.GONE);

            holder.ch_const_receiver_Order_Link.setVisibility(View.VISIBLE);
            holder.ch_const_sender_Order_Link.setVisibility(View.GONE);

            DecimalFormat decim = new DecimalFormat("#,###.##");

            holder.ch_ordlnk_receiver_txt_Product_Name.setText(ch_Product_Name);
            holder.ch_ordlnk_receiver_txt_Price.setText(decim.format(Integer.parseInt(ch_Product_Price)));
            holder.ch_ordlnk_receiver_txt_Date_Added.setText(ch_Prod_Date_Added);

            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/product_photos/" + ch_Product_ID + "_1.png")
                    .transform(new RoundedCorners(px1))
                    .into(holder.ch_ordlnk_receiver_img_Product_Cover);

            if(ch_Logged_Username.equals(ch_Seller_ID)){

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Buyer_ID + "_profile.png")
                        .circleCrop()
                        .into(holder.ch_ordlnk_receiver_img_Profile_Photo);
            }else{
                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Seller_ID + "_profile.png")
                        .circleCrop()
                        .into(holder.ch_ordlnk_receiver_img_Profile_Photo);

            }


        } else if (ch_Message_Type.equals("Sender_Message_String")) {

            holder.ch_const_receiver_message_string.setVisibility(View.GONE);
            holder.ch_const_sender_message_string.setVisibility(View.VISIBLE);

            holder.ch_const_receiver_message_photo.setVisibility(View.GONE);
            holder.ch_const_sender_message_photo.setVisibility(View.GONE);

            holder.ch_const_receiver_Order_Link.setVisibility(View.GONE);
            holder.ch_const_sender_Order_Link.setVisibility(View.GONE);

//            if(ch_Seller_ID.equals("0")){
//
//                holder.ch_msgstr_sender_Progress_bar.setVisibility(View.VISIBLE);
//
//            }else{
//
//                holder.ch_msgstr_sender_Progress_bar.setVisibility(View.INVISIBLE);
//
//            }

            holder.ch_msgstr_sender_txt_Message.setText(ch_Message_Content);
            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Logged_Username + "_profile.png")
                    .circleCrop()
                    .into(holder.ch_msgstr_sender_img_Profile_Photo);


        } else if (ch_Message_Type.equals("Receiver_Message_String")) {

            holder.ch_const_receiver_message_string.setVisibility(View.VISIBLE);
            holder.ch_const_sender_message_string.setVisibility(View.GONE);

            holder.ch_const_receiver_message_photo.setVisibility(View.GONE);
            holder.ch_const_sender_message_photo.setVisibility(View.GONE);

            holder.ch_const_receiver_Order_Link.setVisibility(View.GONE);
            holder.ch_const_sender_Order_Link.setVisibility(View.GONE);


            holder.ch_msgstr_receiver_txt_Message.setText(ch_Message_Content);

            if(ch_Logged_Username.equals(ch_Seller_ID)){
                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Buyer_ID + "_profile.png")
                        .circleCrop()
                        .into(holder.ch_msgstr_receiver_img_Profile_Photo);

            }else{
                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Seller_ID + "_profile.png")
                        .circleCrop()
                        .into(holder.ch_msgstr_receiver_img_Profile_Photo);

            }


        } else if (ch_Message_Type.equals("Sender_Attached_Image")) {

            holder.ch_const_receiver_message_string.setVisibility(View.GONE);
            holder.ch_const_sender_message_string.setVisibility(View.GONE);

            holder.ch_const_receiver_message_photo.setVisibility(View.GONE);
            holder.ch_const_sender_message_photo.setVisibility(View.VISIBLE);

            holder.ch_const_receiver_Order_Link.setVisibility(View.GONE);
            holder.ch_const_sender_Order_Link.setVisibility(View.GONE);


//            if(ch_Seller_ID.equals("0")){
//
//                holder.ch_msgphoto_sender_Progress_bar.setVisibility(View.VISIBLE);
//
//            }else{
//
//                holder.ch_msgphoto_sender_Progress_bar.setVisibility(View.INVISIBLE);
//
//            }




            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/convo_attachments/" + ch_Message_Content + ".png")
                    .into(holder.ch_msgphoto_sender_img_Content);

            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Logged_Username + "_profile.png")
                    .circleCrop()
                    .into(holder.ch_msgphoto_sender_img_Profile_Photo);


        } else if (ch_Message_Type.equals("Receiver_Attached_Image")) {


            holder.ch_const_receiver_message_string.setVisibility(View.GONE);
            holder.ch_const_sender_message_string.setVisibility(View.GONE);

            holder.ch_const_receiver_message_photo.setVisibility(View.VISIBLE);
            holder.ch_const_sender_message_photo.setVisibility(View.GONE);

            holder.ch_const_receiver_Order_Link.setVisibility(View.GONE);
            holder.ch_const_sender_Order_Link.setVisibility(View.GONE);

            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/convo_attachments/" + ch_Message_Content + ".png")
                    .into(holder.ch_msgphoto_receiver_img_Content);

            if(ch_Logged_Username.equals(ch_Seller_ID)){

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Buyer_ID + "_profile.png")
                        .circleCrop()
                        .into(holder.ch_msgphoto_receiver_img_Profile_Photo);
            }else{

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + ch_Seller_ID + "_profile.png")
                        .circleCrop()
                        .into(holder.ch_msgphoto_receiver_img_Profile_Photo);
            }


        }

    }


    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class chat_viewholder extends RecyclerView.ViewHolder {

        public ConstraintLayout ch_const_receiver_message_string;
        public ConstraintLayout ch_const_sender_message_string;
        public ImageView ch_msgstr_receiver_img_Profile_Photo;
        public ImageView ch_msgstr_sender_img_Profile_Photo;
        public TextView ch_msgstr_receiver_txt_Message;
        public TextView ch_msgstr_sender_txt_Message;
        public ProgressBar ch_msgstr_sender_Progress_bar;


        public ConstraintLayout ch_const_receiver_message_photo;
        public ConstraintLayout ch_const_sender_message_photo;
        public ImageView ch_msgphoto_receiver_img_Profile_Photo;
        public ImageView ch_msgphoto_sender_img_Profile_Photo;
        public ImageView ch_msgphoto_receiver_img_Content;
        public ImageView ch_msgphoto_sender_img_Content;
        public ProgressBar ch_msgphoto_sender_Progress_bar;


        public ConstraintLayout ch_const_receiver_Order_Link;
        public ConstraintLayout ch_const_sender_Order_Link;
        public ImageView ch_ordlnk_receiver_img_Product_Cover;
        public ImageView ch_ordlnk_sender_img_Product_Cover;
        public ImageView ch_ordlnk_receiver_img_Profile_Photo;
        public ImageView ch_ordlnk_sender_img_Profile_Photo;
        public TextView ch_ordlnk_receiver_txt_Product_Name;
        public TextView ch_ordlnk_sender_txt_Product_Name;
        public TextView ch_ordlnk_receiver_txt_Price;
        public TextView ch_ordlnk_sender_txt_Price;
        public TextView ch_ordlnk_receiver_txt_Date_Added;
        public TextView ch_ordlnk_sender_txt_Date_Added;


        public chat_viewholder(@NonNull View itemView) {
            super(itemView);


            ch_const_receiver_message_string = itemView.findViewById(R.id.chatitem_receiver_message_string_layout);
            ch_const_sender_message_string = itemView.findViewById(R.id.chatitem_sender_message_string_layout);
            ch_msgstr_receiver_img_Profile_Photo = itemView.findViewById(R.id.chatitem_receiver_str_img_Profile_Photo);
            ch_msgstr_sender_img_Profile_Photo = itemView.findViewById(R.id.chatitem_sender_str_img_Profile_Photo);
            ch_msgstr_receiver_txt_Message = itemView.findViewById(R.id.chatitem_receiver_txt_Message);
            ch_msgstr_sender_txt_Message = itemView.findViewById(R.id.chatitem_sender_txt_Message);
            ch_msgstr_sender_Progress_bar = itemView.findViewById(R.id.chatitem_sender_progbar_msgstr_Sending);

            ch_const_receiver_message_photo = itemView.findViewById(R.id.chatitem_receiver_message_photo_layout);
            ch_const_sender_message_photo = itemView.findViewById(R.id.chatitem_sender_message_photo_layout);
            ch_msgphoto_receiver_img_Profile_Photo = itemView.findViewById(R.id.chatitem_receiver_image_img_Profile_Photo);
            ch_msgphoto_sender_img_Profile_Photo = itemView.findViewById(R.id.chatitem_sender_image_img_Profile_Photo);
            ch_msgphoto_receiver_img_Content = itemView.findViewById(R.id.chatitem_receiver_image_img_Content);
            ch_msgphoto_sender_img_Content = itemView.findViewById(R.id.chatitem_sender_image_img_Content);
            ch_msgphoto_sender_Progress_bar = itemView.findViewById(R.id.chatitem_sender_progbar_msgimg_Sending);

            ch_const_receiver_Order_Link = itemView.findViewById(R.id.chatitem_receiver_Order_ID_Layout);
            ch_const_sender_Order_Link = itemView.findViewById(R.id.chatitem_sender_Order_ID_Layout);
            ch_ordlnk_receiver_img_Product_Cover = itemView.findViewById(R.id.chatitem_receiver_OrderItem_img_Product_Photo);
            ch_ordlnk_sender_img_Product_Cover = itemView.findViewById(R.id.chatitem_sender_OrderItem_img_Product_Photo);
            ch_ordlnk_receiver_txt_Product_Name = itemView.findViewById(R.id.chatitem_receiver_OrderItem_txt_Product_Name);
            ch_ordlnk_sender_txt_Product_Name = itemView.findViewById(R.id.chatitem_sender_OrderItem_txt_Product_Name);
            ch_ordlnk_receiver_txt_Price = itemView.findViewById(R.id.chatitem_receiver_OrderItem_txt_Price);
            ch_ordlnk_sender_txt_Price = itemView.findViewById(R.id.chatitem_sender_OrderItem_txt_Price);
            ch_ordlnk_receiver_txt_Date_Added = itemView.findViewById(R.id.chatitem_receiver_OrderItem_txt_Date_Added);
            ch_ordlnk_sender_txt_Date_Added = itemView.findViewById(R.id.chatitem_sender_OrderItem_txt_Date_Added);
            ch_ordlnk_receiver_img_Profile_Photo = itemView.findViewById(R.id.chatitem_receiver_OrderItem_img_Profile_Photo);
            ch_ordlnk_sender_img_Profile_Photo = itemView.findViewById(R.id.chatitem_sender_OrderItem_img_Profile_Photo);


            ch_const_receiver_message_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chat_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Image_Message", "https://" +
                            mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/convo_attachments/" +
                            currentItem.ch_get_Message_Content() + ".png");



                }
            });

            ch_const_sender_message_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    chat_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Image_Message", "https://" +
                            mContext.getString(R.string.Server_Web_Host_IP) + "BILICACIES/convo_attachments/" +
                            currentItem.ch_get_Message_Content() + ".png");

                }
            });

            ch_const_receiver_Order_Link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    chat_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Order_Link", currentItem.ch_get_Convo_ID());

                }
            });

            ch_const_sender_Order_Link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    chat_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Order_Link", currentItem.ch_get_Convo_ID());


                }
            });







        }


    }
}


