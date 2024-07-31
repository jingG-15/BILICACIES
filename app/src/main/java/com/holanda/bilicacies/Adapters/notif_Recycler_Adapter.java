package com.holanda.bilicacies.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.holanda.bilicacies.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class notif_Recycler_Adapter extends RecyclerView.Adapter<notif_Recycler_Adapter.notif_viewholder> {
    private Context mContext;
    private ArrayList<notif_variable_links> mFBList;

    private notif_Recycler_Adapter.OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position, String Type_Clicked, String Univ_Variable_1, String Univ_Variable_2, String Univ_Variable_3);

    }

    public notif_Recycler_Adapter(Context context, ArrayList<notif_variable_links> sampleList, notif_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public notif_Recycler_Adapter.notif_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_notifications_itemlist_layout, parent, false);

        return new notif_Recycler_Adapter.notif_viewholder(v);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull notif_Recycler_Adapter.notif_viewholder holder, int position) {
        notif_variable_links currentItem = mFBList.get(position);

        String n_Notif_Type = currentItem.getNt_Notif_Type();
        String n_Notif_Content = currentItem.getNt_Notif_Contents();
        String n_Notif_Data_2 = currentItem.getNt_Notif_Data_2();
        String n_Notif_Data_3 = currentItem.getNt_Notif_Data_3();
        String n_Notif_Date = currentItem.getNt_Notif_date();


        switch (n_Notif_Type) {
            case "Order_Filed":
                holder.notiflayout_img_Type_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_shopping_basket_24));
                holder.notiflayout_txt_Header.setText("An order has been filed.");

                break;
            case "New_Message":
                holder.notiflayout_img_Type_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_sms_24));
                holder.notiflayout_txt_Header.setText("New message received from " + n_Notif_Data_3 + ".");

                break;
            case "Order_Approved":
                holder.notiflayout_img_Type_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_local_shipping_24));
                holder.notiflayout_txt_Header.setText("Order # " + n_Notif_Data_2 + " has been approved.");

                break;
            case "Order_Delivered":
                holder.notiflayout_img_Type_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_check_circle_24));
                holder.notiflayout_txt_Header.setText("Order # " + n_Notif_Data_2 + " has been delivered.");

                break;
            case "Order_Denial":
                holder.notiflayout_img_Type_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_cancel_24));
                holder.notiflayout_txt_Header.setText("Order # " + n_Notif_Data_2 + " has been denied.");

                break;
            case "Order_Failed":
                holder.notiflayout_img_Type_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_cancel_24));
                holder.notiflayout_txt_Header.setText("Order # " + n_Notif_Data_2 + " failed to be delivered.");

                break;
        }

        holder.notiflayout_txt_Body.setText(n_Notif_Content);
        holder.notiflayout_txt_date.setText(n_Notif_Date);


    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class notif_viewholder extends RecyclerView.ViewHolder {

        public ImageView notiflayout_img_Type_Icon;
        public TextView notiflayout_txt_Header;
        public TextView notiflayout_txt_Body;
        public TextView notiflayout_txt_date;


        public notif_viewholder(@NonNull View itemView) {
            super(itemView);

            notiflayout_img_Type_Icon = itemView.findViewById(R.id.notif_itemlist_img_Type_Icon);
            notiflayout_txt_Header = itemView.findViewById(R.id.notif_itemlist_txt_Headline);
            notiflayout_txt_Body = itemView.findViewById(R.id.notif_itemlist_txt_Body);
            notiflayout_txt_date = itemView.findViewById(R.id.notif_itemlist_txt_Notif_date);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notif_variable_links currentItem = mFBList.get(getAdapterPosition());

                    String n_Notif_Type = currentItem.getNt_Notif_Type();
                    String n_Notif_Data_1 = currentItem.getNt_Notif_Data_1();
                    String n_Notif_Data_2 = currentItem.getNt_Notif_Data_2();
                    String n_Notif_Data_3 = currentItem.getNt_Notif_Data_3();

                    mListener.onItemClick(getAdapterPosition(),n_Notif_Type, n_Notif_Data_1, n_Notif_Data_2, n_Notif_Data_3 );
                }
            });



        }
    }
}
