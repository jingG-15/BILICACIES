package com.holanda.bilicacies.Adapters;

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

public class cart_Recycler_Adapter extends RecyclerView.Adapter<cart_Recycler_Adapter.cart_viewholder> {
    private Context mContext;
    private ArrayList<cart_variable_links> mFBList;

    private cart_Recycler_Adapter.OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position, String Type_Clicked, String User_Assigned, Boolean itemMarked);

    }

    public cart_Recycler_Adapter(Context context, ArrayList<cart_variable_links> sampleList, cart_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public cart_Recycler_Adapter.cart_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_cartview_itemlist_layout, parent, false);

        return new cart_Recycler_Adapter.cart_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull cart_Recycler_Adapter.cart_viewholder holder, int position) {
        cart_variable_links currentItem = mFBList.get(position);

        String c_Type_to_Display = currentItem.c_get_Type_to_Display();
        String c_Seller_Name = currentItem.c_get_Supplier_Name();
        String c_Product_Name = currentItem.c_get_Product_Name();
        String c_Price = currentItem.c_get_Price();
        String c_Quantity = currentItem.c_get_Quantity();
        String c_Product_ID = currentItem.c_get_Product_ID();


        if(c_Type_to_Display.equals("Supplier_Name")){

            holder.cartlayout_Supplier_Name_Container.setVisibility(View.VISIBLE);
            holder.cartlayout_Product_Details_Container.setVisibility(View.GONE);

            holder.cartlayout_chk_Suppplier_Name.setText(c_Seller_Name);

        }else{

            holder.cartlayout_Supplier_Name_Container.setVisibility(View.GONE);
            holder.cartlayout_Product_Details_Container.setVisibility(View.VISIBLE);

            holder.cartlayout_txt_Product_Name.setText(c_Product_Name);

            DecimalFormat decim = new DecimalFormat("#,###.##");


            holder.cartlayout_txt_Price.setText(decim.format(Integer.parseInt(c_Price)));


            holder.cartlayout_txtin_Quantity.setText(c_Quantity);

            int px1 = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20, mContext.getResources().getDisplayMetrics()
            );

            Glide.with(mContext)
                    .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + c_Product_ID + "_1.png")
                    .transform(new RoundedCorners(px1))
                    .into(holder.cartlayout_img_Product_Thumbnail);

        }

    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class cart_viewholder extends RecyclerView.ViewHolder {

        public ConstraintLayout cartlayout_Supplier_Name_Container;
        public ConstraintLayout cartlayout_Product_Details_Container;
        public CheckBox cartlayout_chk_Suppplier_Name;
        public ImageView cartlayout_img_Product_Thumbnail;
        public TextView cartlayout_txt_Product_Name;
        public TextView cartlayout_txt_Price;
        public EditText cartlayout_txtin_Quantity;
        public ImageView cartlayout_img_Quan_Minus;
        public ImageView cartlayout_img_Quan_Add;
        public ImageView cartlayout_img_Delete_from_Cart;
        public CheckBox cartlayout_chk_Item_Marker;
        public Button cartlayout_btn_Apply_Changes;


        public cart_viewholder(@NonNull View itemView) {
            super(itemView);

            cartlayout_Supplier_Name_Container = itemView.findViewById(R.id.cartview_itemlist_const_header_container);
            cartlayout_Product_Details_Container = itemView.findViewById(R.id.cartview_itemlist_const_product_container);
            cartlayout_chk_Suppplier_Name = itemView.findViewById(R.id.cartview_itemlist_chkbx_Supplier_Name);
            cartlayout_img_Product_Thumbnail = itemView.findViewById(R.id.cartview_itemlist_img_Product_Thumbnail);
            cartlayout_txt_Product_Name = itemView.findViewById(R.id.cartview_itemlist_txt_Product_Name);
            cartlayout_txt_Price = itemView.findViewById(R.id.cartview_itemlist_txt_Price);
            cartlayout_txtin_Quantity = itemView.findViewById(R.id.cartview_itemlist_txtin_Quantity);
            cartlayout_img_Quan_Minus = itemView.findViewById(R.id.cartview_itemlist_img_Quantity_Minus);
            cartlayout_img_Quan_Add = itemView.findViewById(R.id.cartview_itemlist_img_Quantity_Add);
            cartlayout_img_Delete_from_Cart = itemView.findViewById(R.id.cartview_itemlist_img_Delete_from_Cart);
            cartlayout_chk_Item_Marker = itemView.findViewById(R.id.cartview_itemlist_chkbx_item_mark);
            cartlayout_btn_Apply_Changes = itemView.findViewById(R.id.cartview_itemlist_btn_Apply_Qty_Changes);

            cartlayout_img_Product_Thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());
                    mListener.onItemClick(getAdapterPosition(), currentItem.c_get_Type_to_Display(),  currentItem.c_get_Supplier_Username(), false);


                }
            });

            cartlayout_Supplier_Name_Container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());
                    mListener.onItemClick(getAdapterPosition(), currentItem.c_get_Type_to_Display(),  currentItem.c_get_Supplier_Username(), false);


                }
            });

            cartlayout_txt_Product_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());
                    mListener.onItemClick(getAdapterPosition(), currentItem.c_get_Type_to_Display(),  currentItem.c_get_Supplier_Username(), false);

                }
            });

            cartlayout_img_Quan_Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());

                    int cur_quantity = Integer.parseInt(currentItem.c_get_Quantity());

                    cur_quantity++;
                    cartlayout_txtin_Quantity.setText(String.valueOf(cur_quantity));

                    mListener.onItemClick(getAdapterPosition(), "Cart_Add",  String.valueOf(cur_quantity), cartlayout_chk_Item_Marker.isChecked());

                    cartlayout_btn_Apply_Changes.setVisibility(View.VISIBLE);

                }
            });

            cartlayout_img_Quan_Minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());

                    int cur_quantity = Integer.parseInt(currentItem.c_get_Quantity());

                    if(cur_quantity > 1){
                        cur_quantity--;
                        cartlayout_txtin_Quantity.setText(String.valueOf(cur_quantity));

                    }

                    mListener.onItemClick(getAdapterPosition(), "Cart_Minus",  String.valueOf(cur_quantity), cartlayout_chk_Item_Marker.isChecked());

                    cartlayout_btn_Apply_Changes.setVisibility(View.VISIBLE);
                }
            });

            cartlayout_img_Delete_from_Cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());
                    mListener.onItemClick(getAdapterPosition(), "Delete_Item_in_Cart",  currentItem.c_get_Product_ID(), cartlayout_chk_Item_Marker.isChecked());
                }
            });




            cartlayout_chk_Item_Marker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());
                    if(isChecked){
                        mListener.onItemClick(getAdapterPosition(), "Marked",  currentItem.c_get_Price(), cartlayout_chk_Item_Marker.isChecked());

                    }else{
                        mListener.onItemClick(getAdapterPosition(), "Un-Marked",  currentItem.c_get_Price(), cartlayout_chk_Item_Marker.isChecked());

                    }



                }
            });


            cartlayout_txtin_Quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    switch (actionId){
                        case EditorInfo.IME_ACTION_DONE:
                        case EditorInfo.IME_ACTION_NEXT:
                        case EditorInfo.IME_ACTION_PREVIOUS:
                            mListener.onItemClick(getAdapterPosition(), "Quantity_Manual_Change",  cartlayout_txtin_Quantity.getText().toString().trim(), cartlayout_chk_Item_Marker.isChecked());

                            cartlayout_btn_Apply_Changes.setVisibility(View.VISIBLE);

                            return true;
                    }
                    return false;

                }
            });


            cartlayout_chk_Suppplier_Name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Supplier_Chk_Tappped",  currentItem.c_get_Supplier_Username(), isChecked);

                }
            });

            cartlayout_btn_Apply_Changes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cart_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Update_Quantity_Apply",  currentItem.c_get_Product_ID(), cartlayout_chk_Item_Marker.isChecked());

                }
            });




        }
    }



}