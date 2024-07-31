package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.convo_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.convo_variable_links;
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

public class act_Order_Tracker_List extends AppCompatActivity {

    int ORDER_TRACKER_PROPER = 100;

    String logged_username, json_message ;

    GridLayoutManager mGridLayoutmanager;
    RecyclerView R_recycler_orderlist;
    SwipeRefreshLayout swiper_item_container;
    ImageView img_Back;
    int last_index_array;
    String last_ID;

    int Load_times;


    ArrayList<HashMap<String, String>> Resulting_Order_List;
    ArrayList<orderlist_variable_links> order_list;

    int firstVisibleItem, visibleItemCount, totalItemCount;

    private boolean loading = true;

    private int previousTotal = 0;
    private int visibleThreshold = 5;

    String Order_Status_To_Display;

    TextView txt_Header_Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracker_list);

        logged_username = getIntent().getExtras().getString("OrdList_Logged_Username");
        Order_Status_To_Display = getIntent().getExtras().getString("OrdList_Order_Status_Display");

        R_recycler_orderlist = findViewById(R.id.ordlist_recycler_view);
        swiper_item_container = findViewById(R.id.ordlist_swipe_ref);
        img_Back = findViewById(R.id.ordlist_img_Back);
        txt_Header_Type = findViewById(R.id.ordlist_txt_Label);

        txt_Header_Type.setText("Orders (" + Order_Status_To_Display + ")");

        mGridLayoutmanager = new GridLayoutManager(this, 1);
        R_recycler_orderlist.setLayoutManager(mGridLayoutmanager);



        last_index_array = 0;
        last_ID = "0";
        Load_times = 1;
        Resulting_Order_List = new ArrayList<>();
        order_list = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        swiper_item_container.setRefreshing(true);
        new Load_Order_List().execute();

        R_recycler_orderlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = R_recycler_orderlist.getChildCount();
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
                    if((Load_times >= 1) && (Resulting_Order_List.size() >= (50 * Load_times))){
                        Load_times = Load_times + 1;
                        new Load_Next_Order_List().execute();

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

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Order_List = new ArrayList<>();
                order_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                new Load_Order_List().execute();




            }
        });

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ORDER_TRACKER_PROPER) {
            if (resultCode == RESULT_FIRST_USER) {

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Order_List = new ArrayList<>();
                order_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                new Load_Order_List().execute();

            }


        }


    }




    class Load_Order_List extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Load_Order_List.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Ord_Last_ID", last_ID);
                data_1.put("Ord_First_Load_Flag", "True");
                data_1.put("Ord_User_Logged", logged_username);
                data_1.put("Ord_Order_Status", Order_Status_To_Display);


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
                    if (jsonObj.getString("OrderList").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("OrderList");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value


                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Order_ID", c.getString("Order_ID"));
                            result_arr.put("Seller_ID", c.getString("Seller_ID"));
                            result_arr.put("Buyer_ID", c.getString("Buyer_ID"));
                            result_arr.put("Order_Status", c.getString("Order_Status"));
                            result_arr.put("Order_Type", c.getString("Order_Type"));
                            result_arr.put("Order_Created", c.getString("Order_Created"));
                            result_arr.put("Seller_Fullname", c.getString("Seller_Fullname"));
                            result_arr.put("First_Product_ID", c.getString("First_Product_ID"));
                            result_arr.put("First_Product_Name", c.getString("First_Product_Name"));
                            result_arr.put("First_Product_Quantity", c.getString("First_Product_Quantity"));
                            result_arr.put("First_Product_Price", c.getString("First_Product_Price"));
                            result_arr.put("Total_Product_Order_Count", c.getString("Total_Product_Order_Count"));
                            result_arr.put("Total_Payables", c.getString("Total_Payables"));


                            // adding contact to contact list
                            Resulting_Order_List.add(result_arr);



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

                if(Resulting_Order_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Order_List.size(); i++){


                        String ID;
                        String Order_ID;
                        String Seller_ID;
                        String Buyer_ID;
                        String Order_Status;
                        String Order_Type;
                        String Order_Created;
                        String Seller_Fullname;
                        String First_Product_ID;
                        String First_Product_Name;
                        String First_Product_Quantity;
                        String First_Product_Price;
                        String Total_Product_Order_Count;
                        String Total_Payables;




                        HashMap<String, String> currentRow = Resulting_Order_List.get(i);

                        ID = currentRow.get("ID");
                        last_ID = ID;
                        Order_ID = currentRow.get("Order_ID");
                        Seller_ID = currentRow.get("Seller_ID");
                        Buyer_ID = currentRow.get("Buyer_ID");
                        Order_Status = currentRow.get("Order_Status");
                        Order_Type = currentRow.get("Order_Type");
                        Order_Created = currentRow.get("Order_Created");
                        Seller_Fullname = currentRow.get("Seller_Fullname");
                        First_Product_ID = currentRow.get("First_Product_ID");
                        First_Product_Name = currentRow.get("First_Product_Name");
                        First_Product_Quantity = currentRow.get("First_Product_Quantity");
                        First_Product_Price = currentRow.get("First_Product_Price");
                        Total_Product_Order_Count = currentRow.get("Total_Product_Order_Count");
                        Total_Payables = currentRow.get("Total_Payables");


                        order_list.add(new orderlist_variable_links(ID, Order_ID, Seller_ID, Buyer_ID, Order_Status, Order_Type,
                                Order_Created, Seller_Fullname, First_Product_ID, First_Product_Name, First_Product_Quantity,
                                First_Product_Price, Total_Product_Order_Count, Total_Payables));


                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_orderlist.setAdapter(new orderlist_Recycler_Adapter(act_Order_Tracker_List.this, order_list, new orderlist_Recycler_Adapter.OnItemClickListener() {


                        @Override
                        public void onItemClick(int position) {

                            Intent OrderDetails = new Intent(act_Order_Tracker_List.this, act_Order_Tracker_Proper.class);

                            OrderDetails.putExtra("OrdProp_Logged_Username", logged_username);
                            OrderDetails.putExtra("OrdProp_Order_Status", order_list.get(position).getO_Order_Status());
                            OrderDetails.putExtra("OrdProp_Order_Type", order_list.get(position).getO_Order_Type());
                            OrderDetails.putExtra("OrdProp_Order_ID", order_list.get(position).getO_Order_ID());
                            OrderDetails.putExtra("OrdProp_Order_Created", order_list.get(position).getO_Order_Created());
                            OrderDetails.putExtra("OrdProp_Seller_ID", order_list.get(position).getO_Seller_ID());
                            OrderDetails.putExtra("OrdProp_Buyer_ID", order_list.get(position).getO_Buyer_ID());
                            OrderDetails.putExtra("OrdProp_Total_Payables", order_list.get(position).getO_Total_Payables());
                            OrderDetails.putExtra("OrdProp_Seller_Fullname", order_list.get(position).getO_Seller_Fullname());


                            startActivityForResult(OrderDetails, ORDER_TRACKER_PROPER);





                        }
                    }));


                    last_index_array = Resulting_Order_List.size() ;





                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Order_Tracker_List.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_List.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_List.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Load_Next_Order_List extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Load_Order_List.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Ord_Last_ID", last_ID);
                data_1.put("Ord_First_Load_Flag", "False");
                data_1.put("Ord_User_Logged", logged_username);
                data_1.put("Ord_Order_Status", Order_Status_To_Display);


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
                    if (jsonObj.getString("OrderList").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("OrderList");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value


                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Order_ID", c.getString("Order_ID"));
                            result_arr.put("Seller_ID", c.getString("Seller_ID"));
                            result_arr.put("Buyer_ID", c.getString("Buyer_ID"));
                            result_arr.put("Order_Status", c.getString("Order_Status"));
                            result_arr.put("Order_Type", c.getString("Order_Type"));
                            result_arr.put("Order_Created", c.getString("Order_Created"));
                            result_arr.put("Seller_Fullname", c.getString("Seller_Fullname"));
                            result_arr.put("First_Product_ID", c.getString("First_Product_ID"));
                            result_arr.put("First_Product_Name", c.getString("First_Product_Name"));
                            result_arr.put("First_Product_Quantity", c.getString("First_Product_Quantity"));
                            result_arr.put("First_Product_Price", c.getString("First_Product_Price"));
                            result_arr.put("Total_Product_Order_Count", c.getString("Total_Product_Order_Count"));
                            result_arr.put("Total_Payables", c.getString("Total_Payables"));


                            // adding contact to contact list
                            Resulting_Order_List.add(result_arr);



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

                if(Resulting_Order_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Order_List.size(); i++){


                        String ID;
                        String Order_ID;
                        String Seller_ID;
                        String Buyer_ID;
                        String Order_Status;
                        String Order_Type;
                        String Order_Created;
                        String Seller_Fullname;
                        String First_Product_ID;
                        String First_Product_Name;
                        String First_Product_Quantity;
                        String First_Product_Price;
                        String Total_Product_Order_Count;
                        String Total_Payables;




                        HashMap<String, String> currentRow = Resulting_Order_List.get(i);

                        ID = currentRow.get("ID");
                        last_ID = ID;
                        Order_ID = currentRow.get("Order_ID");
                        Seller_ID = currentRow.get("Seller_ID");
                        Buyer_ID = currentRow.get("Buyer_ID");
                        Order_Status = currentRow.get("Order_Status");
                        Order_Type = currentRow.get("Order_Type");
                        Order_Created = currentRow.get("Order_Created");
                        Seller_Fullname = currentRow.get("Seller_Fullname");
                        First_Product_ID = currentRow.get("First_Product_ID");
                        First_Product_Name = currentRow.get("First_Product_Name");
                        First_Product_Quantity = currentRow.get("First_Product_Quantity");
                        First_Product_Price = currentRow.get("First_Product_Price");
                        Total_Product_Order_Count = currentRow.get("Total_Product_Order_Count");
                        Total_Payables = currentRow.get("Total_Payables");

                        order_list.add(new orderlist_variable_links(ID, Order_ID, Seller_ID, Buyer_ID, Order_Status, Order_Type,
                                Order_Created, Seller_Fullname, First_Product_ID, First_Product_Name, First_Product_Quantity,
                                First_Product_Price, Total_Product_Order_Count, Total_Payables));


                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    last_index_array = Resulting_Order_List.size() ;
                    R_recycler_orderlist.getAdapter().notifyDataSetChanged();

                    }

            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Order_Tracker_List.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Order_Tracker_List.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Order_Tracker_List.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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