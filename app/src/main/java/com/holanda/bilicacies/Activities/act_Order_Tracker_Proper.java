package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.orderdetails_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.orderdetails_variable_links;
import com.holanda.bilicacies.Adapters.orderlist_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.orderlist_variable_links;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class act_Order_Tracker_Proper extends AppCompatActivity {

    private ProgressDialog pDialog;

    int DENY_REASON_REQUEST = 100;
    int GET_DATE_DEL_FEE_REQUEST = 101;

    String json_message, Denial_Purpose, Delivery_Date, Delivery_Fee;

    String Order_Status, Order_Type, Order_ID, Order_Created, Seller_ID, Buyer_ID, logged_username;

    ConstraintLayout const_To_Send_Controls, const_Nego_Controls;

    ImageView img_Back;

    ArrayList<HashMap<String, String>> Resulting_Order_Details;
    ArrayList<orderdetails_variable_links> order_Details;

    GridLayoutManager mGridLayoutmanager;
    RecyclerView R_recycler_orderProper;
    SwipeRefreshLayout swiper_item_container;

    int last_index_array;
    String last_ID;

    int Load_times;

    int firstVisibleItem, visibleItemCount, totalItemCount;

    private boolean loading = true;

    private int previousTotal = 0;
    private int visibleThreshold = 5;

    String Total_Payables, Seller_FullName;

    ImageButton imgbtn_Approve_Button, imgbtn_Deny_Button;

    Button btn_Delivery_Failed, btn_Delivery_Success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracker_proper);

        logged_username = getIntent().getExtras().getString("OrdProp_Logged_Username");
        Order_Status = getIntent().getExtras().getString("OrdProp_Order_Status");
        Order_Type = getIntent().getExtras().getString("OrdProp_Order_Type");
        Order_ID = getIntent().getExtras().getString("OrdProp_Order_ID");
        Order_Created = getIntent().getExtras().getString("OrdProp_Order_Created");
        Seller_ID = getIntent().getExtras().getString("OrdProp_Seller_ID");
        Buyer_ID = getIntent().getExtras().getString("OrdProp_Buyer_ID");
        Total_Payables = getIntent().getExtras().getString("OrdProp_Total_Payables");
        Seller_FullName = getIntent().getExtras().getString("OrdProp_Seller_Fullname");



        const_Nego_Controls = findViewById(R.id.ordProp_const_Nego_Layout);
        const_To_Send_Controls = findViewById(R.id.ordProp_const_To_Send_Layout);

        imgbtn_Approve_Button = findViewById(R.id.ordProp_imgbtn_Approve);
        imgbtn_Deny_Button = findViewById(R.id.ordProp_imgbtn_Deny);

        btn_Delivery_Success = findViewById(R.id.ordProp_btn_Del_Success);
        btn_Delivery_Failed = findViewById(R.id.ordProp_btn_Del_Failed);

        switch (Order_Status) {
            case "Negotiation":
                if (Order_Type.equals("Buying")) {
                    const_To_Send_Controls.setVisibility(View.GONE);
                    const_Nego_Controls.setVisibility(View.GONE);

                } else if (Order_Type.equals("Selling")) {
                    const_To_Send_Controls.setVisibility(View.GONE);
                    const_Nego_Controls.setVisibility(View.VISIBLE);
                }

                break;
            case "To_Send":
                const_To_Send_Controls.setVisibility(View.VISIBLE);
                const_Nego_Controls.setVisibility(View.GONE);

                break;

            default:
                const_To_Send_Controls.setVisibility(View.GONE);
                const_Nego_Controls.setVisibility(View.GONE);

                break;

        }


        R_recycler_orderProper = findViewById(R.id.ordProp_recycler_view);
        swiper_item_container = findViewById(R.id.ordProp_swipre_ref);
        img_Back = findViewById(R.id.ordProp_img_Back);

        mGridLayoutmanager = new GridLayoutManager(this, 1);
        R_recycler_orderProper.setLayoutManager(mGridLayoutmanager);

        last_index_array = 0;
        last_ID = "0";
        Load_times = 1;
        Resulting_Order_Details = new ArrayList<>();
        order_Details = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        swiper_item_container.setRefreshing(true);

        if(Order_Status.equals("Negotiation")){
            new Load_Order_Details_Nego().execute();

        }else if(Order_Status.equals("To_Send")){

            new Load_Order_Details_To_Send().execute();

        }else if(Order_Status.equals("To_Receive")){

            new Load_Order_Details_To_Receive().execute();

        }else{
            new Load_Order_Details_To_Completed().execute();

        }




        R_recycler_orderProper.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = R_recycler_orderProper.getChildCount();
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
                    if((Load_times >= 1) && (Resulting_Order_Details.size() >= (50 * Load_times))){
                        Load_times = Load_times + 1;
                        //new Load_Next_Order_List().execute();

                    }else{
                        //txtEndofProd.setVisibility(View.VISIBLE);


                    }


                    loading = true;
                }

            }
        });



        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        swiper_item_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Order_Details = new ArrayList<>();
                order_Details = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                if(Order_Status.equals("Negotiation")){
                    new Load_Order_Details_Nego().execute();

                }else if(Order_Status.equals("To_Send")){

                    new Load_Order_Details_To_Send().execute();

                }else if(Order_Status.equals("To_Receive")){

                    new Load_Order_Details_To_Receive().execute();

                }else{
                    new Load_Order_Details_To_Completed().execute();

                }




            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DENY_REASON_REQUEST) {
            if (resultCode == RESULT_OK) {


                Denial_Purpose = data.getStringExtra("Denial_Purpose");
                new Update_Order_Number_Denied().execute();

            }


        }else if(requestCode == GET_DATE_DEL_FEE_REQUEST){

            if (resultCode == RESULT_OK) {


                Delivery_Date = data.getStringExtra("Delivery_Date");
                Delivery_Fee = data.getStringExtra("Delivery_Fee");
                new Update_Order_Number_Approved().execute();

            }

        }


    }

    class  Load_Order_Details_To_Completed extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Load_Order_Details_Completed.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("OrdProp_Last_ID", last_ID);
                data_1.put("OrdProp_First_Load_Flag", "True");
                data_1.put("OrdProp_User_Logged", logged_username);
                data_1.put("OrdProp_Order_ID", Order_ID);
                data_1.put("OrdProp_Buyer_ID", Buyer_ID);


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
                    if (jsonObj.getString("OrderDetails").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("OrderDetails");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value


                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Order_ID", c.getString("Order_ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Quantity", c.getString("Product_Quantity"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Date_Added", c.getString("Product_Date_Added"));
                            result_arr.put("Product_Photo_Count", c.getString("Product_Photo_Count"));
                            result_arr.put("Product_Desc", c.getString("Product_Desc"));
                            result_arr.put("F_Delivery_Date", c.getString("F_Delivery_Date"));
                            result_arr.put("F_Delivery_Fee", c.getString("F_Delivery_Fee"));
                            result_arr.put("F_Buyer_Fullname", c.getString("F_Buyer_Fullname"));
                            result_arr.put("F_Buyer_Contact_Number", c.getString("F_Buyer_Contact_Number"));
                            result_arr.put("F_Buyer_Complete_Address", c.getString("F_Buyer_Complete_Address"));
                            result_arr.put("F_Remarks", c.getString("F_Remarks"));


                            // adding contact to contact list
                            Resulting_Order_Details.add(result_arr);



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

                if(Resulting_Order_Details.size() > 0){

                    String Rem_Seller_Fullname = null;
                    String Rem_Delivery_Fee = null;

                    for(int i = last_index_array; i < Resulting_Order_Details.size(); i++){
                        HashMap<String, String> currentRow = Resulting_Order_Details.get(i);

                        String ID = currentRow.get("ID");
                        String Order_ID = currentRow.get("Order_ID");
                        String Product_ID = currentRow.get("Product_ID");
                        String Product_Quantity = currentRow.get("Product_Quantity");
                        String Product_Price = currentRow.get("Product_Price");
                        String Product_Name = currentRow.get("Product_Name");
                        String Product_Date_Added = currentRow.get("Product_Date_Added");
                        String Product_Photo_Count = currentRow.get("Product_Photo_Count");
                        String Product_Desc = currentRow.get("Product_Desc");
                        String F_Delivery_Fee = currentRow.get("F_Delivery_Fee");
                        String F_Delivery_Date = currentRow.get("F_Delivery_Date");
                        String F_Buyer_Fullname = currentRow.get("F_Buyer_Fullname");
                        String F_Buyer_Contact_Number = currentRow.get("F_Buyer_Contact_Number");
                        String F_Buyer_Complete_Address = currentRow.get("F_Buyer_Complete_Address");
                        String F_Remarks = currentRow.get("F_Remarks");


                        last_ID = ID;



                        if(i == last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    false, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, F_Delivery_Fee, F_Delivery_Date, F_Buyer_Fullname,
                                    F_Buyer_Contact_Number, F_Buyer_Complete_Address, F_Remarks));

                        }else if(i > last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    true, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, F_Delivery_Fee, F_Delivery_Date, F_Buyer_Fullname,
                                    F_Buyer_Contact_Number, F_Buyer_Complete_Address, F_Remarks));

                        }

                        Rem_Seller_Fullname = Seller_FullName;
                        Rem_Delivery_Fee = F_Delivery_Fee;



                    }

                    order_Details.add(new orderdetails_variable_links("-", Order_ID, "-", "-", "-",
                            Seller_ID, Rem_Seller_Fullname, "-", Total_Payables, Order_Type, Order_Status,
                            true, true, Order_Created, "_", "_", "_",
                            Rem_Delivery_Fee, "_", "_",
                            "_", "_", "_"));


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_orderProper.setAdapter(new orderdetails_Recycler_Adapter(act_Order_Tracker_Proper.this, order_Details, new orderdetails_Recycler_Adapter.OnItemClickListener() {


                        @Override
                        public void onItemClick(int position, String type_clicked, String Univ_Variable) {
                            if(type_clicked.equals("Store_Visit")){

                                Intent ViewProfileAct = new Intent(act_Order_Tracker_Proper.this, act_Profile_Preview.class);

                                ViewProfileAct.putExtra("profv_Username", order_Details.get(position).getOD_Seller_ID());

                                startActivity(ViewProfileAct);


                            }else if(type_clicked.equals("Product_Preview")){

                                Intent ViewSpecificProduct = new Intent(act_Order_Tracker_Proper.this, act_Buyer_Product_View.class);

                                ViewSpecificProduct.putExtra("bv_Product_ID", order_Details.get(position).getOD_Product_ID());
                                ViewSpecificProduct.putExtra("bv_Date_Added", order_Details.get(position).getOD_Product_Date_Added());
                                ViewSpecificProduct.putExtra("bv_Photo_Count", order_Details.get(position).getOD_Product_Photo_Count());
                                ViewSpecificProduct.putExtra("bv_Price", order_Details.get(position).getOD_Product_Price());
                                ViewSpecificProduct.putExtra("bv_Product_Name", order_Details.get(position).getOD_Product_Name());
                                ViewSpecificProduct.putExtra("bv_Product_Desc", order_Details.get(position).getOD_Product_Desc());
                                ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                                startActivity(ViewSpecificProduct);


                            }else if(type_clicked.equals("Open_Conversation")){

                                Intent GoToChat = new Intent(act_Order_Tracker_Proper.this, act_conversations_proper.class);

                                GoToChat.putExtra("Chat_Convo_ID", order_Details.get(position).getOD_Order_ID());
                                GoToChat.putExtra("Chat_Supplier_Name", order_Details.get(position).getOD_Seller_Fullname());
                                GoToChat.putExtra("Chat_Supplier_ID", order_Details.get(position).getOD_Seller_ID());
                                GoToChat.putExtra("Chat_Logged_Username", logged_username);
                                GoToChat.putExtra("Chat_Buyer_ID", Buyer_ID);

                                startActivity(GoToChat);


                            }



                        }
                    }));


                    last_index_array = Resulting_Order_Details.size() ;





                }


                imgbtn_Approve_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Get_Date_Delivery_Fee.class);

                        startActivityForResult(ConfirmationActivity, GET_DATE_DEL_FEE_REQUEST);



                    }
                });


                imgbtn_Deny_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Order_Denial_Reason.class);

                        startActivityForResult(ConfirmationActivity, DENY_REASON_REQUEST);




                    }
                });


                btn_Delivery_Failed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Failed().execute();


                    }
                });

                btn_Delivery_Success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Success().execute();

                    }
                });








            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class  Load_Order_Details_To_Receive extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Load_Order_Details_ToReceive.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("OrdProp_Last_ID", last_ID);
                data_1.put("OrdProp_First_Load_Flag", "True");
                data_1.put("OrdProp_User_Logged", logged_username);
                data_1.put("OrdProp_Order_ID", Order_ID);
                data_1.put("OrdProp_Buyer_ID", Buyer_ID);


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
                    if (jsonObj.getString("OrderDetails").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("OrderDetails");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value


                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Order_ID", c.getString("Order_ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Quantity", c.getString("Product_Quantity"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Date_Added", c.getString("Product_Date_Added"));
                            result_arr.put("Product_Photo_Count", c.getString("Product_Photo_Count"));
                            result_arr.put("Product_Desc", c.getString("Product_Desc"));
                            result_arr.put("F_Delivery_Date", c.getString("F_Delivery_Date"));
                            result_arr.put("F_Delivery_Fee", c.getString("F_Delivery_Fee"));
                            result_arr.put("F_Buyer_Fullname", c.getString("F_Buyer_Fullname"));
                            result_arr.put("F_Buyer_Contact_Number", c.getString("F_Buyer_Contact_Number"));
                            result_arr.put("F_Buyer_Complete_Address", c.getString("F_Buyer_Complete_Address"));


                            // adding contact to contact list
                            Resulting_Order_Details.add(result_arr);



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

                if(Resulting_Order_Details.size() > 0){

                    String Rem_Seller_Fullname = null;
                    String Rem_Delivery_Fee = null;

                    for(int i = last_index_array; i < Resulting_Order_Details.size(); i++){
                        HashMap<String, String> currentRow = Resulting_Order_Details.get(i);

                        String ID = currentRow.get("ID");
                        String Order_ID = currentRow.get("Order_ID");
                        String Product_ID = currentRow.get("Product_ID");
                        String Product_Quantity = currentRow.get("Product_Quantity");
                        String Product_Price = currentRow.get("Product_Price");
                        String Product_Name = currentRow.get("Product_Name");
                        String Product_Date_Added = currentRow.get("Product_Date_Added");
                        String Product_Photo_Count = currentRow.get("Product_Photo_Count");
                        String Product_Desc = currentRow.get("Product_Desc");
                        String F_Delivery_Fee = currentRow.get("F_Delivery_Fee");
                        String F_Delivery_Date = currentRow.get("F_Delivery_Date");
                        String F_Buyer_Fullname = currentRow.get("F_Buyer_Fullname");
                        String F_Buyer_Contact_Number = currentRow.get("F_Buyer_Contact_Number");
                        String F_Buyer_Complete_Address = currentRow.get("F_Buyer_Complete_Address");


                        last_ID = ID;



                        if(i == last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    false, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, F_Delivery_Fee, F_Delivery_Date, F_Buyer_Fullname,
                                    F_Buyer_Contact_Number, F_Buyer_Complete_Address, "_"));

                        }else if(i > last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    true, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, F_Delivery_Fee, F_Delivery_Date, F_Buyer_Fullname,
                                    F_Buyer_Contact_Number, F_Buyer_Complete_Address,"_"));

                        }

                        Rem_Seller_Fullname = Seller_FullName;
                        Rem_Delivery_Fee = F_Delivery_Fee;



                    }

                    order_Details.add(new orderdetails_variable_links("-", Order_ID, "-", "-", "-",
                            Seller_ID, Rem_Seller_Fullname, "-", Total_Payables, Order_Type, Order_Status,
                            true, true, Order_Created, "_", "_", "_",
                            Rem_Delivery_Fee, "_", "_",
                            "_", "_", "_"));


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_orderProper.setAdapter(new orderdetails_Recycler_Adapter(act_Order_Tracker_Proper.this, order_Details, new orderdetails_Recycler_Adapter.OnItemClickListener() {


                        @Override
                        public void onItemClick(int position, String type_clicked, String Univ_Variable) {
                            if(type_clicked.equals("Store_Visit")){

                                Intent ViewProfileAct = new Intent(act_Order_Tracker_Proper.this, act_Profile_Preview.class);

                                ViewProfileAct.putExtra("profv_Username", order_Details.get(position).getOD_Seller_ID());

                                startActivity(ViewProfileAct);


                            }else if(type_clicked.equals("Product_Preview")){

                                Intent ViewSpecificProduct = new Intent(act_Order_Tracker_Proper.this, act_Buyer_Product_View.class);

                                ViewSpecificProduct.putExtra("bv_Product_ID", order_Details.get(position).getOD_Product_ID());
                                ViewSpecificProduct.putExtra("bv_Date_Added", order_Details.get(position).getOD_Product_Date_Added());
                                ViewSpecificProduct.putExtra("bv_Photo_Count", order_Details.get(position).getOD_Product_Photo_Count());
                                ViewSpecificProduct.putExtra("bv_Price", order_Details.get(position).getOD_Product_Price());
                                ViewSpecificProduct.putExtra("bv_Product_Name", order_Details.get(position).getOD_Product_Name());
                                ViewSpecificProduct.putExtra("bv_Product_Desc", order_Details.get(position).getOD_Product_Desc());
                                ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                                startActivity(ViewSpecificProduct);


                            }else if(type_clicked.equals("Open_Conversation")){

                                Intent GoToChat = new Intent(act_Order_Tracker_Proper.this, act_conversations_proper.class);

                                GoToChat.putExtra("Chat_Convo_ID", order_Details.get(position).getOD_Order_ID());
                                GoToChat.putExtra("Chat_Supplier_Name", order_Details.get(position).getOD_Seller_Fullname());
                                GoToChat.putExtra("Chat_Supplier_ID", order_Details.get(position).getOD_Seller_ID());
                                GoToChat.putExtra("Chat_Logged_Username", logged_username);
                                GoToChat.putExtra("Chat_Buyer_ID", Buyer_ID);

                                startActivity(GoToChat);


                            }



                        }
                    }));


                    last_index_array = Resulting_Order_Details.size() ;





                }


                imgbtn_Approve_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Get_Date_Delivery_Fee.class);

                        startActivityForResult(ConfirmationActivity, GET_DATE_DEL_FEE_REQUEST);



                    }
                });


                imgbtn_Deny_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Order_Denial_Reason.class);

                        startActivityForResult(ConfirmationActivity, DENY_REASON_REQUEST);




                    }
                });


                btn_Delivery_Failed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Failed().execute();


                    }
                });

                btn_Delivery_Success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Success().execute();

                    }
                });








            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Update_Order_Number_Delivery_Success extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Order_Tracker_Proper.this);
            pDialog.setMessage("Saving. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {

                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Update_Order_Delivered.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("UPDn_User_Logged", logged_username);
                data_1.put("UPDn_Order_ID", Order_ID);
                data_1.put("UPDn_Buyer_ID", Buyer_ID);


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
            pDialog.dismiss();
            if(json_message.equals("Order Update Successful")){


                Intent intent = new Intent();

                setResult(RESULT_FIRST_USER, intent);
                finish();


            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Update_Order_Number_Delivery_Failed extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Order_Tracker_Proper.this);
            pDialog.setMessage("Saving. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {

                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Update_Order_Failed.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("UPDn_User_Logged", logged_username);
                data_1.put("UPDn_Order_ID", Order_ID);
                data_1.put("UPDn_Buyer_ID", Buyer_ID);


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
            pDialog.dismiss();
            if(json_message.equals("Order Update Successful")){


                Intent intent = new Intent();

                setResult(RESULT_FIRST_USER, intent);
                finish();


            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    class Load_Order_Details_To_Send extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Load_Order_Details_ToSend.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("OrdProp_Last_ID", last_ID);
                data_1.put("OrdProp_First_Load_Flag", "True");
                data_1.put("OrdProp_User_Logged", logged_username);
                data_1.put("OrdProp_Order_ID", Order_ID);
                data_1.put("OrdProp_Buyer_ID", Buyer_ID);


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
                    if (jsonObj.getString("OrderDetails").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("OrderDetails");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value


                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Order_ID", c.getString("Order_ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Quantity", c.getString("Product_Quantity"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Date_Added", c.getString("Product_Date_Added"));
                            result_arr.put("Product_Photo_Count", c.getString("Product_Photo_Count"));
                            result_arr.put("Product_Desc", c.getString("Product_Desc"));
                            result_arr.put("F_Delivery_Date", c.getString("F_Delivery_Date"));
                            result_arr.put("F_Delivery_Fee", c.getString("F_Delivery_Fee"));
                            result_arr.put("F_Buyer_Fullname", c.getString("F_Buyer_Fullname"));
                            result_arr.put("F_Buyer_Contact_Number", c.getString("F_Buyer_Contact_Number"));
                            result_arr.put("F_Buyer_Complete_Address", c.getString("F_Buyer_Complete_Address"));


                            // adding contact to contact list
                            Resulting_Order_Details.add(result_arr);



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

                if(Resulting_Order_Details.size() > 0){

                    String Rem_Seller_Fullname = null;
                    String Rem_Delivery_Fee = null;

                    for(int i = last_index_array; i < Resulting_Order_Details.size(); i++){
                        HashMap<String, String> currentRow = Resulting_Order_Details.get(i);

                        String ID = currentRow.get("ID");
                        String Order_ID = currentRow.get("Order_ID");
                        String Product_ID = currentRow.get("Product_ID");
                        String Product_Quantity = currentRow.get("Product_Quantity");
                        String Product_Price = currentRow.get("Product_Price");
                        String Product_Name = currentRow.get("Product_Name");
                        String Product_Date_Added = currentRow.get("Product_Date_Added");
                        String Product_Photo_Count = currentRow.get("Product_Photo_Count");
                        String Product_Desc = currentRow.get("Product_Desc");
                        String F_Delivery_Fee = currentRow.get("F_Delivery_Fee");
                        String F_Delivery_Date = currentRow.get("F_Delivery_Date");
                        String F_Buyer_Fullname = currentRow.get("F_Buyer_Fullname");
                        String F_Buyer_Contact_Number = currentRow.get("F_Buyer_Contact_Number");
                        String F_Buyer_Complete_Address = currentRow.get("F_Buyer_Complete_Address");


                        last_ID = ID;



                        if(i == last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    false, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, F_Delivery_Fee, F_Delivery_Date, F_Buyer_Fullname,
                                    F_Buyer_Contact_Number, F_Buyer_Complete_Address, "_"));

                        }else if(i > last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    true, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, F_Delivery_Fee, F_Delivery_Date, F_Buyer_Fullname,
                                    F_Buyer_Contact_Number, F_Buyer_Complete_Address, "_"));

                        }

                        Rem_Seller_Fullname = Seller_FullName;
                        Rem_Delivery_Fee = F_Delivery_Fee;



                    }

                    order_Details.add(new orderdetails_variable_links("-", Order_ID, "-", "-", "-",
                            Seller_ID, Rem_Seller_Fullname, "-", Total_Payables, Order_Type, Order_Status,
                            true, true, Order_Created, "_", "_", "_",
                            Rem_Delivery_Fee, "_", "_",
                            "_", "_", "_"));


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_orderProper.setAdapter(new orderdetails_Recycler_Adapter(act_Order_Tracker_Proper.this, order_Details, new orderdetails_Recycler_Adapter.OnItemClickListener() {


                        @Override
                        public void onItemClick(int position, String type_clicked, String Univ_Variable) {
                            if(type_clicked.equals("Store_Visit")){

                                Intent ViewProfileAct = new Intent(act_Order_Tracker_Proper.this, act_Profile_Preview.class);

                                ViewProfileAct.putExtra("profv_Username", order_Details.get(position).getOD_Seller_ID());

                                startActivity(ViewProfileAct);


                            }else if(type_clicked.equals("Product_Preview")){

                                Intent ViewSpecificProduct = new Intent(act_Order_Tracker_Proper.this, act_Buyer_Product_View.class);

                                ViewSpecificProduct.putExtra("bv_Product_ID", order_Details.get(position).getOD_Product_ID());
                                ViewSpecificProduct.putExtra("bv_Date_Added", order_Details.get(position).getOD_Product_Date_Added());
                                ViewSpecificProduct.putExtra("bv_Photo_Count", order_Details.get(position).getOD_Product_Photo_Count());
                                ViewSpecificProduct.putExtra("bv_Price", order_Details.get(position).getOD_Product_Price());
                                ViewSpecificProduct.putExtra("bv_Product_Name", order_Details.get(position).getOD_Product_Name());
                                ViewSpecificProduct.putExtra("bv_Product_Desc", order_Details.get(position).getOD_Product_Desc());
                                ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                                startActivity(ViewSpecificProduct);


                            }else if(type_clicked.equals("Open_Conversation")){

                                Intent GoToChat = new Intent(act_Order_Tracker_Proper.this, act_conversations_proper.class);

                                GoToChat.putExtra("Chat_Convo_ID", order_Details.get(position).getOD_Order_ID());
                                GoToChat.putExtra("Chat_Supplier_Name", order_Details.get(position).getOD_Seller_Fullname());
                                GoToChat.putExtra("Chat_Supplier_ID", order_Details.get(position).getOD_Seller_ID());
                                GoToChat.putExtra("Chat_Logged_Username", logged_username);
                                GoToChat.putExtra("Chat_Buyer_ID", Buyer_ID);

                                startActivity(GoToChat);


                            }



                        }
                    }));


                    last_index_array = Resulting_Order_Details.size() ;





                }


                imgbtn_Approve_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Get_Date_Delivery_Fee.class);

                        startActivityForResult(ConfirmationActivity, GET_DATE_DEL_FEE_REQUEST);



                    }
                });


                imgbtn_Deny_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Order_Denial_Reason.class);

                        startActivityForResult(ConfirmationActivity, DENY_REASON_REQUEST);




                    }
                });


                btn_Delivery_Failed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Failed().execute();


                    }
                });

                btn_Delivery_Success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Success().execute();

                    }
                });








            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Update_Order_Number_Approved extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Order_Tracker_Proper.this);
            pDialog.setMessage("Saving. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {

                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Update_Order_Approved.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("UPAp_Last_ID", last_ID);
                data_1.put("UPAp_User_Logged", logged_username);
                data_1.put("UPAp_Order_ID", Order_ID);
                data_1.put("UPAp_Buyer_ID", Buyer_ID);
                data_1.put("UPAp_Delivery_Date", Delivery_Date);
                data_1.put("UPAp_Delivery_Fee", Delivery_Fee);


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
            pDialog.dismiss();
            if(json_message.equals("Order Update Successful")){


                Intent intent = new Intent();

                setResult(RESULT_FIRST_USER, intent);
                finish();


            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Update_Order_Number_Denied extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(act_Order_Tracker_Proper.this);
            pDialog.setMessage("Saving. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {

                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Update_Order_Denied.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("UPDn_Last_ID", last_ID);
                data_1.put("UPDn_User_Logged", logged_username);
                data_1.put("UPDn_Order_ID", Order_ID);
                data_1.put("UPDn_Buyer_ID", Buyer_ID);
                data_1.put("UPDn_Denial_Purpose", Denial_Purpose);


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
            pDialog.dismiss();
            if(json_message.equals("Order Update Successful")){


                Intent intent = new Intent();

                setResult(RESULT_FIRST_USER, intent);
                finish();


            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Load_Order_Details_Nego extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Load_Order_Details_Nego.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("OrdProp_Last_ID", last_ID);
                data_1.put("OrdProp_First_Load_Flag", "True");
                data_1.put("OrdProp_User_Logged", logged_username);
                data_1.put("OrdProp_Order_ID", Order_ID);
                data_1.put("OrdProp_Seller_ID", Seller_ID);


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
                    if (jsonObj.getString("OrderDetails").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("OrderDetails");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value


                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Order_ID", c.getString("Order_ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Quantity", c.getString("Product_Quantity"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Date_Added", c.getString("Product_Date_Added"));
                            result_arr.put("Product_Photo_Count", c.getString("Product_Photo_Count"));
                            result_arr.put("Product_Desc", c.getString("Product_Desc"));



                            // adding contact to contact list
                            Resulting_Order_Details.add(result_arr);



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

                if(Resulting_Order_Details.size() > 0){

                    String Rem_Seller_Fullname = null;

                    for(int i = last_index_array; i < Resulting_Order_Details.size(); i++){
                        HashMap<String, String> currentRow = Resulting_Order_Details.get(i);

                        String ID = currentRow.get("ID");
                        String Order_ID = currentRow.get("Order_ID");
                        String Product_ID = currentRow.get("Product_ID");
                        String Product_Quantity = currentRow.get("Product_Quantity");
                        String Product_Price = currentRow.get("Product_Price");
                        String Product_Name = currentRow.get("Product_Name");
                        String Product_Date_Added = currentRow.get("Product_Date_Added");
                        String Product_Photo_Count = currentRow.get("Product_Photo_Count");
                        String Product_Desc = currentRow.get("Product_Desc");






                        last_ID = ID;



                        if(i == last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    false, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, "_", "_",
                                    "_", "_", "_", "_"));

                        }else if(i > last_index_array){
                            order_Details.add(new orderdetails_variable_links(ID, Order_ID, Product_ID, Product_Quantity, Product_Price,
                                    Seller_ID, Seller_FullName, Product_Name, Total_Payables, Order_Type, Order_Status,
                                    true, false, Order_Created, Product_Date_Added,
                                    Product_Photo_Count, Product_Desc, "_", "_",
                                    "_", "_", "_", "_"));

                        }

                        Rem_Seller_Fullname = Seller_FullName;



                    }

                    order_Details.add(new orderdetails_variable_links("-", Order_ID, "-", "-", "-",
                            Seller_ID, Rem_Seller_Fullname, "-", Total_Payables, Order_Type, Order_Status,
                            true, true, Order_Created, "_", "_", "_",
                            "_", "_", "_",
                            "_", "_", "_"));


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_orderProper.setAdapter(new orderdetails_Recycler_Adapter(act_Order_Tracker_Proper.this, order_Details, new orderdetails_Recycler_Adapter.OnItemClickListener() {


                        @Override
                        public void onItemClick(int position, String type_clicked, String Univ_Variable) {
                            if(type_clicked.equals("Store_Visit")){

                                Intent ViewProfileAct = new Intent(act_Order_Tracker_Proper.this, act_Profile_Preview.class);

                                ViewProfileAct.putExtra("profv_Username", order_Details.get(position).getOD_Seller_ID());

                                startActivity(ViewProfileAct);


                            }else if(type_clicked.equals("Product_Preview")){

                                Intent ViewSpecificProduct = new Intent(act_Order_Tracker_Proper.this, act_Buyer_Product_View.class);

                                ViewSpecificProduct.putExtra("bv_Product_ID", order_Details.get(position).getOD_Product_ID());
                                ViewSpecificProduct.putExtra("bv_Date_Added", order_Details.get(position).getOD_Product_Date_Added());
                                ViewSpecificProduct.putExtra("bv_Photo_Count", order_Details.get(position).getOD_Product_Photo_Count());
                                ViewSpecificProduct.putExtra("bv_Price", order_Details.get(position).getOD_Product_Price());
                                ViewSpecificProduct.putExtra("bv_Product_Name", order_Details.get(position).getOD_Product_Name());
                                ViewSpecificProduct.putExtra("bv_Product_Desc", order_Details.get(position).getOD_Product_Desc());
                                ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                                startActivity(ViewSpecificProduct);


                            }else if(type_clicked.equals("Open_Conversation")){

                                Intent GoToChat = new Intent(act_Order_Tracker_Proper.this, act_conversations_proper.class);

                                GoToChat.putExtra("Chat_Convo_ID", order_Details.get(position).getOD_Order_ID());
                                GoToChat.putExtra("Chat_Supplier_Name", order_Details.get(position).getOD_Seller_Fullname());
                                GoToChat.putExtra("Chat_Supplier_ID", order_Details.get(position).getOD_Seller_ID());
                                GoToChat.putExtra("Chat_Logged_Username", logged_username);
                                GoToChat.putExtra("Chat_Buyer_ID", Buyer_ID);

                                startActivity(GoToChat);


                            }



                        }
                    }));


                    last_index_array = Resulting_Order_Details.size() ;





                }


                imgbtn_Approve_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Get_Date_Delivery_Fee.class);

                        startActivityForResult(ConfirmationActivity, GET_DATE_DEL_FEE_REQUEST);



                    }
                });


                imgbtn_Deny_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ConfirmationActivity = new Intent(act_Order_Tracker_Proper.this, act_Order_Denial_Reason.class);

                        startActivityForResult(ConfirmationActivity, DENY_REASON_REQUEST);




                    }
                });


                btn_Delivery_Failed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Failed().execute();


                    }
                });

                btn_Delivery_Success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Update_Order_Number_Delivery_Success().execute();

                    }
                });








            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_Proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_Proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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