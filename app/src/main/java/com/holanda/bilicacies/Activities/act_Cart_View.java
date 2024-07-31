package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.cart_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.cart_variable_links;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class act_Cart_View extends AppCompatActivity {

    static int PASSWORD_CONFIRMATION_REQ = 109;

    String logged_username, json_message;

    ImageView img_Back;
    ImageView img_Conversations;
    SwipeRefreshLayout swiper_item_container;
    RecyclerView R_recycler_cart;
    CheckBox chkbx_Select_All;
    TextView txt_Sub_Total_Price;
    ImageButton imgbtn_Checkout;

    int Sub_Total;

    String prod_ID_to_Change_Qty, prod_new_Qty;
    int index_Apply_Button;

    String prod_ID_for_Delete;



    GridLayoutManager mGridLayoutmanager;

    int last_index_array;
    String last_Last_Date;
    int Load_times;

    ArrayList<HashMap<String, String>> Resulting_Cart_List;
    ArrayList<cart_variable_links> cart_list;

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private int previousTotal = 0;
    private int visibleThreshold = 5;

    private ProgressDialog pDialog;

    int index_subtractor = 0;


    int checkout_index_cycler;
    String checkout_Previous_Supplier, checkout_Resulting_Order_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);


        logged_username = getIntent().getExtras().getString("Cart_Current_Username");


        img_Back = findViewById(R.id.conpro_img_Back);
        swiper_item_container = findViewById(R.id.cartview_swipe_ref);
        R_recycler_cart = findViewById(R.id.cartview_recycler_view);
        chkbx_Select_All = findViewById(R.id.cartview_chkbox_Select_All);
        imgbtn_Checkout = findViewById(R.id.cartview_imgbtn_Checkout);
        imgbtn_Checkout.setEnabled(false);
        img_Conversations = findViewById(R.id.cartview_img_Conversations);

        txt_Sub_Total_Price = findViewById(R.id.cartview_txt_SubTotal);
        txt_Sub_Total_Price.setText("0");



        mGridLayoutmanager = new GridLayoutManager(this, 1);
        R_recycler_cart.setLayoutManager(mGridLayoutmanager);

        Sub_Total = 0;

        last_index_array = 0;
        last_Last_Date = "0";
        Load_times = 1;
        index_subtractor = 0;
        Resulting_Cart_List = new ArrayList<>();
        cart_list = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        swiper_item_container.setRefreshing(true);
        new Load_Cart().execute();

        R_recycler_cart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = R_recycler_cart.getChildCount();
                totalItemCount = mGridLayoutmanager.getItemCount();
                firstVisibleItem = mGridLayoutmanager.findFirstVisibleItemPosition();

                if (loading) {

                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;

                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("Yaeye!", "end called");

                    // Do something
                    if((Load_times >= 1) && (Resulting_Cart_List.size() >= (50 * Load_times))){
                        Load_times = Load_times + 1;
                        new Load_Next_Cart_Items().execute();

                    }else{
                        //txtEndofProd.setVisibility(View.VISIBLE);


                    }







                    loading = true;
                }

            }
        });


        swiper_item_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                index_subtractor = 0;

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_Last_Date = "0";
                Load_times = 0;
                Resulting_Cart_List = new ArrayList<>();
                cart_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                new Load_Cart().execute();




            }
        });





        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        img_Conversations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ConvoAct = new Intent(getApplicationContext(), act_Conversations_List.class);
                ConvoAct.putExtra("Convo_Logged_Username", logged_username);
                startActivity(ConvoAct);



            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PASSWORD_CONFIRMATION_REQ) {
            if (resultCode == RESULT_OK) {

                for (int x = R_recycler_cart.getChildCount(), i = 0; i < x; ++i) {
                    RecyclerView.ViewHolder holder = R_recycler_cart.getChildViewHolder(R_recycler_cart.getChildAt(i));
                    CheckBox itemMarker = holder.itemView.findViewById(R.id.cartview_itemlist_chkbx_item_mark);
                    if(itemMarker.isChecked() && cart_list.get(i).c_get_Type_to_Display().equals("Product_Details")){

                        checkout_index_cycler = i;

                        new Create_Order_ID().execute();

                        break;

                    }

                }

            }


        }


    }






    class Load_Cart extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
            //progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCartItems.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("C_Last_Date", last_Last_Date);
                data_1.put("C_First_Load_Flag", "True");
                data_1.put("C_User_Logged", logged_username);

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
                    if (jsonObj.getString("CartPrev").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("CartPrev");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("Prod_ID", c.getString("Prod_ID"));
                            result_arr.put("Prod_Quantity", c.getString("Prod_Quantity"));
                            result_arr.put("User_Assigned", c.getString("User_Assigned"));
                            result_arr.put("Seller_Fullname", c.getString("Seller_Fullname"));
                            result_arr.put("Prod_Name", c.getString("Prod_Name"));
                            result_arr.put("Prod_Price", c.getString("Prod_Price"));
                            result_arr.put("Group_Updated", c.getString("Group_Updated"));
                            result_arr.put("Prod_Date_Added", c.getString("Prod_Date_Added"));
                            result_arr.put("Prod_Photo_Count", c.getString("Prod_Photo_Count"));
                            result_arr.put("Prod_Product_Desc", c.getString("Prod_Product_Desc"));
                            // adding contact to contact list
                            Resulting_Cart_List.add(result_arr);



                        }

                        json_message = "List Loaded";
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
            //progBar.setVisibility(View.INVISIBLE);
            swiper_item_container.setRefreshing(false);

            if(json_message.equals("List Loaded")){

                if(Resulting_Cart_List.size() > 0){
                    String last_User_Assigned = "";
                    for(int i = last_index_array; i < Resulting_Cart_List.size(); i++){


                        String Prod_ID;
                        String Prod_Quantity;
                        String User_Assigned;
                        String Full_Name;
                        String Prod_Name;
                        String Prod_Price;
                        String Prod_Date_Added;
                        String Prod_Photo_Count;
                        String Prod_Product_Desc;




                        HashMap<String, String> currentRow = Resulting_Cart_List.get(i);

                        Prod_ID = currentRow.get("Prod_ID");
                        Prod_Quantity = currentRow.get("Prod_Quantity");
                        User_Assigned = currentRow.get("User_Assigned");
                        Prod_Name = currentRow.get("Prod_Name");
                        Prod_Price = currentRow.get("Prod_Price");
                        last_Last_Date = currentRow.get("Group_Updated");
                        Prod_Date_Added = currentRow.get("Prod_Date_Added");
                        Prod_Photo_Count = currentRow.get("Prod_Photo_Count");
                        Prod_Product_Desc = currentRow.get("Prod_Product_Desc");
                        Full_Name = currentRow.get("Seller_Fullname");


                        if(last_User_Assigned.equals("") || !last_User_Assigned.equals(User_Assigned)){
                            cart_list.add(new cart_variable_links(Full_Name, User_Assigned, Prod_Name, Prod_Price, Prod_Quantity, Prod_ID, "Supplier_Name", Prod_Date_Added,
                                    Prod_Photo_Count, Prod_Product_Desc));
                            last_User_Assigned = User_Assigned;
                            index_subtractor++;
                        }
                        cart_list.add(new cart_variable_links(Full_Name, User_Assigned, Prod_Name, Prod_Price, Prod_Quantity, Prod_ID, "Product_Details", Prod_Date_Added,
                                Prod_Photo_Count, Prod_Product_Desc));


                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_cart.setAdapter(new cart_Recycler_Adapter(act_Cart_View.this, cart_list, new cart_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position, String Type_Clicked, String Multi_Purpose_Var, Boolean itemMarked) {

                            if(Type_Clicked.equals("Supplier_Name")){
//
                                Intent ViewProfileAct = new Intent(act_Cart_View.this, act_Profile_Preview.class);

                                ViewProfileAct.putExtra("profv_Username", Multi_Purpose_Var);

                                startActivity(ViewProfileAct);


                            }else if(Type_Clicked.equals("Product_Details")){

                                Intent ViewSpecificProduct = new Intent(act_Cart_View.this, act_Buyer_Product_View.class);

                                ViewSpecificProduct.putExtra("bv_Product_ID", cart_list.get(position).c_get_Product_ID());
                                ViewSpecificProduct.putExtra("bv_Date_Added", cart_list.get(position).c_get_Date_Added());
                                ViewSpecificProduct.putExtra("bv_Photo_Count", cart_list.get(position).c_get_Photo_Count());
                                ViewSpecificProduct.putExtra("bv_Price", cart_list.get(position).c_get_Price());
                                ViewSpecificProduct.putExtra("bv_Product_Name", cart_list.get(position).c_get_Product_Name());
                                ViewSpecificProduct.putExtra("bv_Product_Desc", cart_list.get(position).c_get_Product_Desc());
                                ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                                startActivity(ViewSpecificProduct);


                            }else if(Type_Clicked.equals("Cart_Add")){

                                DecimalFormat decim = new DecimalFormat("#,###.##");

                                if(itemMarked){


                                    Sub_Total = Sub_Total - (Integer.parseInt(cart_list.get(position).c_get_Quantity()) * Integer.parseInt(cart_list.get(position).c_get_Price()));

                                }

                                cart_list.set(position, new cart_variable_links(cart_list.get(position).c_get_Supplier_Name(), cart_list.get(position).c_get_Supplier_Username(),
                                        cart_list.get(position).c_get_Product_Name(), cart_list.get(position).c_get_Price(), Multi_Purpose_Var,
                                        cart_list.get(position).c_get_Product_ID(), cart_list.get(position).c_get_Type_to_Display(),
                                        cart_list.get(position).c_get_Date_Added(), cart_list.get(position).c_get_Photo_Count(),
                                        cart_list.get(position).c_get_Product_Desc()));



                                if(itemMarked){

                                    Sub_Total = Sub_Total + (Integer.parseInt(Multi_Purpose_Var) * Integer.parseInt(cart_list.get(position).c_get_Price()));
                                    txt_Sub_Total_Price.setText(decim.format(Sub_Total));

                                }



                            }else if(Type_Clicked.equals("Cart_Minus")){

                                DecimalFormat decim = new DecimalFormat("#,###.##");

                                if(itemMarked){


                                    Sub_Total = Sub_Total - (Integer.parseInt(cart_list.get(position).c_get_Quantity()) * Integer.parseInt(cart_list.get(position).c_get_Price()));

                                }

                                cart_list.set(position, new cart_variable_links(cart_list.get(position).c_get_Supplier_Name(), cart_list.get(position).c_get_Supplier_Username(),
                                        cart_list.get(position).c_get_Product_Name(), cart_list.get(position).c_get_Price(), Multi_Purpose_Var,
                                        cart_list.get(position).c_get_Product_ID(), cart_list.get(position).c_get_Type_to_Display(),
                                        cart_list.get(position).c_get_Date_Added(), cart_list.get(position).c_get_Photo_Count(),
                                        cart_list.get(position).c_get_Product_Desc()));



                                if(itemMarked){

                                    Sub_Total = Sub_Total + (Integer.parseInt(Multi_Purpose_Var) * Integer.parseInt(cart_list.get(position).c_get_Price()));
                                    txt_Sub_Total_Price.setText(decim.format(Sub_Total));

                                }

                            }else if(Type_Clicked.equals("Delete_Item_in_Cart")){

                                prod_ID_for_Delete = Multi_Purpose_Var;


                                AlertDialog.Builder builder = new AlertDialog.Builder(act_Cart_View.this);

                                builder.setTitle("Are you sure to delete the cart item selected?");
                                builder.setMessage("Product Name: " + cart_list.get(position).c_get_Product_Name());

                                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing but close the dialog

                                        new Delete_Prod_in_Cart().execute();

                                        dialog.dismiss();
                                    }
                                });

                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();








                            }else if(Type_Clicked.equals("Marked")){
                                DecimalFormat decim = new DecimalFormat("#,###.##");

                                if(cart_list.get(position).c_get_Type_to_Display().equals("Product_Details")){
                                    Sub_Total = Sub_Total + (Integer.parseInt(Multi_Purpose_Var) * Integer.parseInt(cart_list.get(position).c_get_Quantity()));
                                    txt_Sub_Total_Price.setText(decim.format(Sub_Total));

                                    imgbtn_Checkout.setEnabled(Sub_Total > 0);

                                }



                            }else if(Type_Clicked.equals("Un-Marked")){
                                DecimalFormat decim = new DecimalFormat("#,###.##");

                                if(cart_list.get(position).c_get_Type_to_Display().equals("Product_Details")){
                                    Sub_Total = Sub_Total - (Integer.parseInt(Multi_Purpose_Var) * Integer.parseInt(cart_list.get(position).c_get_Quantity()));
                                    txt_Sub_Total_Price.setText(decim.format(Sub_Total));

                                    imgbtn_Checkout.setEnabled(Sub_Total > 0);

                                }



                            }else if(Type_Clicked.equals("Quantity_Manual_Change")){

                                DecimalFormat decim = new DecimalFormat("#,###.##");

                                if(itemMarked){


                                    Sub_Total = Sub_Total - (Integer.parseInt(Multi_Purpose_Var) * Integer.parseInt(cart_list.get(position).c_get_Quantity()));


                                }


                                cart_list.set(position, new cart_variable_links(cart_list.get(position).c_get_Supplier_Name(), cart_list.get(position).c_get_Supplier_Username(),
                                        cart_list.get(position).c_get_Product_Name(), cart_list.get(position).c_get_Price(), Multi_Purpose_Var,
                                        cart_list.get(position).c_get_Product_ID(), cart_list.get(position).c_get_Type_to_Display(),
                                        cart_list.get(position).c_get_Date_Added(), cart_list.get(position).c_get_Photo_Count(),
                                        cart_list.get(position).c_get_Product_Desc()));

                                if(itemMarked){

                                    Sub_Total = Sub_Total + (Integer.parseInt(Multi_Purpose_Var) * Integer.parseInt(cart_list.get(position).c_get_Quantity()));
                                    txt_Sub_Total_Price.setText(decim.format(Sub_Total));


                                }



                            }else if(Type_Clicked.equals("Supplier_Chk_Tappped")){
                                for (int x = R_recycler_cart.getChildCount(), i = 0; i < x; ++i) {
                                    RecyclerView.ViewHolder holder = R_recycler_cart.getChildViewHolder(R_recycler_cart.getChildAt(i));
                                    CheckBox itemMarker = holder.itemView.findViewById(R.id.cartview_itemlist_chkbx_item_mark);
                                    if(cart_list.get(i).c_get_Supplier_Username().equals(Multi_Purpose_Var)){
                                        itemMarker.setChecked(itemMarked);
                                    }
                                }



                            }else if(Type_Clicked.equals("Update_Quantity_Apply")){
                                prod_ID_to_Change_Qty = Multi_Purpose_Var;
                                prod_new_Qty = cart_list.get(position).c_get_Quantity();
                                index_Apply_Button = position;

                                new Apply_Qty_Changes().execute();

                            }


                        }

                    }));





                    chkbx_Select_All.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            for (int x = R_recycler_cart.getChildCount(), i = 0; i < x; ++i) {
                                RecyclerView.ViewHolder holder = R_recycler_cart.getChildViewHolder(R_recycler_cart.getChildAt(i));
                                CheckBox itemMarker = holder.itemView.findViewById(R.id.cartview_itemlist_chkbx_Supplier_Name);
                                if(cart_list.get(i).c_get_Type_to_Display().equals("Supplier_Name")){
                                    itemMarker.setChecked(isChecked);
                                }

                            }
                            imgbtn_Checkout.setEnabled(isChecked);


                        }
                    });

                    imgbtn_Checkout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent ConfirmationActivity = new Intent(getApplicationContext(), act_Password_Confirmation.class);
                            ConfirmationActivity.putExtra("Logged_Username", logged_username);
                            startActivityForResult(ConfirmationActivity, PASSWORD_CONFIRMATION_REQ);

                        }
                    });



                    last_index_array = Resulting_Cart_List.size() ;





                }




            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();
                R_recycler_cart.setVisibility(View.INVISIBLE);

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }





    class Load_Next_Cart_Items extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
            //progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCartItems.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("C_Last_Date", last_Last_Date);
                data_1.put("C_First_Load_Flag", "False");
                data_1.put("C_User_Logged", logged_username);

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
                    if (jsonObj.getString("ProductPrev").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Prod_Json = jsonObj.getJSONArray("ProductPrev");
                        int total_prods = 0;
                        for (int i = 0; i < Prod_Json.length(); i++) {

                            JSONObject c = Prod_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("Prod_ID", c.getString("Prod_ID"));
                            result_arr.put("Prod_Quantity", c.getString("Prod_Quantity"));
                            result_arr.put("User_Assigned", c.getString("User_Assigned"));
                            result_arr.put("Seller_Fullname", c.getString("Seller_Fullname"));
                            result_arr.put("Prod_Name", c.getString("Prod_Name"));
                            result_arr.put("Prod_Price", c.getString("Prod_Price"));
                            result_arr.put("Group_Updated", c.getString("Group_Updated"));
                            result_arr.put("Prod_Date_Added", c.getString("Prod_Date_Added"));
                            result_arr.put("Prod_Photo_Count", c.getString("Prod_Photo_Count"));
                            result_arr.put("Prod_Product_Desc", c.getString("Prod_Product_Desc"));
                            // adding contact to contact list
                            Resulting_Cart_List.add(result_arr);



                        }

                        json_message = "List Loaded";
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
            //progBar.setVisibility(View.INVISIBLE);
            swiper_item_container.setRefreshing(false);
            if(json_message.equals("List Loaded")){





                if(Resulting_Cart_List.size() > 0){
                    String last_User_Assigned = "";
                    for(int i = last_index_array; i < Resulting_Cart_List.size(); i++){


                        String Prod_ID;
                        String Prod_Quantity;
                        String User_Assigned;
                        String Prod_Name;
                        String Prod_Price;
                        String Prod_Date_Added;
                        String Prod_Photo_Count;
                        String Prod_Product_Desc;
                        String Full_Name;




                        HashMap<String, String> currentRow = Resulting_Cart_List.get(i);

                        Prod_ID = currentRow.get("Prod_ID");
                        Prod_Quantity = currentRow.get("Prod_Quantity");
                        User_Assigned = currentRow.get("User_Assigned");
                        Prod_Name = currentRow.get("Prod_Name");
                        Prod_Price = currentRow.get("Prod_Price");
                        last_Last_Date = currentRow.get("Group_Updated");
                        Prod_Date_Added = currentRow.get("Prod_Date_Added");
                        Prod_Photo_Count = currentRow.get("Prod_Photo_Count");
                        Prod_Product_Desc = currentRow.get("Prod_Product_Desc");
                        Full_Name = currentRow.get("Seller_Fullname");


                        if(last_User_Assigned.equals("") || !last_User_Assigned.equals(User_Assigned)){
                            cart_list.add(new cart_variable_links(Full_Name, User_Assigned, Prod_Name, Prod_Price, Prod_Quantity, Prod_ID, "Supplier_Name", Prod_Date_Added,
                                    Prod_Photo_Count, Prod_Product_Desc));
                            last_User_Assigned = User_Assigned;
                            index_subtractor++;
                        }
                        cart_list.add(new cart_variable_links(Full_Name, User_Assigned, Prod_Name, Prod_Price, Prod_Quantity, Prod_ID, "Product_Details", Prod_Date_Added,
                                Prod_Photo_Count, Prod_Product_Desc));


                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);




                    last_index_array = Resulting_Cart_List.size() ;

                    R_recycler_cart.getAdapter().notifyDataSetChanged();




                }




            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Apply_Qty_Changes extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Cart_View.this);
            pDialog.setMessage("Saving changes. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/UpdateCartItemQty.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("U_Prod_ID", prod_ID_to_Change_Qty);
                data_1.put("U_Username_Logged", logged_username);
                data_1.put("U_New_Qty", prod_new_Qty);

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
            if(json_message.equals("Cart Update Successful")){

                RecyclerView.ViewHolder holder = R_recycler_cart.getChildViewHolder(R_recycler_cart.getChildAt(index_Apply_Button));
                Button btn_Apply = holder.itemView.findViewById(R.id.cartview_itemlist_btn_Apply_Qty_Changes);
                btn_Apply.setVisibility(View.GONE);

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Delete_Prod_in_Cart extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Cart_View.this);
            pDialog.setMessage("Deleting cart item. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/DeleteCartItem.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("D_Prod_ID", prod_ID_for_Delete);
                data_1.put("D_Username_Logged", logged_username);

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
            if(json_message.equals("Delete Successful")){

                index_subtractor = 0;

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_Last_Date = "0";
                Load_times = 0;
                Resulting_Cart_List = new ArrayList<>();
                cart_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                new Load_Cart().execute();




            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Create_Order_ID extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Cart_View.this);
            pDialog.setMessage("Creating Order. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Checkout_Create_Order_Convo.php";





                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("O_Logged_Username", logged_username);
                data_1.put("O_Seller_Username", cart_list.get(checkout_index_cycler).c_get_Supplier_Username());



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

                    if(json_message.equals("Order Created Successfully")){

                        checkout_Resulting_Order_ID = obj.getString("result_order_id");

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
            //pDialog.dismiss();
            if(json_message.equals("Order Created Successfully")){


                new Insert_Items_to_Order_1().execute();


            }else if(json_message.equals("No Response from server")){

                pDialog.dismiss();
                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();



            }else{
                pDialog.dismiss();

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    class Insert_Items_to_Order_1 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Insert_Order_Items.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("IO_Logged_Username", logged_username);
                data_1.put("IO_Seller_Username", cart_list.get(checkout_index_cycler).c_get_Supplier_Username());
                data_1.put("IO_Order_ID", checkout_Resulting_Order_ID);
                data_1.put("IO_Product_ID", cart_list.get(checkout_index_cycler).c_get_Product_ID());
                data_1.put("IO_Product_Quantity", cart_list.get(checkout_index_cycler).c_get_Quantity());
                data_1.put("IO_Product_Price", cart_list.get(checkout_index_cycler).c_get_Price());

                checkout_Previous_Supplier = cart_list.get(checkout_index_cycler).c_get_Supplier_Username();


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
            //pDialog.dismiss();
            if(json_message.equals("Order_Item_Insert_Success")){

                if(checkout_index_cycler < R_recycler_cart.getChildCount()) {

                    if ((checkout_index_cycler + 1) >= R_recycler_cart.getChildCount()) {

                        pDialog.dismiss();
                        Toast.makeText(act_Cart_View.this, "Check out Successful! Please check your conversations tab.", Toast.LENGTH_LONG).show();


                        index_subtractor = 0;

                        previousTotal = 0;
                        loading = true;
                        visibleThreshold = 5;

                        last_index_array = 0;
                        last_Last_Date = "0";
                        Load_times = 0;
                        Resulting_Cart_List = new ArrayList<>();
                        cart_list = new ArrayList<>();
                        //txtEndofProd.setVisibility(View.INVISIBLE);
                        swiper_item_container.setRefreshing(true);
                        new Load_Cart().execute();


                    } else {



                        for (int x = R_recycler_cart.getChildCount(), i = checkout_index_cycler + 1; i < x; ++i) {
                            RecyclerView.ViewHolder holder = R_recycler_cart.getChildViewHolder(R_recycler_cart.getChildAt(i));
                            CheckBox itemMarker = holder.itemView.findViewById(R.id.cartview_itemlist_chkbx_item_mark);

                            if (itemMarker.isChecked()) {

                                checkout_index_cycler = i;

                                if(cart_list.get(i).c_get_Type_to_Display().equals("Product_Details")) {
                                    if (cart_list.get(i).c_get_Supplier_Username().equals(checkout_Previous_Supplier)) {

                                        new Insert_Items_to_Order_2().execute();

                                    } else {

                                        new Create_Order_ID().execute();
                                    }
                                }

                                break;


                            }


                        }

                    }

                }else{

                    pDialog.dismiss();
                    Toast.makeText(act_Cart_View.this, "Check out Successful! Please check your conversations tab.", Toast.LENGTH_LONG).show();


                    index_subtractor = 0;

                    previousTotal = 0;
                    loading = true;
                    visibleThreshold = 5;

                    last_index_array = 0;
                    last_Last_Date = "0";
                    Load_times = 0;
                    Resulting_Cart_List = new ArrayList<>();
                    cart_list = new ArrayList<>();
                    //txtEndofProd.setVisibility(View.INVISIBLE);
                    swiper_item_container.setRefreshing(true);
                    new Load_Cart().execute();


                    //TODO Open Conversations Tab after this line and when conversations UI Functionality is done.

                }







            }else if(json_message.equals("No Response from server")){
                pDialog.dismiss();

                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();


            }else{
                pDialog.dismiss();
                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Insert_Items_to_Order_2 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Insert_Order_Items.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("IO_Logged_Username", logged_username);
                data_1.put("IO_Seller_Username", cart_list.get(checkout_index_cycler).c_get_Supplier_Username());
                data_1.put("IO_Order_ID", checkout_Resulting_Order_ID);
                data_1.put("IO_Product_ID", cart_list.get(checkout_index_cycler).c_get_Product_ID());
                data_1.put("IO_Product_Quantity", cart_list.get(checkout_index_cycler).c_get_Quantity());
                data_1.put("IO_Product_Price", cart_list.get(checkout_index_cycler).c_get_Price());

                checkout_Previous_Supplier = cart_list.get(checkout_index_cycler).c_get_Supplier_Username();


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
            //pDialog.dismiss();
            if(json_message.equals("Order_Item_Insert_Success")){



                if(checkout_index_cycler < R_recycler_cart.getChildCount()){
                    if ((checkout_index_cycler + 1) >= R_recycler_cart.getChildCount()) {

                        pDialog.dismiss();
                        Toast.makeText(act_Cart_View.this, "Check out Successful! Please check your conversations tab.", Toast.LENGTH_LONG).show();



                        index_subtractor = 0;

                        previousTotal = 0;
                        loading = true;
                        visibleThreshold = 5;

                        last_index_array = 0;
                        last_Last_Date = "0";
                        Load_times = 0;
                        Resulting_Cart_List = new ArrayList<>();
                        cart_list = new ArrayList<>();
                        //txtEndofProd.setVisibility(View.INVISIBLE);
                        swiper_item_container.setRefreshing(true);
                        new Load_Cart().execute();


                    } else {



                        for (int x = R_recycler_cart.getChildCount(), i = checkout_index_cycler + 1; i < x; ++i) {
                            RecyclerView.ViewHolder holder = R_recycler_cart.getChildViewHolder(R_recycler_cart.getChildAt(i));
                            CheckBox itemMarker = holder.itemView.findViewById(R.id.cartview_itemlist_chkbx_item_mark);

                            if (itemMarker.isChecked()) {

                                checkout_index_cycler = i;

                                if(cart_list.get(i).c_get_Type_to_Display().equals("Product_Details")) {
                                    if (cart_list.get(i).c_get_Supplier_Username().equals(checkout_Previous_Supplier)) {

                                        new Insert_Items_to_Order_1().execute();

                                    } else {

                                        new Create_Order_ID().execute();
                                    }
                                }

                                break;


                            }


                        }

                    }

                }else{

                    pDialog.dismiss();
                    Toast.makeText(act_Cart_View.this, "Check out Successful! Please check your conversations tab.", Toast.LENGTH_LONG).show();


                    index_subtractor = 0;

                    previousTotal = 0;
                    loading = true;
                    visibleThreshold = 5;

                    last_index_array = 0;
                    last_Last_Date = "0";
                    Load_times = 0;
                    Resulting_Cart_List = new ArrayList<>();
                    cart_list = new ArrayList<>();
                    //txtEndofProd.setVisibility(View.INVISIBLE);
                    swiper_item_container.setRefreshing(true);
                    new Load_Cart().execute();


                    //TODO Open Conversations Tab after this line and when conversations UI Functionality is done.

                }





            }else if(json_message.equals("No Response from server")){

                pDialog.dismiss();
                Toast.makeText(act_Cart_View.this, json_message, Toast.LENGTH_LONG).show();


            }else{
                pDialog.dismiss();

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Cart_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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