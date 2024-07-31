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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.prod_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.prod_sell_item_var_link;
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

public class act_Selling_Products_View extends AppCompatActivity {

    int VIEW_SPECIFIC_PROD_REQ = 115;
    int selected_pos_seller_perspective;

    RecyclerView rec_product_list;
    GridLayoutManager mGridLayoutmanager;

    String loggedUsername, json_message;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    String last_ID;
    int last_index_array;
    String Last_DB_ID;

    //ProgressBar progBar;

    ArrayList<HashMap<String, String>> Resulting_Product_List;

    ArrayList<prod_sell_item_var_link> prod_list;


    prod_Recycler_Adapter prod_adapter;


    EditText txtinSearchTerm;
    ImageView imgSearchbtn;
    TextView txtEndofProd;

    int Load_times;

    SwipeRefreshLayout recyclerHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_products_view);

        loggedUsername = getIntent().getExtras().getString("Logged_Username");

        rec_product_list = findViewById(R.id.sell_prod_recyclerview);
        //progBar = findViewById(R.id.sell_progressbar);
        //progBar.setVisibility(View.GONE);

        txtinSearchTerm = findViewById(R.id.sell_txtin_Search_Term);
        imgSearchbtn = findViewById(R.id.sell_img_Search_Product);
        txtEndofProd = findViewById(R.id.sell_txt_End_of_Prod);
        recyclerHolder = findViewById(R.id.sell_Swiper_Ref_Layout);


        mGridLayoutmanager = new GridLayoutManager(this, 2);
        rec_product_list.setLayoutManager(mGridLayoutmanager);


        //TODO call async here to load products then call the addOnScrollListener below to the postProcess of the asynctask


        last_index_array = 0;
        last_ID = "0";
        Load_times = 1;
        Resulting_Product_List = new ArrayList<>();
        prod_list = new ArrayList<>();
        txtEndofProd.setVisibility(View.INVISIBLE);
        recyclerHolder.setRefreshing(true);
        new Load_Products().execute();


        rec_product_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rec_product_list.getChildCount();
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
                    if((Load_times >= 1) && (Resulting_Product_List.size() >= (10 * Load_times))){
                        Load_times = Load_times + 1;
                        new Load_Next_Sell_Products().execute();

                    }else{
                        txtEndofProd.setVisibility(View.VISIBLE);


                    }







                    loading = true;
                }

            }
        });

        imgSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Searchterm = txtinSearchTerm.getText().toString().trim();

                if(!Searchterm.isEmpty() && !Searchterm.equals(null)){

                    last_index_array = 0;
                    last_ID = "0";
                    Resulting_Product_List = new ArrayList<>();
                    prod_list = new ArrayList<>();
                    recyclerHolder.setRefreshing(true);
                    new Search_Product_by_Name().execute(Searchterm);



                }else{


                    Toast.makeText(act_Selling_Products_View.this, "Product name cannot be empty.", Toast.LENGTH_LONG).show();


                }









            }
        });



        recyclerHolder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Product_List = new ArrayList<>();
                prod_list = new ArrayList<>();
                txtEndofProd.setVisibility(View.INVISIBLE);
                recyclerHolder.setRefreshing(true);
                new Load_Products().execute();




            }
        });






    }





    class Load_Products extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadProcductsByUser.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("S_Username", loggedUsername);
                data_1.put("S_Last_ID", last_ID);

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

                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Date_Added", c.getString("Date_Added"));
                            result_arr.put("Photo_Count", c.getString("Photo_Count"));
                            result_arr.put("Product_Description", c.getString("Product_Description"));

                            // adding contact to contact list
                            Resulting_Product_List.add(result_arr);



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
            recyclerHolder.setRefreshing(false);

            if(json_message.equals("List Loaded")){


                if(Resulting_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Product_List.size(); i++){


                        HashMap<String, String> currentRow = Resulting_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");
                        String prod_Price = currentRow.get("Product_Price");
                        String prod_Date_Added = currentRow.get("Date_Added");
                        String prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        String prod_ID = currentRow.get("Product_ID");
                        String prod_Photo_Count = currentRow.get("Photo_Count");
                        String prod_Product_Desc = currentRow.get("Product_Description");

                        Last_DB_ID = currentRow.get("Product_ID");

                        prod_list.add(new prod_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    rec_product_list.setAdapter(new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, new prod_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {

                            selected_pos_seller_perspective = position;


                            Intent ViewSpecificProduct = new Intent(act_Selling_Products_View.this, act_Product_Preview_Seller_Perspective.class);

                            ViewSpecificProduct.putExtra("pv_Product_ID", prod_list.get(position).getProdID());
                            ViewSpecificProduct.putExtra("pv_Date_Added", prod_list.get(position).getDate());
                            ViewSpecificProduct.putExtra("pv_Photo_Count", prod_list.get(position).getPhotoCount());
                            ViewSpecificProduct.putExtra("pv_Price", prod_list.get(position).getPrice());
                            ViewSpecificProduct.putExtra("pv_Product_Name", prod_list.get(position).getProductName());
                            ViewSpecificProduct.putExtra("pv_Product_Desc", prod_list.get(position).getProductDesc());


                            startActivityForResult(ViewSpecificProduct, VIEW_SPECIFIC_PROD_REQ);



                        }
                    }));


                    last_index_array = Resulting_Product_List.size() ;





                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Selling_Products_View.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Selling_Products_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Selling_Products_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }





    class Load_Next_Sell_Products extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadProcductsByUser.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("S_Username", loggedUsername);
                data_1.put("S_Last_ID", Last_DB_ID);

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

                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Date_Added", c.getString("Date_Added"));
                            result_arr.put("Photo_Count", c.getString("Photo_Count"));
                            result_arr.put("Product_Description", c.getString("Product_Description"));

                            // adding contact to contact list
                            Resulting_Product_List.add(result_arr);



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
            recyclerHolder.setRefreshing(false);
            if(json_message.equals("List Loaded")){


                if(Resulting_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Product_List.size(); i++){


                        HashMap<String, String> currentRow = Resulting_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");
                        String prod_Price = currentRow.get("Product_Price");
                        String prod_Date_Added = currentRow.get("Date_Added");
                        String prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        String prod_ID = currentRow.get("Product_ID");
                        String prod_Photo_Count = currentRow.get("Photo_Count");
                        String prod_Product_Desc = currentRow.get("Product_Description");

                        Last_DB_ID = currentRow.get("Product_ID");

                        prod_list.add(new prod_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list);
//                    rec_product_list.setAdapter(prod_adapter);
                    rec_product_list.getAdapter().notifyDataSetChanged();


                    last_index_array = Resulting_Product_List.size() - 1;



                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Selling_Products_View.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Selling_Products_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Selling_Products_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Search_Product_by_Name extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
           // progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/SearchProcductsByUserAndName.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("S_Username", loggedUsername);
                data_1.put("S_Search_Term", args[0]);

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

                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Product_ID", c.getString("Product_ID"));
                            result_arr.put("Product_Name", c.getString("Product_Name"));
                            result_arr.put("Product_Price", c.getString("Product_Price"));
                            result_arr.put("Date_Added", c.getString("Date_Added"));
                            result_arr.put("Photo_Count", c.getString("Photo_Count"));
                            result_arr.put("Product_Description", c.getString("Product_Description"));


                            // adding contact to contact list
                            Resulting_Product_List.add(result_arr);



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
            recyclerHolder.setRefreshing(false);
            if(json_message.equals("List Loaded")){


                if(Resulting_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Product_List.size(); i++){


                        HashMap<String, String> currentRow = Resulting_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");
                        String prod_Price = currentRow.get("Product_Price");
                        String prod_Date_Added = currentRow.get("Date_Added");
                        String prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        String prod_ID = currentRow.get("Product_ID");
                        String prod_Photo_Count = currentRow.get("Photo_Count");
                        String prod_Product_Desc = currentRow.get("Product_Description");

                        Last_DB_ID = currentRow.get("Product_ID");

                        prod_list.add(new prod_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);
                    rec_product_list.setAdapter(new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, new prod_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {

                            selected_pos_seller_perspective = position;

                            Intent ViewSpecificProduct = new Intent(act_Selling_Products_View.this, act_Product_Preview_Seller_Perspective.class);

                            ViewSpecificProduct.putExtra("pv_Product_ID", prod_list.get(position).getProdID());
                            ViewSpecificProduct.putExtra("pv_Date_Added", prod_list.get(position).getDate());
                            ViewSpecificProduct.putExtra("pv_Photo_Count", prod_list.get(position).getPhotoCount());
                            ViewSpecificProduct.putExtra("pv_Price", prod_list.get(position).getPrice());
                            ViewSpecificProduct.putExtra("pv_Product_Name", prod_list.get(position).getProductName());
                            ViewSpecificProduct.putExtra("pv_Product_Desc", prod_list.get(position).getProductDesc());


                            startActivityForResult(ViewSpecificProduct, VIEW_SPECIFIC_PROD_REQ);


                        }
                    }));


                    last_index_array = Resulting_Product_List.size() ;



                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Selling_Products_View.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Selling_Products_View.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Selling_Products_View.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == VIEW_SPECIFIC_PROD_REQ){
            if(resultCode == 2){

                //TODO
                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Product_List = new ArrayList<>();
                prod_list = new ArrayList<>();
                txtEndofProd.setVisibility(View.INVISIBLE);
                recyclerHolder.setRefreshing(true);
                new Load_Products().execute();





            }

        }



    }

}