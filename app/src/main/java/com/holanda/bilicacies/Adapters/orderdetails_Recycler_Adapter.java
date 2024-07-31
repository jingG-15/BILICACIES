package com.holanda.bilicacies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.holanda.bilicacies.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class orderdetails_Recycler_Adapter extends RecyclerView.Adapter<orderdetails_Recycler_Adapter.orderdetails_viewholder> {
    private Context mContext;
    private ArrayList<orderdetails_variable_links> mFBList;

    private orderdetails_Recycler_Adapter.OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position, String type_clicked, String Univ_Variable);

    }

    public orderdetails_Recycler_Adapter(Context context, ArrayList<orderdetails_variable_links> sampleList, orderdetails_Recycler_Adapter.OnItemClickListener mListener) {
        mContext = context;
        mFBList = sampleList;


        this.mListener = mListener;

    }

    @NonNull
    @Override
    public orderdetails_Recycler_Adapter.orderdetails_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rec_item_orderdetails_itemlist_layout, parent, false);

        return new orderdetails_Recycler_Adapter.orderdetails_viewholder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull orderdetails_Recycler_Adapter.orderdetails_viewholder holder, int position) {
        orderdetails_variable_links currentItem = mFBList.get(position);

        String OD_ID = currentItem.getOD_ID();
        String OD_Order_ID = currentItem.getOD_Order_ID();
        String OD_Product_ID = currentItem.getOD_Product_ID();
        String OD_Product_Quantity = currentItem.getOD_Product_Quantity();
        String OD_Product_Price = currentItem.getOD_Product_Price();
        String OD_Seller_ID = currentItem.getOD_Seller_ID();
        String OD_Seller_Fullname = currentItem.getOD_Seller_Fullname();
        String OD_Product_Name = currentItem.getOD_Product_Name();
        String OD_Total_Payables = currentItem.getOD_Total_Payables();
        String OD_Order_Type = currentItem.getOD_Order_Type();
        String OD_Order_Status = currentItem.getOD_Order_Status();
        Boolean OD_product_only = currentItem.getOD_product_Only();
        Boolean OD_products_Done = currentItem.getOD_products_Done();
        String OD_Order_Created = currentItem.getOD_Order_Created();
        String OD_Delivery_Fee = currentItem.getOD_Delivery_Fee();
        String OD_Delivery_Date = currentItem.getOD_Delivery_Date();
        String OD_Buyer_Fullname = currentItem.getOD_Buyer_Fullname();
        String OD_Buyer_Contact_Number = currentItem.getOD_Buyer_Contact_Number();
        String OD_Complete_Address = currentItem.getOD_Complete_Address();
        String OD_Remarks = currentItem.getOD_Remarks();

        DecimalFormat decim = new DecimalFormat("#,###.##");

        if(OD_Order_Status.equals("Negotiation")){

            if(!OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                holder.ordprop_txt_Status_Headline.setText("Awaiting seller's confirmation.");
                holder.ordprop_txt_Status_Body.setText("Waiting for the seller's approval of your ordered items. Please wait or contact seller for more information.");
                holder.ordprop_img_Status_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_warning_24));

                holder.ordprop_txt_Seller_Name.setText(OD_Seller_Fullname);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);


                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));



            }else if(OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);
                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));

            }else if(OD_product_only && OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.GONE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.VISIBLE);

                holder.ordprop_txt_Merch_Subtotal.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));
                holder.ordprop_txt_Delivery_Fee.setText("----");
                holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));

                holder.ordprop_txt_Order_ID.setText(OD_Order_ID);


                try {
                    String timeStamp = OD_Order_Created;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                    SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                    String month = temp1.format(dateFormat.parse(timeStamp));

                    holder.ordprop_txt_Order_Creation_Date.setText(month);
                } catch (ParseException e) {
                    holder.ordprop_txt_Order_Creation_Date.setText(OD_Order_Created);
                    e.printStackTrace();

                }



            }

        }else if(OD_Order_Status.equals("To_Send")){

            if(!OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Shop_Header.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                holder.ordprop_txt_Status_Headline.setText("Seller has approved the order.");
                holder.ordprop_txt_Status_Body.setText("Expect delivery on " + OD_Delivery_Date + " along with the address you provided.");
                holder.ordprop_img_Status_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_local_shipping_24));

                holder.ordprop_txt_Date_of_Delivery.setText(OD_Delivery_Date);
                holder.ordprop_txt_Recepient_Name.setText(OD_Buyer_Fullname);
                holder.ordprop_txt_Recepient_Contact_Number.setText(OD_Buyer_Contact_Number);
                holder.ordprop_txt_Complete_Address.setText(OD_Complete_Address);

                holder.ordprop_txt_Seller_Name.setText(OD_Seller_Fullname);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);


                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));



            }else if(OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);
                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));

            }else if(OD_product_only && OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.GONE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.VISIBLE);

                holder.ordprop_txt_Merch_Subtotal.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));
                if(!OD_Delivery_Fee.equals("_")){

                    holder.ordprop_txt_Delivery_Fee.setText("₱ " + decim.format(Integer.parseInt(OD_Delivery_Fee)));
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables) + Integer.parseInt(OD_Delivery_Fee)));


                }else{

                    holder.ordprop_txt_Delivery_Fee.setText("----");
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));

                }


                holder.ordprop_txt_Order_ID.setText(OD_Order_ID);


                try {
                    String timeStamp = OD_Order_Created;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                    SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                    String month = temp1.format(dateFormat.parse(timeStamp));

                    holder.ordprop_txt_Order_Creation_Date.setText(month);
                } catch (ParseException e) {
                    holder.ordprop_txt_Order_Creation_Date.setText(OD_Order_Created);
                    e.printStackTrace();

                }



            }






        }else if(OD_Order_Status.equals("To_Receive")){

            if(!OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Shop_Header.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                holder.ordprop_txt_Status_Headline.setText("Seller has approved the order.");
                holder.ordprop_txt_Status_Body.setText("Expect delivery on " + OD_Delivery_Date + " along with the address you provided.");
                holder.ordprop_img_Status_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_local_shipping_24));

                holder.ordprop_txt_Date_of_Delivery.setText(OD_Delivery_Date);
                holder.ordprop_txt_Recepient_Name.setText(OD_Buyer_Fullname);
                holder.ordprop_txt_Recepient_Contact_Number.setText(OD_Buyer_Contact_Number);
                holder.ordprop_txt_Complete_Address.setText(OD_Complete_Address);

                holder.ordprop_txt_Seller_Name.setText(OD_Seller_Fullname);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);


                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));



            }else if(OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);
                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));

            }else if(OD_product_only && OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.GONE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.VISIBLE);

                holder.ordprop_txt_Merch_Subtotal.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));
                if(!OD_Delivery_Fee.equals("_")){

                    holder.ordprop_txt_Delivery_Fee.setText("₱ " + decim.format(Integer.parseInt(OD_Delivery_Fee)));
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables) + Integer.parseInt(OD_Delivery_Fee)));


                }else{

                    holder.ordprop_txt_Delivery_Fee.setText("----");
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));

                }


                holder.ordprop_txt_Order_ID.setText(OD_Order_ID);


                try {
                    String timeStamp = OD_Order_Created;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                    SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                    String month = temp1.format(dateFormat.parse(timeStamp));

                    holder.ordprop_txt_Order_Creation_Date.setText(month);
                } catch (ParseException e) {
                    holder.ordprop_txt_Order_Creation_Date.setText(OD_Order_Created);
                    e.printStackTrace();

                }



            }


        }else if(OD_Order_Status.equals("Completed-Denied")){

            if(!OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Shop_Header.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                holder.ordprop_txt_Status_Headline.setText("Seller has denied your order for the following reason:");
                holder.ordprop_txt_Status_Body.setText(OD_Remarks);
                holder.ordprop_img_Status_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_local_shipping_24));

                holder.ordprop_txt_Date_of_Delivery.setText(OD_Delivery_Date);
                holder.ordprop_txt_Recepient_Name.setText(OD_Buyer_Fullname);
                holder.ordprop_txt_Recepient_Contact_Number.setText(OD_Buyer_Contact_Number);
                holder.ordprop_txt_Complete_Address.setText(OD_Complete_Address);

                holder.ordprop_txt_Seller_Name.setText(OD_Seller_Fullname);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);


                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));



            }else if(OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);
                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));

            }else if(OD_product_only && OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.GONE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.VISIBLE);

                holder.ordprop_txt_Merch_Subtotal.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));
                if(!OD_Delivery_Fee.equals("_")){

                    holder.ordprop_txt_Delivery_Fee.setText("₱ " + decim.format(Integer.parseInt(OD_Delivery_Fee)));
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables) + Integer.parseInt(OD_Delivery_Fee)));


                }else{

                    holder.ordprop_txt_Delivery_Fee.setText("----");
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));

                }


                holder.ordprop_txt_Order_ID.setText(OD_Order_ID);


                try {
                    String timeStamp = OD_Order_Created;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                    SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                    String month = temp1.format(dateFormat.parse(timeStamp));

                    holder.ordprop_txt_Order_Creation_Date.setText(month);
                } catch (ParseException e) {
                    holder.ordprop_txt_Order_Creation_Date.setText(OD_Order_Created);
                    e.printStackTrace();

                }



            }

        }else if(OD_Order_Status.equals("Completed-Failed")) {



            if(!OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Shop_Header.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                holder.ordprop_txt_Status_Headline.setText("Seller failed to deliver the product.");
                holder.ordprop_txt_Status_Body.setText("Please contact the seller for more info. Thank you.");
                holder.ordprop_img_Status_Icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_local_shipping_24));

                holder.ordprop_txt_Date_of_Delivery.setText(OD_Delivery_Date);
                holder.ordprop_txt_Recepient_Name.setText(OD_Buyer_Fullname);
                holder.ordprop_txt_Recepient_Contact_Number.setText(OD_Buyer_Contact_Number);
                holder.ordprop_txt_Complete_Address.setText(OD_Complete_Address);

                holder.ordprop_txt_Seller_Name.setText(OD_Seller_Fullname);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);


                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));



            }else if(OD_product_only && !OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.VISIBLE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load("https://" + mContext.getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + OD_Product_ID + "_1.png")
                        .into(holder.ordprop_img_Product_Cover);
                holder.ordprop_txt_Product_Name.setText(OD_Product_Name);
                holder.ordprop_txt_Product_Quantity.setText("x" + OD_Product_Quantity);
                holder.ordprop_txt_Product_Price.setText("₱ " + decim.format(Integer.parseInt(OD_Product_Price)));

            }else if(OD_product_only && OD_products_Done){

                holder.ordprop_const_Status_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Delivery_Details_Layout.setVisibility(View.GONE);
                holder.ordprop_const_Shop_Header.setVisibility(View.GONE);
                holder.ordprop_const_Order_Items.setVisibility(View.GONE);
                holder.ordprop_const_Totals_Layout.setVisibility(View.VISIBLE);
                holder.ordprop_const_Order_File_Details_Layout.setVisibility(View.VISIBLE);

                holder.ordprop_txt_Merch_Subtotal.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));
                if(!OD_Delivery_Fee.equals("_")){

                    holder.ordprop_txt_Delivery_Fee.setText("₱ " + decim.format(Integer.parseInt(OD_Delivery_Fee)));
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables) + Integer.parseInt(OD_Delivery_Fee)));


                }else{

                    holder.ordprop_txt_Delivery_Fee.setText("----");
                    holder.ordprop_txt_Order_Total.setText("₱ " + decim.format(Integer.parseInt(OD_Total_Payables)));

                }


                holder.ordprop_txt_Order_ID.setText(OD_Order_ID);


                try {
                    String timeStamp = OD_Order_Created;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
                    SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy hh:mm a", Locale.getDefault());
                    String month = temp1.format(dateFormat.parse(timeStamp));

                    holder.ordprop_txt_Order_Creation_Date.setText(month);
                } catch (ParseException e) {
                    holder.ordprop_txt_Order_Creation_Date.setText(OD_Order_Created);
                    e.printStackTrace();

                }



            }


        }

    }

    @Override
    public int getItemCount() {
        return mFBList.size();
    }


    public class orderdetails_viewholder extends RecyclerView.ViewHolder {

        public ConstraintLayout ordprop_const_Status_Layout;
        public TextView ordprop_txt_Status_Headline;
        public TextView ordprop_txt_Status_Body;
        public ImageView ordprop_img_Status_Icon;

        public ConstraintLayout ordprop_const_Delivery_Details_Layout;
        public TextView ordprop_txt_Date_of_Delivery;
        public TextView ordprop_txt_Recepient_Name;
        public TextView ordprop_txt_Recepient_Contact_Number;
        public TextView ordprop_txt_Complete_Address;

        public ConstraintLayout ordprop_const_Shop_Header;
        public TextView ordprop_txt_Seller_Name;
        public TextView ordprop_txt_Visit_Store;
        public ImageView ordprop_img_Visit_Store_Arrow;

        public ConstraintLayout ordprop_const_Order_Items;
        public ImageView ordprop_img_Product_Cover;
        public TextView ordprop_txt_Product_Name;
        public TextView ordprop_txt_Product_Quantity;
        public TextView ordprop_txt_Product_Price;

        public ConstraintLayout ordprop_const_Totals_Layout;
        public TextView ordprop_txt_Merch_Subtotal;
        public TextView ordprop_txt_Delivery_Fee;
        public TextView ordprop_txt_Order_Total;

        public ConstraintLayout ordprop_const_Order_File_Details_Layout;
        public TextView ordprop_txt_Order_ID;
        public TextView ordprop_txt_Order_Creation_Date;
        public Button ordprop_btn_Open_Chat;


        public orderdetails_viewholder(@NonNull View itemView) {
            super(itemView);

            ordprop_const_Status_Layout = itemView.findViewById(R.id.ordprop_itemlist_const_Status_Header);
            ordprop_txt_Status_Headline = itemView.findViewById(R.id.ordprop_itemlist_txt_Headline);
            ordprop_txt_Status_Body = itemView.findViewById(R.id.ordprop_itemlist_txt_Body);
            ordprop_img_Status_Icon = itemView.findViewById(R.id.ordprop_itemlist_img_Status_Icon);

            ordprop_const_Delivery_Details_Layout = itemView.findViewById(R.id.ordprop_itemlist_const_Delivery_Details);
            ordprop_txt_Date_of_Delivery = itemView.findViewById(R.id.ordprop_itemlist_txt_Date_of_Dellivery);
            ordprop_txt_Recepient_Name = itemView.findViewById(R.id.ordprop_itemlist_txt_Recepient_Name);
            ordprop_txt_Recepient_Contact_Number = itemView.findViewById(R.id.ordprop_itemlist_txt_Recepient_Contact_Number);
            ordprop_txt_Complete_Address = itemView.findViewById(R.id.ordprop_itemlist_txt_Recepient_Complete_Address);

            ordprop_const_Shop_Header = itemView.findViewById(R.id.ordprop_itemlist_const_Shop_Header);
            ordprop_txt_Seller_Name = itemView.findViewById(R.id.ordprop_itemlist_txt_Seller_Name);
            ordprop_txt_Visit_Store = itemView.findViewById(R.id.ordprop_itemlist_txt_Visit_Store);
            ordprop_img_Visit_Store_Arrow = itemView.findViewById(R.id.ordprop_itemlist_img_Visit_Store);

            ordprop_const_Totals_Layout = itemView.findViewById(R.id.ordprop_itemlist_const_Total_Amount_Breakdown);
            ordprop_txt_Merch_Subtotal = itemView.findViewById(R.id.ordprop_itemlist_txt_Merch_Subtotal);
            ordprop_txt_Delivery_Fee = itemView.findViewById(R.id.ordprop_itemlist_txt_Delivery_Fee);
            ordprop_txt_Order_Total = itemView.findViewById(R.id.ordprop_itemlist_txt_Order_Total);

            ordprop_const_Order_File_Details_Layout = itemView.findViewById(R.id.ordprop_itemlist_const_Order_File_Details);
            ordprop_txt_Order_ID = itemView.findViewById(R.id.ordprop_itemlist_txt_Order_ID);
            ordprop_txt_Order_Creation_Date = itemView.findViewById(R.id.ordprop_itemlist_txt_Order_Time);
            ordprop_btn_Open_Chat = itemView.findViewById(R.id.ordprop_itemlist_btn_Open_Chat);

            ordprop_const_Order_Items = itemView.findViewById(R.id.ordprop_itemlist_const_Order_item);
            ordprop_img_Product_Cover = itemView.findViewById(R.id.ordprop_itemlist_img_Product_Cover);
            ordprop_txt_Product_Name = itemView.findViewById(R.id.ordprop_itemlist_txt_Product_Name);
            ordprop_txt_Product_Quantity = itemView.findViewById(R.id.ordprop_itemlist_txt_Quantity);
            ordprop_txt_Product_Price = itemView.findViewById(R.id.ordprop_itemlist_txt_Product_Price);



            ordprop_txt_Visit_Store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    orderdetails_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Store_Visit" ,currentItem.getOD_Seller_ID());
                }
            });

            ordprop_img_Visit_Store_Arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    orderdetails_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Store_Visit" ,currentItem.getOD_Seller_ID());
                }
            });

            ordprop_const_Order_Items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    orderdetails_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Product_Preview", currentItem.getOD_Product_ID());
                }
            });

            ordprop_btn_Open_Chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    orderdetails_variable_links currentItem = mFBList.get(getAdapterPosition());

                    mListener.onItemClick(getAdapterPosition(), "Open_Conversation", currentItem.getOD_Order_ID());
                }
            });

        }
    }
}
