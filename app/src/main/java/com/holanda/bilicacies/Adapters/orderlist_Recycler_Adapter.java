package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.holanda.bilicacies.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class orderlist_Recycler_Adapter extends RecyclerView.Adapter<orderlist_Recycler_Adapter.orderlist_viewholder> {
    private Context mContext;
    private ArrayList<orderlist_variable_links> mFBList;

    private orderlist_Recycler_Adapter.OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public orderlist_Recycler_Adapter(Context context, ArrayList<orderlist_variable_links> sampleList, orderlist_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public orderlist_Recycler_Adapter.orderlist_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_orderlist_itemlist_layout, parent, false);

        return new orderlist_Recycler_Adapter.orderlist_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull orderlist_Recycler_Adapter.orderlist_viewholder holder, int position) {
        orderlist_variable_links currentItem = mFBList.get(position);

        String O_ID = currentItem.getO_ID();
        String O_Order_ID = currentItem.getO_Order_ID();
        String O_Seller_ID = currentItem.getO_Seller_ID();
        String O_Buyer_ID = currentItem.getO_Buyer_ID();
        String O_Order_Status = currentItem.getO_Order_Status();
        String O_Order_Type = currentItem.getO_Order_Type();
        String O_Order_Created = currentItem.getO_Order_Created();
        String O_Seller_Fullname = currentItem.getO_Seller_Fullname();
        String O_First_Product_ID = currentItem.getO_First_Product_ID();
        String O_First_Product_Name = currentItem.getO_First_Product_Name();
        String O_First_Product_Quantity = currentItem.getO_First_Product_Quantity();
        String O_First_Product_Price = currentItem.getO_First_Product_Price();
        String O_Total_Product_Order_Count = currentItem.getO_Total_Product_Order_Count();
        String O_Total_Payables = currentItem.getO_Total_Payables();


        holder.orlst_txt_Supplier_Name.setText(O_Seller_Fullname);
        holder.orlst_txt_First_Product_Name.setText(O_First_Product_Name);
        holder.orlst_txt_Quantity_per_Item.setText("Quantity: x" + O_First_Product_Quantity);

        DecimalFormat decim = new DecimalFormat("#,###.##");


        holder.orlst_txt_First_Product_Price.setText( "Price: ₱ " + decim.format(Integer.parseInt(O_First_Product_Price)));


       holder.orlst_txt_Total_Items_Count.setText("Total items: " + O_Total_Product_Order_Count);

        int px1 = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20, mContext.getResources().getDisplayMetrics()
        );

        Glide.with(mContext)
                .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + O_First_Product_ID + "_1.png")
                .transform(new RoundedCorners(px1))
                .into(holder.orlst_img_First_Product_Cover);

        holder.orlst_txt_Total_Payment.setText("Order Total: ₱ " + decim.format(Integer.parseInt(O_Total_Payables)));

    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class orderlist_viewholder extends RecyclerView.ViewHolder {

        public TextView orlst_txt_Supplier_Name;
        public TextView orlst_txt_First_Product_Name;
        public ImageView orlst_img_First_Product_Cover;
        public TextView orlst_txt_Quantity_per_Item;
        public TextView orlst_txt_First_Product_Price;
        public TextView orlst_txt_Total_Items_Count;
        public TextView orlst_txt_Total_Payment;


        public orderlist_viewholder(@NonNull View itemView) {
            super(itemView);

            orlst_txt_Supplier_Name = itemView.findViewById(R.id.ordlstitems_txt_Supplier_Name);
            orlst_txt_First_Product_Name = itemView.findViewById(R.id.ordlstitems_txt_Product_Name);
            orlst_img_First_Product_Cover = itemView.findViewById(R.id.ordlstitems_img_Product_Cover);
            orlst_txt_Quantity_per_Item = itemView.findViewById(R.id.ordlstitems_txt_Quantity);
            orlst_txt_First_Product_Price = itemView.findViewById(R.id.ordlstitems_txt_Price_per_Unit);
            orlst_txt_Total_Items_Count = itemView.findViewById(R.id.ordlstitems_txt_Product_Count_per_Order);
            orlst_txt_Total_Payment = itemView.findViewById(R.id.ordlstitems_txt_Total_Payment);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListener.onItemClick(getAdapterPosition());



                }
            });

        }
    }



}