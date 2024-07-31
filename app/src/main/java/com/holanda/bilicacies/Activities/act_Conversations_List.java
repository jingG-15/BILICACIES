package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.cart_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.cart_variable_links;
import com.holanda.bilicacies.Adapters.convo_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.convo_variable_links;
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

public class act_Conversations_List extends AppCompatActivity {


    GridLayoutManager mGridLayoutmanager;
    String json_message;


    RecyclerView  R_recycler_convo;
    SwipeRefreshLayout swiper_item_container;
    ImageView img_Back;


    int last_index_array;
    String last_ConvoDate_Fetched;
    int Load_times;

    ArrayList<HashMap<String, String>> Resulting_Convo_List;
    ArrayList<convo_variable_links> convo_list;

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private int previousTotal = 0;
    private int visibleThreshold = 5;

    String logged_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);


        logged_username = getIntent().getExtras().getString("Convo_Logged_Username");

        R_recycler_convo = findViewById(R.id.convoview_recycler_view);
        swiper_item_container = findViewById(R.id.convoview_swipe_ref);
        img_Back = findViewById(R.id.convoview_img_Back);




        mGridLayoutmanager = new GridLayoutManager(this, 1);
        R_recycler_convo.setLayoutManager(mGridLayoutmanager);



        last_index_array = 0;
        last_ConvoDate_Fetched = "0";
        Load_times = 1;
        Resulting_Convo_List = new ArrayList<>();
        convo_list = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        swiper_item_container.setRefreshing(true);
        new Load_Conversations_List().execute();

        R_recycler_convo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = R_recycler_convo.getChildCount();
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
                    if((Load_times >= 1) && (Resulting_Convo_List.size() >= (50 * Load_times))){
                        Load_times = Load_times + 1;
                        new Load_Next_Conversations_List().execute();

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
                last_ConvoDate_Fetched = "0";
                Load_times = 0;
                Resulting_Convo_List = new ArrayList<>();
                convo_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                new Load_Conversations_List().execute();




            }
        });


        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }





    class Load_Conversations_List extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadConvoList.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Con_Last_Convo_Date_Loaded", last_ConvoDate_Fetched);
                data_1.put("Con_First_Load_Flag", "True");
                data_1.put("Con_User_Logged", logged_username);


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
                    if (jsonObj.getString("ConvoPrev").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("ConvoPrev");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("Convo_ID", c.getString("Convo_ID"));
                            result_arr.put("Seller_ID", c.getString("Seller_ID"));
                            result_arr.put("Buyer_ID", c.getString("Buyer_ID"));
                            result_arr.put("Convo_Updated", c.getString("Convo_Updated"));
                            result_arr.put("First_Message", c.getString("First_Message"));
                            result_arr.put("Seller_Fullname", c.getString("Seller_Fullname"));
                            result_arr.put("Buyer_Fullname", c.getString("Buyer_Fullname"));
                            // adding contact to contact list
                            Resulting_Convo_List.add(result_arr);



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

                if(Resulting_Convo_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Convo_List.size(); i++){


                        String Convo_ID;
                        String Seller_ID;
                        String Buyer_ID;
                        String Convo_Updated;
                        String First_Message;
                        String Seller_Fullname;
                        String Buyer_Fullname;


                        HashMap<String, String> currentRow = Resulting_Convo_List.get(i);

                        Convo_ID = currentRow.get("Convo_ID");
                        Seller_ID = currentRow.get("Seller_ID");
                        Buyer_ID = currentRow.get("Buyer_ID");
                        Convo_Updated = currentRow.get("Convo_Updated");
                        last_ConvoDate_Fetched = currentRow.get("Convo_Updated");
                        First_Message = currentRow.get("First_Message");
                        Seller_Fullname = currentRow.get("Seller_Fullname");
                        Buyer_Fullname = currentRow.get("Buyer_Fullname");

                        convo_list.add(new convo_variable_links(Convo_ID, Seller_ID, Buyer_ID,
                                Convo_Updated, First_Message, Seller_Fullname, Buyer_Fullname, logged_username));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_convo.setAdapter(new convo_Recycler_Adapter(act_Conversations_List.this, convo_list, new convo_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {
                            Intent GoToChat = new Intent(act_Conversations_List.this, act_conversations_proper.class);

                            GoToChat.putExtra("Chat_Convo_ID", convo_list.get(position).cn_get_Convo_ID());
                            GoToChat.putExtra("Chat_Supplier_Name", convo_list.get(position).cn_get_Seller_Fullname());
                            GoToChat.putExtra("Chat_Supplier_ID", convo_list.get(position).cn_get_Seller_ID());
                            GoToChat.putExtra("Chat_Logged_Username", logged_username);
                            GoToChat.putExtra("Chat_Buyer_ID", convo_list.get(position).cn_get_Buyer_ID());

                            startActivity(GoToChat);

                        }

                    }));









                    last_index_array = Resulting_Convo_List.size() ;





                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Conversations_List.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Conversations_List.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Conversations_List.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    class Load_Next_Conversations_List extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadConvoList.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Con_Last_Convo_Date_Loaded", last_ConvoDate_Fetched);
                data_1.put("Con_First_Load_Flag", "False");
                data_1.put("Con_User_Logged", logged_username);

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
                    if (jsonObj.getString("ConvoPrev").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("ConvoPrev");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("Convo_ID", c.getString("Convo_ID"));
                            result_arr.put("Seller_ID", c.getString("Seller_ID"));
                            result_arr.put("Buyer_ID", c.getString("Buyer_ID"));
                            result_arr.put("Convo_Updated", c.getString("Convo_Updated"));
                            result_arr.put("First_Message", c.getString("First_Message"));
                            result_arr.put("Seller_Fullname", c.getString("Seller_Fullname"));
                            result_arr.put("Buyer_Fullname", c.getString("Buyer_Fullname"));

                            // adding contact to contact list
                            Resulting_Convo_List.add(result_arr);



                        }

                        json_message = "List Loaded";
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
            //progBar.setVisibility(View.INVISIBLE);
            swiper_item_container.setRefreshing(false);
            if(json_message.equals("List Loaded")){


                if(Resulting_Convo_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Convo_List.size(); i++){


                        String Convo_ID;
                        String Seller_ID;
                        String Buyer_ID;
                        String Convo_Updated;
                        String First_Message;
                        String Seller_Fullname;
                        String Buyer_Fullname;


                        HashMap<String, String> currentRow = Resulting_Convo_List.get(i);

                        Convo_ID = currentRow.get("Convo_ID");
                        Seller_ID = currentRow.get("Seller_ID");
                        Buyer_ID = currentRow.get("Buyer_ID");
                        Convo_Updated = currentRow.get("Convo_Updated");
                        First_Message = currentRow.get("First_Message");
                        Seller_Fullname = currentRow.get("Seller_Fullname");
                        Buyer_Fullname = currentRow.get("Buyer_Fullname");


                        convo_list.add(new convo_variable_links(Convo_ID, Seller_ID, Buyer_ID,
                                Convo_Updated, First_Message, Seller_Fullname, Buyer_Fullname, logged_username));


                    }




                    last_index_array = Resulting_Convo_List.size();
                    R_recycler_convo.getAdapter().notifyDataSetChanged();





                }


            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_Conversations_List.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_Conversations_List.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Conversations_List.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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