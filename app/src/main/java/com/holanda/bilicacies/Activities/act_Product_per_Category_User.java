package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.search_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.search_sell_item_var_link;
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

public class act_Product_per_Category_User extends AppCompatActivity {

    final String PREFS_NAME = "BilicaciesPrefFile";

    Boolean Custom_Mode = false;


    TextView txt_title;

    String Str_FullNameForDisp, Custom_Search_Term, version;

    RecyclerView ppcu_Recycler_View;
    SwipeRefreshLayout ppcu_Swiper_Layout;

    GridLayoutManager ppcu_GridLayoutmanager;

    String last_ID;
    String Last_DB_ID;
    int last_index_array;
    int Load_times;

    ArrayList<HashMap<String, String>> Resulting_Custom_Product_List;
    ArrayList<search_sell_item_var_link> custom_prod_list;

    int firstVisibleItem, visibleItemCount, totalItemCount;

    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    String Search_Type, Search_Term;

    String link, json_message, logged_username;

    int selected_pos_seller_perspective;

    EditText txtin_Search_Term;
    ImageView img_Search, img_Cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_per_category_user);



        ppcu_Recycler_View = findViewById(R.id.ppcu_recycler_view);
        ppcu_Swiper_Layout = findViewById(R.id.ppcu_Swiper_Layout);
        txt_title = findViewById(R.id.ppcu_txt_Show_Title);
        txtin_Search_Term = findViewById(R.id.ppcu_txtin_Search_Term);
        img_Search = findViewById(R.id.ppcu_img_Search);
        img_Cart = findViewById(R.id.ppcu_img_Cart);


        txt_title.setText(getIntent().getExtras().getString("PPCU_Title"));


        Search_Type = getIntent().getExtras().getString("PPCU_Search_Type");
        Search_Term = getIntent().getExtras().getString("PPCU_Search_Term");
        Str_FullNameForDisp = getIntent().getExtras().getString("PPCU_Fullname_Only");


        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;


        if(settings.getBoolean("registered_already_" + version, false)){
            logged_username = settings.getString("logged_Username_" + version, "Not Logged in");




        }



        ppcu_GridLayoutmanager = new GridLayoutManager(this, 2);
        ppcu_Recycler_View.setLayoutManager(ppcu_GridLayoutmanager);



        last_index_array = 0;
        last_ID = "0";
        Load_times = 1;
        Resulting_Custom_Product_List = new ArrayList<>();
        custom_prod_list = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        ppcu_Recycler_View.setVisibility(View.INVISIBLE);
        ppcu_Swiper_Layout.setRefreshing(true);



        switch (Search_Type) {
            case "Category":
                link = "https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/LoadCustomProducts_Category.php";

                new Load_Custom_Products().execute();

                break;
            case "Name_Search":
                link = "https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/LoadCustomProducts_NameSearch.php";
                txtin_Search_Term.setText(Search_Term);
                new Load_Custom_Products().execute();

                break;
            case "Username_Specific":
                link = "https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/LoadCustomProducts_PerUsername.php";

                new Load_Custom_Products().execute();

                break;
        }



        ppcu_Recycler_View.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = ppcu_Recycler_View.getChildCount();
                totalItemCount = ppcu_GridLayoutmanager.getItemCount();
                firstVisibleItem = ppcu_GridLayoutmanager.findFirstVisibleItemPosition();

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
                    if((Load_times >= 1) && (Resulting_Custom_Product_List.size() >= (30 * Load_times))){
                        Load_times = Load_times + 1;

                        if(Custom_Mode == false){
                            new Load_Next_Custom_Products().execute();

                        }else{
                            new Load_Next_Custom_Products_Custom_Term().execute();

                        }


                    }else{
                        //txtEndofProd.setVisibility(View.VISIBLE);


                    }







                    loading = true;
                }

            }
        });


        ppcu_Swiper_Layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Custom_Product_List = new ArrayList<>();
                custom_prod_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                ppcu_Recycler_View.setVisibility(View.INVISIBLE);
                ppcu_Swiper_Layout.setRefreshing(true);

                if(Custom_Mode == false){
                    if(Search_Type.equals("Category")){
                        link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCustomProducts_Category.php";
                        new Load_Custom_Products().execute();

                    }else if(Search_Type.equals("Name_Search")){
                        link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCustomProducts_NameSearch.php";
                        new Load_Custom_Products().execute();

                    }else if(Search_Type.equals("Username_Specific")){
                        link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCustomProducts_PerUsername.php";
                        new Load_Custom_Products().execute();

                    }

                }else{
                    if(Search_Type.equals("Category")){
                        link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCustomProducts_Category_Custom_Term.php";
                        new Load_Custom_Products_Custom_Term().execute();

                    }else if(Search_Type.equals("Name_Search")){
                        link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCustomProducts_NameSearch.php";
                        new Load_Custom_Products().execute();

                    }else if(Search_Type.equals("Username_Specific")){
                        link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadCustomProducts_PerUsername_Custom_Term.php";
                        new Load_Custom_Products_Custom_Term().execute();

                    }

                }











            }
        });

        img_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences settings = act_Product_per_Category_User.this.getSharedPreferences(PREFS_NAME, 0);
                PackageInfo pInfo = null;
                try {
                    pInfo = act_Product_per_Category_User.this.getPackageManager().getPackageInfo(act_Product_per_Category_User.this.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                version = pInfo.versionName;

                if(settings.getBoolean("registered_already_" + version, false)){

                    String logged_username = settings.getString("logged_Username_" + version, "Not Logged in");

                    Intent ViewCart = new Intent(act_Product_per_Category_User.this, act_Cart_View.class);

                    ViewCart.putExtra("Cart_Current_Username", logged_username);

                    startActivity(ViewCart);

                }



            }
        });

    }



    class Load_Custom_Products extends AsyncTask<String, String, String> {
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


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("LCP_Last_ID", last_ID);
                data_1.put("LCP_Search_Term", Search_Term);

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

                        HashMap<String, String> result_arr_temp = new HashMap<>();

                        // adding each child node to HashMap key => value

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
                            Resulting_Custom_Product_List.add(result_arr);



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
            ppcu_Recycler_View.setVisibility(View.VISIBLE);
            ppcu_Swiper_Layout.setRefreshing(false);

            if(json_message.equals("List Loaded")){


                if(Resulting_Custom_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Custom_Product_List.size(); i++){

                        String prod_Price;
                        String prod_Date_Added;
                        String prod_Cover_URL;
                        String prod_ID;
                        String prod_Photo_Count;
                        String prod_Product_Desc;


                        HashMap<String, String> currentRow = Resulting_Custom_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");

                        prod_Price = currentRow.get("Product_Price");
                        prod_Date_Added = currentRow.get("Date_Added");
                        prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        prod_ID = currentRow.get("Product_ID");
                        prod_Photo_Count = currentRow.get("Photo_Count");
                        prod_Product_Desc = currentRow.get("Product_Description");






                        Last_DB_ID = currentRow.get("Product_ID");

                        custom_prod_list.add(new search_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    ppcu_Recycler_View.setAdapter(new search_Recycler_Adapter(act_Product_per_Category_User.this, custom_prod_list, new search_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {

                            selected_pos_seller_perspective = position;



                            Intent ViewSpecificProduct = new Intent(act_Product_per_Category_User.this, act_Buyer_Product_View.class);

                            ViewSpecificProduct.putExtra("bv_Product_ID", custom_prod_list.get(position).getProdID());
                            ViewSpecificProduct.putExtra("bv_Date_Added", custom_prod_list.get(position).getDate());
                            ViewSpecificProduct.putExtra("bv_Photo_Count", custom_prod_list.get(position).getPhotoCount());
                            ViewSpecificProduct.putExtra("bv_Price", custom_prod_list.get(position).getPrice());
                            ViewSpecificProduct.putExtra("bv_Product_Name", custom_prod_list.get(position).getProductName());
                            ViewSpecificProduct.putExtra("bv_Product_Desc", custom_prod_list.get(position).getProductDesc());
                            ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                            startActivity(ViewSpecificProduct);



                        }
                    }));


                    last_index_array = Resulting_Custom_Product_List.size() ;





                }



            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Product_per_Category_User.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }

            txtin_Search_Term.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:



                                Search_Function();

                                return true;
                            default:
                                break;
                        }
                    }

                    return false;
                }
            });


            img_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Search_Function();


                }
            });






        }

    }




    class Load_Custom_Products_Custom_Term extends AsyncTask<String, String, String> {
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


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("LCP_Last_ID", last_ID);
                data_1.put("LCP_Filter_1", Search_Term);
                data_1.put("LCP_Search_Term", Custom_Search_Term);


                Log.i("Debug_Category_Custom", Search_Term);
                Log.i("Debug_Category_Term", Custom_Search_Term);

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

                        HashMap<String, String> result_arr_temp = new HashMap<>();

                        // adding each child node to HashMap key => value

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
                            Resulting_Custom_Product_List.add(result_arr);



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
            ppcu_Recycler_View.setVisibility(View.VISIBLE);
            ppcu_Swiper_Layout.setRefreshing(false);

            if(json_message.equals("List Loaded")){


                if(Resulting_Custom_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Custom_Product_List.size(); i++){

                        String prod_Price;
                        String prod_Date_Added;
                        String prod_Cover_URL;
                        String prod_ID;
                        String prod_Photo_Count;
                        String prod_Product_Desc;


                        HashMap<String, String> currentRow = Resulting_Custom_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");

                        prod_Price = currentRow.get("Product_Price");
                        prod_Date_Added = currentRow.get("Date_Added");
                        prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        prod_ID = currentRow.get("Product_ID");
                        prod_Photo_Count = currentRow.get("Photo_Count");
                        prod_Product_Desc = currentRow.get("Product_Description");






                        Last_DB_ID = currentRow.get("Product_ID");

                        custom_prod_list.add(new search_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    ppcu_Recycler_View.setAdapter(new search_Recycler_Adapter(act_Product_per_Category_User.this, custom_prod_list, new search_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {

                            selected_pos_seller_perspective = position;

                            Intent ViewSpecificProduct = new Intent(act_Product_per_Category_User.this, act_Buyer_Product_View.class);

                            ViewSpecificProduct.putExtra("bv_Product_ID", custom_prod_list.get(position).getProdID());
                            ViewSpecificProduct.putExtra("bv_Date_Added", custom_prod_list.get(position).getDate());
                            ViewSpecificProduct.putExtra("bv_Photo_Count", custom_prod_list.get(position).getPhotoCount());
                            ViewSpecificProduct.putExtra("bv_Price", custom_prod_list.get(position).getPrice());
                            ViewSpecificProduct.putExtra("bv_Product_Name", custom_prod_list.get(position).getProductName());
                            ViewSpecificProduct.putExtra("bv_Product_Desc", custom_prod_list.get(position).getProductDesc());
                            ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                            startActivity(ViewSpecificProduct);



                        }
                    }));


                    last_index_array = Resulting_Custom_Product_List.size() ;





                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Product_per_Category_User.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }

            txtin_Search_Term.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:



                                Search_Function();

                                return true;
                            default:
                                break;
                        }
                    }

                    return false;
                }
            });


            img_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Search_Function();



                }
            });






        }

    }



    class Load_Next_Custom_Products extends AsyncTask<String, String, String> {
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


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("LCP_Last_ID", Last_DB_ID);
                data_1.put("LCP_Search_Term", Search_Term);

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
                            Resulting_Custom_Product_List.add(result_arr);



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
            ppcu_Recycler_View.setVisibility(View.VISIBLE);
            ppcu_Swiper_Layout.setRefreshing(false);
            if(json_message.equals("List Loaded")){



                if(Resulting_Custom_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Custom_Product_List.size(); i++){


                        HashMap<String, String> currentRow = Resulting_Custom_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");
                        String prod_Price = currentRow.get("Product_Price");
                        String prod_Date_Added = currentRow.get("Date_Added");
                        String prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        String prod_ID = currentRow.get("Product_ID");
                        String prod_Photo_Count = currentRow.get("Photo_Count");
                        String prod_Product_Desc = currentRow.get("Product_Description");

                        Last_DB_ID = currentRow.get("Product_ID");

                        custom_prod_list.add(new search_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list);
//                    rec_product_list.setAdapter(prod_adapter);
                    ppcu_Recycler_View.getAdapter().notifyDataSetChanged();


                    last_index_array = Resulting_Custom_Product_List.size() - 1;



                }



            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Product_per_Category_User.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }

            txtin_Search_Term.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:



                                Search_Function();

                                return true;
                            default:
                                break;
                        }
                    }

                    return false;
                }
            });


            img_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Search_Function();

                }
            });


        }

    }






    class Load_Next_Custom_Products_Custom_Term extends AsyncTask<String, String, String> {
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


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("LCP_Last_ID", last_ID);
                data_1.put("LCP_Filter_1", Search_Term);
                data_1.put("LCP_Search_Term", Custom_Search_Term);

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
                            Resulting_Custom_Product_List.add(result_arr);



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
            ppcu_Recycler_View.setVisibility(View.VISIBLE);
            ppcu_Swiper_Layout.setRefreshing(false);
            if(json_message.equals("List Loaded")){



                if(Resulting_Custom_Product_List.size() > 0){
                    for(int i = last_index_array; i < Resulting_Custom_Product_List.size(); i++){


                        HashMap<String, String> currentRow = Resulting_Custom_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");
                        String prod_Price = currentRow.get("Product_Price");
                        String prod_Date_Added = currentRow.get("Date_Added");
                        String prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                        String prod_ID = currentRow.get("Product_ID");
                        String prod_Photo_Count = currentRow.get("Photo_Count");
                        String prod_Product_Desc = currentRow.get("Product_Description");

                        Last_DB_ID = currentRow.get("Product_ID");

                        custom_prod_list.add(new search_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list);
//                    rec_product_list.setAdapter(prod_adapter);
                    ppcu_Recycler_View.getAdapter().notifyDataSetChanged();


                    last_index_array = Resulting_Custom_Product_List.size() - 1;



                }



            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Product_per_Category_User.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Product_per_Category_User.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    private void Search_Function(){



        last_index_array = 0;
        last_ID = "0";
        Load_times = 1;
        Resulting_Custom_Product_List = new ArrayList<>();
        custom_prod_list = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        ppcu_Recycler_View.setVisibility(View.INVISIBLE);
        ppcu_Swiper_Layout.setRefreshing(true);




        String cust_search_term = txtin_Search_Term.getText().toString().trim();


        if(cust_search_term.equals("") || cust_search_term.isEmpty() || cust_search_term == null){

            Toast.makeText(act_Product_per_Category_User.this, "Search box cannot be empty", Toast.LENGTH_LONG).show();


        }else{
            previousTotal = 0;
            loading = true;
            visibleThreshold = 5;

            last_index_array = 0;
            last_ID = "0";
            Load_times = 0;
            Resulting_Custom_Product_List = new ArrayList<>();
            custom_prod_list = new ArrayList<>();
            //txtEndofProd.setVisibility(View.INVISIBLE);
            ppcu_Recycler_View.setVisibility(View.INVISIBLE);
            ppcu_Swiper_Layout.setRefreshing(true);


            switch (Search_Type) {
                case "Category":

                    link = "https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/LoadCustomProducts_Category_Custom_Term.php";
                    txt_title.setText("Search Result for '" + cust_search_term + "' from " + Search_Term);
                    Custom_Mode = true;
                    Custom_Search_Term = cust_search_term;
                    new Load_Custom_Products_Custom_Term().execute();

                    break;
                case "Name_Search":
                    link = "https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/LoadCustomProducts_NameSearch.php";
                    txt_title.setText("Search Result for '" + cust_search_term + "'");
                    Search_Term = cust_search_term;
                    //txtin_Search_Term.setText(Search_Term);
                    new Load_Custom_Products().execute();

                    break;
                case "Username_Specific":
                    link = "https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/LoadCustomProducts_PerUsername_Custom_Term.php";
                    txt_title.setText("Search Result for '" + cust_search_term + "' from " + Str_FullNameForDisp + "'s Products");
                    Custom_Mode = true;
                    Custom_Search_Term = cust_search_term;
                    new Load_Custom_Products_Custom_Term().execute();

                    break;
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