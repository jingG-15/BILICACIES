package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.holanda.bilicacies.Adapters.ViewPagerAdapter;
import com.holanda.bilicacies.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class act_Buyer_Product_View extends AppCompatActivity {

    private ProgressDialog pDialog;

    BottomSheetDialog bottomSheetDialog;


    String json_message, logged_Username;

    String bv_Product_ID, bv_Date_Added, bv_Photo_Count, bv_Price, bv_Product_Name, bv_Product_Desc;

    TextView txt_Product_Seq, txt_Product_Name, txt_Price, txt_Product_Desc, txt_Date_Added;

    ArrayList<HashMap<String, String>> Image_URL_List;

    ViewPager viewPager;
    ViewPagerAdapter myCustomPagerAdapter;

    ProgressBar prog_det_loading;

    ImageView img_Seller_Profile_Pic, img_Address_Icon;
    TextView txt_Seller_Name, txt_Seller_Address, txt_Product_Count, txt_Transaction_Count, txt_Trans_Count_Label,
            txt_Prod_Count_Label, txt_View_Shop_Label;
    ImageButton imgbtn_View_Shop, imgbtn_Product_Count_Container,
            imgbtn_Trans_Count_Container, imgbtn_AddToCart, imgbtn_Chat_Seller;


    String fetched_First_Name;
    String fetched_Middle_Name;
    String fetched_Last_Name;
    String fetched_Municipality;
    String fetched_Barangay;
    String fetched_Extra_Address;
    String fetched_Username;
    String fetched_Product_Count;
    String fetched_Total_Trans;

    ConstraintLayout const_layout;
    SwipeRefreshLayout swipe_layout;

    int Sel_Quantity;

    String Resulting_Convo_ID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_product_view);


        bv_Product_ID = getIntent().getExtras().getString("bv_Product_ID");
        bv_Date_Added = getIntent().getExtras().getString("bv_Date_Added");
        bv_Photo_Count = getIntent().getExtras().getString("bv_Photo_Count");
        bv_Price = getIntent().getExtras().getString("bv_Price");
        bv_Product_Name = getIntent().getExtras().getString("bv_Product_Name");
        bv_Product_Desc = getIntent().getExtras().getString("bv_Product_Desc");
        logged_Username = getIntent().getExtras().getString("bv_Logged_Username");


        txt_Product_Seq = findViewById(R.id.cust_view_txt_Photo_Seq);

        txt_Date_Added = findViewById(R.id.cust_view_txt_Date_Added);
        txt_Price = findViewById(R.id.cust_view_txt_Price);
        txt_Product_Name = findViewById(R.id.cust_view_txt_Product_Name);
        txt_Product_Desc = findViewById(R.id.cust_view_txt_Description);

        prog_det_loading = findViewById(R.id.cust_view_loading_store_Det);
        img_Seller_Profile_Pic = findViewById(R.id.cust_view_img_Profile_Photo);
        img_Address_Icon = findViewById(R.id.cust_view_img_Loc_icon);
        txt_Seller_Name = findViewById(R.id.cust_view_txt_Seller_Name);
        txt_Seller_Address = findViewById(R.id.cust_view_txt_Seller_Address);
        txt_Product_Count = findViewById(R.id.cust_view_txt_Product_Count);
        txt_Transaction_Count = findViewById(R.id.cust_view_txt_Transaction_Count);
        txt_Trans_Count_Label = findViewById(R.id.cust_view_txt_Transaction_Count_Label);
        txt_Prod_Count_Label = findViewById(R.id.cust_view_txt_Product_Count_Label);
        txt_View_Shop_Label = findViewById(R.id.cust_view_txt_View_Shop_Label);
        imgbtn_Product_Count_Container = findViewById(R.id.cust_view_imgbtn_Product_Count_Container);
        imgbtn_Trans_Count_Container = findViewById(R.id.cust_view_imgbtn_Transaction_Count_Container);
        imgbtn_View_Shop = findViewById(R.id.cust_view_imgbtn_View_Shop);
        imgbtn_AddToCart = findViewById(R.id.cust_view_imgbtn_Add_Cart);
        imgbtn_Chat_Seller = findViewById(R.id.cust_view_imgbtn_Chat_Seller);

        const_layout = findViewById(R.id.cust_view_Cons_Layout);
        swipe_layout = findViewById(R.id.cust_view_Swiper);

        const_layout.setVisibility(View.INVISIBLE);
        swipe_layout.setRefreshing(true);




        try {
            String timeStamp = bv_Date_Added;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
            SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy", Locale.getDefault());
            String month = temp1.format(dateFormat.parse(timeStamp));

            txt_Date_Added.setText(month);
        } catch (ParseException e) {
            txt_Date_Added.setText(bv_Date_Added);
            e.printStackTrace();

        }



        txt_Price.setText(bv_Price);
        txt_Product_Name.setText(bv_Product_Name);
        txt_Product_Desc.setText(bv_Product_Desc);


        txt_Product_Seq.setText("1/" + bv_Photo_Count);

        Image_URL_List = new ArrayList<>();


        for (int i = 0; i < Integer.parseInt(bv_Photo_Count); i++) {
            String curr_photo_seq = String.valueOf(i + 1);
            HashMap<String, String> result_arr = new HashMap<>();

            result_arr.put("URL", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + bv_Product_ID + "_" + curr_photo_seq + ".png");

            Image_URL_List.add(result_arr);

        }



        viewPager = (ViewPager)findViewById(R.id.cust_view_viewPager);
        myCustomPagerAdapter = new ViewPagerAdapter(this, Image_URL_List);
        viewPager.setAdapter(myCustomPagerAdapter);

        PageListener pageListener = new PageListener();
        viewPager.addOnPageChangeListener(pageListener);


        new Load_Store_Details().execute();

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                const_layout.setVisibility(View.INVISIBLE);
                swipe_layout.setRefreshing(true);

                myCustomPagerAdapter.notifyDataSetChanged();

                new Load_Store_Details().execute();

            }
        });






    }

    private void showBottomSheetDialog_CartSpecifics() {

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.rec_item_add_to_cart_quantity_layout);

        EditText cart_Quantity = bottomSheetDialog.findViewById(R.id.addcart_txtin_Quantity);
        ImageView cart_Thumbnail = bottomSheetDialog.findViewById(R.id.addcart_img_Product_Thumbnail);
        ImageView cart_Close = bottomSheetDialog.findViewById(R.id.addcart_img_Close);
        TextView cart_Price = bottomSheetDialog.findViewById(R.id.addcart_txt_Price);
        Button cart_Add_to_Cart = bottomSheetDialog.findViewById(R.id.addcart_btn_Add_to_Cart);
        ImageView cart_plus_Quantity = bottomSheetDialog.findViewById(R.id.addcart_img_Add);
        ImageView cart_minus_Quantity = bottomSheetDialog.findViewById(R.id.addcart_img_Minus);
        TextView cart_Product_Name = bottomSheetDialog.findViewById(R.id.addcart_txt_Product_Name);


        assert cart_Product_Name != null;
        cart_Product_Name.setText(bv_Product_Name);

        Sel_Quantity = 1;

        assert cart_Quantity != null;
        cart_Quantity.setText(String.valueOf(Sel_Quantity));

        assert cart_Thumbnail != null;
        Glide.with(this)
                .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + bv_Product_ID + "_1.png")
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(cart_Thumbnail);

        assert cart_Close != null;
        cart_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        assert cart_Price != null;
        cart_Price.setText(bv_Price);

        assert cart_Add_to_Cart != null;
        cart_Add_to_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Add_To_Cart_Data().execute();

            }
        });


        assert cart_plus_Quantity != null;
        cart_plus_Quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Sel_Quantity++;
                cart_Quantity.setText(String.valueOf(Sel_Quantity));

            }
        });


        assert cart_minus_Quantity != null;
        cart_minus_Quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Sel_Quantity > 0){

                    Sel_Quantity--;
                    cart_Quantity.setText(String.valueOf(Sel_Quantity));
                }

            }
        });

        cart_Quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_PREVIOUS:

                        Sel_Quantity = Integer.parseInt(cart_Quantity.getText().toString().trim());
                        cart_Quantity.setText(String.valueOf(Sel_Quantity));

                        return true;
                }
                return false;
            }
        });


        bottomSheetDialog.show();


    }



    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            String pageseq = (position + 1) + "/" + bv_Photo_Count;
            txt_Product_Seq.setText(pageseq);
        }
    }




    class Load_Store_Details extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            prog_det_loading.setVisibility(View.VISIBLE);


            img_Seller_Profile_Pic.setVisibility(View.GONE);
            img_Address_Icon.setVisibility(View.GONE);
            txt_Seller_Name.setVisibility(View.GONE);
            txt_Seller_Address.setVisibility(View.GONE);
            txt_Product_Count.setVisibility(View.GONE);
            txt_Transaction_Count.setVisibility(View.GONE);
            txt_Trans_Count_Label.setVisibility(View.GONE);
            txt_Prod_Count_Label.setVisibility(View.GONE);
            txt_View_Shop_Label.setVisibility(View.GONE);
            imgbtn_Product_Count_Container.setVisibility(View.GONE);
            imgbtn_Trans_Count_Container.setVisibility(View.GONE);
            imgbtn_View_Shop.setVisibility(View.GONE);
            txt_Transaction_Count.setText("----");


        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/FetchProductSellerDetails.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("V_Product_ID", bv_Product_ID);

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( getPostDataString(data_1) );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);

                    break;
                }

                reader.close();
                if (line != null){
                    JSONArray jsonArray = new JSONArray(line);
                    JSONObject obj = jsonArray.getJSONObject(0);

                    json_message = obj.getString("message");


                    if(json_message.equals("Match Found")){

                        fetched_First_Name = obj.getString("First_Name");
                        fetched_Middle_Name = obj.getString("Middle_Name");
                        fetched_Last_Name = obj.getString("Last_Name");
                        fetched_Municipality = obj.getString("Municipality");
                        fetched_Barangay = obj.getString("Barangay");
                        fetched_Extra_Address = obj.getString("Extra_Address");
                        fetched_Username = obj.getString("Username");
                        fetched_Total_Trans = obj.getString("Trans_Total");
                    }


                }else{
                    json_message = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                json_message = new String("Exception: " + e.getMessage());
                //Toast.makeText(getContext(), new String("Exception: " + e.getMessage()), Toast.LENGTH_LONG).show();
            }






            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            if(json_message.equals("Match Found")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();


                Glide.with(act_Buyer_Product_View.this)
                        .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" +
                                fetched_Username + "_profile.png")
                        .circleCrop()
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(img_Seller_Profile_Pic);

                txt_Seller_Name.setText(new StringBuilder().append(fetched_Last_Name).append(", " +
                        "").append(fetched_First_Name).append(" " +
                        "").append(fetched_Middle_Name.substring(0, 1)).append(".").toString());

                txt_Seller_Address.setText(new StringBuilder().append(fetched_Extra_Address).append(", " +
                        "").append(fetched_Barangay).append(", " +
                        "").append(fetched_Municipality).toString());

                txt_Transaction_Count.setText(fetched_Total_Trans);


                prog_det_loading.setVisibility(View.GONE);

                img_Seller_Profile_Pic.setVisibility(View.VISIBLE);
                img_Address_Icon.setVisibility(View.VISIBLE);
                txt_Seller_Name.setVisibility(View.VISIBLE);
                txt_Seller_Address.setVisibility(View.VISIBLE);
                txt_Product_Count.setVisibility(View.VISIBLE);
                txt_Transaction_Count.setVisibility(View.VISIBLE);
                txt_Trans_Count_Label.setVisibility(View.VISIBLE);
                txt_Prod_Count_Label.setVisibility(View.VISIBLE);
                txt_View_Shop_Label.setVisibility(View.VISIBLE);
                imgbtn_Product_Count_Container.setVisibility(View.VISIBLE);
                imgbtn_Trans_Count_Container.setVisibility(View.VISIBLE);
                imgbtn_View_Shop.setVisibility(View.VISIBLE);

                const_layout.setVisibility(View.VISIBLE);
                swipe_layout.setRefreshing(false);


                imgbtn_View_Shop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ViewProfileAct = new Intent(act_Buyer_Product_View.this, act_Profile_Preview.class);

                        ViewProfileAct.putExtra("profv_Username", fetched_Username);

                        startActivity(ViewProfileAct);


                    }
                });

                img_Seller_Profile_Pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ViewProfileAct = new Intent(act_Buyer_Product_View.this, act_Profile_Preview.class);

                        ViewProfileAct.putExtra("profv_Username", fetched_Username);

                        startActivity(ViewProfileAct);
                    }
                });


                imgbtn_AddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(logged_Username.equals(fetched_Username)){
                            Toast.makeText(act_Buyer_Product_View.this, "Cannot add your own product. Action not allowed.", Toast.LENGTH_LONG).show();


                        }else{
                            showBottomSheetDialog_CartSpecifics();

                        }




                    }
                });


                imgbtn_Chat_Seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(logged_Username.equals(fetched_Username)){
                            Toast.makeText(act_Buyer_Product_View.this, "Cannot create conversation with yourself. Action not allowed.", Toast.LENGTH_LONG).show();


                        }else{

                            if(Resulting_Convo_ID == null){

                                new Create_Manual_Convo().execute();

                            }else{

                                Intent GoToChat = new Intent(act_Buyer_Product_View.this, act_conversations_proper.class);

                                GoToChat.putExtra("Chat_Convo_ID", Resulting_Convo_ID);
                                GoToChat.putExtra("Chat_Supplier_Name", new StringBuilder().append(fetched_Last_Name).append(", " +
                                        "").append(fetched_First_Name).append(" " +
                                        "").append(fetched_Middle_Name.substring(0, 1)).append(".").toString());
                                GoToChat.putExtra("Chat_Supplier_ID", fetched_Username);
                                GoToChat.putExtra("Chat_Logged_Username", logged_Username);
                                GoToChat.putExtra("Chat_Buyer_ID", logged_Username);

                                startActivity(GoToChat);

                            }

                        }







                    }
                });


                new Load_Product_Count().execute();

            }else if(json_message.equals("User Not Found")){
                Toast.makeText(act_Buyer_Product_View.this, json_message, Toast.LENGTH_LONG).show();


            }else if(json_message.equals("Product ID Mismatch")){
                //new Create_New_Profile().execute();
                Toast.makeText(act_Buyer_Product_View.this, json_message , Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Buyer_Product_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Load_Product_Count extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();




            txt_Product_Count.setText("----");


        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/GetProductCountSeller.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("G_Username", fetched_Username);

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( getPostDataString(data_1) );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);

                    break;
                }

                reader.close();
                if (line != null){
                    JSONObject jsonObj = new JSONObject(line);
                    if (jsonObj.getString("ProdCountByUser").equals("No Records Found.")){

                        json_message = "No Records Found.";

                    }else{
                        JSONArray Reg_Json = jsonObj.getJSONArray("ProdCountByUser");

                        for (int i = 0; i < Reg_Json.length(); i++) {

                            JSONObject c = Reg_Json.getJSONObject(i);


                           if(c.getString("User_Assigned").equals(fetched_Username)){

                               fetched_Product_Count = c.getString("TotalProds");

                           }


                        }




                        json_message = "Count Loaded";
                    }
                    // Getting JSON Array node

                }else {
                    json_message = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                json_message = new String("Exception: " + e.getMessage());
                //Toast.makeText(getContext(), new String("Exception: " + e.getMessage()), Toast.LENGTH_LONG).show();
            }






            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            if(json_message.equals("Count Loaded")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

                txt_Product_Count.setText(fetched_Product_Count);





            }else if(json_message.equals("Internal Server Error 404")){
                Toast.makeText(act_Buyer_Product_View.this, json_message, Toast.LENGTH_LONG).show();


            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Buyer_Product_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    class Add_To_Cart_Data extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();




            pDialog = new ProgressDialog(act_Buyer_Product_View.this);
            pDialog.setMessage("Adding to cart. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();






        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/AddToCart.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Cart_Prod_ID", bv_Product_ID);
                data_1.put("Cart_Quantity", String.valueOf(Sel_Quantity));
                data_1.put("Cart_User_Assigned", fetched_Username);
                data_1.put("Username_Adder", logged_Username);

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( getPostDataString(data_1) );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);

                    break;
                }

                reader.close();
                if (line != null){
                    JSONArray jsonArray = new JSONArray(line);
                    JSONObject obj = jsonArray.getJSONObject(0);

                    json_message = obj.getString("message");


                }else{
                    json_message = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                json_message = new String("Exception: " + e.getMessage());
                //Toast.makeText(getContext(), new String("Exception: " + e.getMessage()), Toast.LENGTH_LONG).show();
            }






            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            pDialog.dismiss();

            if(json_message.equals("Data Submit Successfully")){

                bottomSheetDialog.dismiss();

                Toast toast = Toast.makeText(act_Buyer_Product_View.this, "Added to cart successfully!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }else if(json_message.equals("Try Again Err: 10")){

                Toast.makeText(act_Buyer_Product_View.this, "Internal Server Error. Please try again or contact administrator.", Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Buyer_Product_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Create_Manual_Convo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Buyer_Product_View.this);
            pDialog.setMessage("Creating Conversation. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/CreateManualConvo.php";





                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("O_Logged_Username", logged_Username);
                data_1.put("O_Seller_Username", fetched_Username);



                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( getPostDataString(data_1) );
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);

                    break;
                }

                reader.close();
                if (line != null){
                    JSONArray jsonArray = new JSONArray(line);
                    JSONObject obj = jsonArray.getJSONObject(0);

                    json_message = obj.getString("message");

                    if(json_message.equals("Convo Created Successfully")){

                        Resulting_Convo_ID = obj.getString("result_convo_id");

                    }



                }else {
                    json_message = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                json_message = new String("Exception: " + e.getMessage());
                //Toast.makeText(getContext(), new String("Exception: " + e.getMessage()), Toast.LENGTH_LONG).show();
            }






            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(json_message.equals("Convo Created Successfully")){


                Intent GoToChat = new Intent(act_Buyer_Product_View.this, act_conversations_proper.class);

                GoToChat.putExtra("Chat_Convo_ID", Resulting_Convo_ID);
                GoToChat.putExtra("Chat_Supplier_Name", new StringBuilder().append(fetched_Last_Name).append(", " +
                        "").append(fetched_First_Name).append(" " +
                        "").append(fetched_Middle_Name.substring(0, 1)).append(".").toString());
                GoToChat.putExtra("Chat_Supplier_ID", fetched_Username);
                GoToChat.putExtra("Chat_Logged_Username", logged_Username);
                GoToChat.putExtra("Chat_Buyer_ID", logged_Username);

                startActivity(GoToChat);




            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Buyer_Product_View.this, json_message, Toast.LENGTH_LONG).show();



            }else{


                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Buyer_Product_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}