package com.holanda.bilicacies.Fragments.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.holanda.bilicacies.Activities.act_Conversations_List;
import com.holanda.bilicacies.Activities.act_Order_Tracker_List;
import com.holanda.bilicacies.Activities.act_conversations_proper;
import com.holanda.bilicacies.Adapters.convo_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.convo_variable_links;
import com.holanda.bilicacies.Adapters.notif_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.notif_variable_links;
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

public class NotificationsFragment extends Fragment {

    final String PREFS_NAME = "BilicaciesPrefFile";

    ImageView img_Notif_Icon;
    TextView txt_Header;
    TextView txt_Body;

    String logged_username, version;

    RecyclerView  R_recycler_notifs;
    SwipeRefreshLayout swiper_item_container;

    GridLayoutManager mGridLayoutmanager;
    String json_message;

    int last_index_array;
    String last_ID_Fetched;
    int Load_times;

    ArrayList<HashMap<String, String>> Resulting_Notif_List;
    ArrayList<notif_variable_links> notif_list;

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private int previousTotal = 0;
    private int visibleThreshold = 5;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        img_Notif_Icon = root.findViewById(R.id.notif_itemlist_img_Type_Icon);
        txt_Header = root.findViewById(R.id.notif_itemlist_txt_Headline);
        txt_Body = root.findViewById(R.id.notif_itemlist_txt_Body);

        R_recycler_notifs = root.findViewById(R.id.notifview_recycler_view);
        swiper_item_container = root.findViewById(R.id.notifview_swipe_ref);


        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;


        if(settings.getBoolean("registered_already_" + version, false)){

            logged_username = settings.getString("logged_Username_" + version, "Not Logged in");


            mGridLayoutmanager = new GridLayoutManager(getContext(), 1);
            R_recycler_notifs.setLayoutManager(mGridLayoutmanager);



            last_index_array = 0;
            last_ID_Fetched = "0";
            Load_times = 1;
            Resulting_Notif_List = new ArrayList<>();
            notif_list = new ArrayList<>();
            //txtEndofProd.setVisibility(View.INVISIBLE);
            swiper_item_container.setRefreshing(true);
            new Load_Notif_List().execute();



            R_recycler_notifs.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = R_recycler_notifs.getChildCount();
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
                        if((Load_times >= 1) && (Resulting_Notif_List.size() >= (50 * Load_times))){
                            Load_times = Load_times + 1;
                            new Load_Next_Notif_List().execute();

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
                    last_ID_Fetched = "0";
                    Load_times = 0;
                    Resulting_Notif_List = new ArrayList<>();
                    notif_list = new ArrayList<>();
                    //txtEndofProd.setVisibility(View.INVISIBLE);
                    swiper_item_container.setRefreshing(true);
                    new Load_Notif_List().execute();




                }
            });



        }

        return root;
    }


    class Load_Notif_List extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadNotifList.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Nt_Last_ID_Loaded", last_ID_Fetched);
                data_1.put("Nt_First_Load_Flag", "True");
                data_1.put("Nt_User_Logged", logged_username);


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
                    if (jsonObj.getString("NotifPrev").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("NotifPrev");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("Notif_Type", c.getString("Notif_Type"));
                            result_arr.put("Notif_Content", c.getString("Notif_Content"));
                            result_arr.put("Notif_Data_1", c.getString("Notif_Data_1"));
                            result_arr.put("Notif_Data_2", c.getString("Notif_Data_2"));
                            result_arr.put("Notif_Data_3", c.getString("Notif_Data_3"));
                            result_arr.put("Notif_Date", c.getString("Notif_Date"));
                            result_arr.put("ID", c.getString("ID"));
                            // adding contact to contact list
                            Resulting_Notif_List.add(result_arr);



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

                if(Resulting_Notif_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Notif_List.size(); i++){

                        String Notif_Type;
                        String Notif_Content;
                        String Notif_Data_1;
                        String Notif_Data_2;
                        String Notif_Data_3;
                        String Notif_Date;


                        HashMap<String, String> currentRow = Resulting_Notif_List.get(i);

                        Notif_Type = currentRow.get("Notif_Type");
                        Notif_Content = currentRow.get("Notif_Content");
                        Notif_Data_1 = currentRow.get("Notif_Data_1");
                        Notif_Data_2 = currentRow.get("Notif_Data_2");
                        Notif_Data_3 = currentRow.get("Notif_Data_3");
                        Notif_Date = currentRow.get("FNotif_Date");
                        last_ID_Fetched = currentRow.get("ID");

                        notif_list.add(new notif_variable_links(Notif_Type, Notif_Content, Notif_Data_1, Notif_Data_2,
                                Notif_Data_3, Notif_Date));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_notifs.setAdapter(new notif_Recycler_Adapter(getContext(), notif_list, new notif_Recycler_Adapter.OnItemClickListener() {


                        @Override
                        public void onItemClick(int position, String Type_Clicked, String Univ_Variable_1, String Univ_Variable_2, String Univ_Variable_3) {
                            if(Type_Clicked.equals("Order_Filed")){
                                Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                                OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                                OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Negotiation");


                                startActivity(OrderTrackingList);

                            }else if(Type_Clicked.equals("New_Message")){

                                Intent ConvoAct = new Intent(getContext(), act_Conversations_List.class);
                                ConvoAct.putExtra("Convo_Logged_Username", logged_username);
                                startActivity(ConvoAct);

                            }else if(Type_Clicked.equals("Order_Approved")){
                                if(Univ_Variable_1.equals("Order_List_To_Receive")){

                                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "To_Receive");

                                    startActivity(OrderTrackingList);

                                }

                            }else if(Type_Clicked.equals("Order_Delivered")){
                                if(Univ_Variable_1.equals("Order_List_Completed")){

                                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Completed");

                                    startActivity(OrderTrackingList);

                                }

                            }else if(Type_Clicked.equals("Order_Denial")){
                                if(Univ_Variable_1.equals("Order_List_Completed")){

                                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Completed");

                                    startActivity(OrderTrackingList);


                                }

                            }else if(Type_Clicked.equals("Order_Failed")){
                                if(Univ_Variable_1.equals("Order_List_Completed")){

                                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Completed");

                                    startActivity(OrderTrackingList);


                                }

                            }
                        }
                    }));


                    last_index_array = Resulting_Notif_List.size() ;





                }












            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(getContext(), json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(getContext(), json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(getContext(), finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Load_Next_Notif_List extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadNotifList.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Nt_Last_ID_Loaded", last_ID_Fetched);
                data_1.put("Nt_First_Load_Flag", "False");
                data_1.put("Nt_User_Logged", logged_username);

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
                    if (jsonObj.getString("NotifPrev").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("NotifPrev");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("Notif_Type", c.getString("Notif_Type"));
                            result_arr.put("Notif_Content", c.getString("Notif_Content"));
                            result_arr.put("Notif_Data_1", c.getString("Notif_Data_1"));
                            result_arr.put("Notif_Data_2", c.getString("Notif_Data_2"));
                            result_arr.put("Notif_Data_3", c.getString("Notif_Data_3"));
                            result_arr.put("Notif_Date", c.getString("Notif_Date"));
                            result_arr.put("ID", c.getString("ID"));
                            // adding contact to contact list
                            Resulting_Notif_List.add(result_arr);



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


                if(Resulting_Notif_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Notif_List.size(); i++){


                        String Notif_Type;
                        String Notif_Content;
                        String Notif_Data_1;
                        String Notif_Data_2;
                        String Notif_Data_3;
                        String Notif_Date;


                        HashMap<String, String> currentRow = Resulting_Notif_List.get(i);

                        Notif_Type = currentRow.get("Notif_Type");
                        Notif_Content = currentRow.get("Notif_Content");
                        Notif_Data_1 = currentRow.get("Notif_Data_1");
                        Notif_Data_2 = currentRow.get("Notif_Data_2");
                        Notif_Data_3 = currentRow.get("Notif_Data_3");
                        Notif_Date = currentRow.get("FNotif_Date");
                        last_ID_Fetched = currentRow.get("ID");

                        notif_list.add(new notif_variable_links(Notif_Type, Notif_Content, Notif_Data_1, Notif_Data_2,
                                Notif_Data_3, Notif_Date));

                    }




                    last_index_array = Resulting_Notif_List.size();
                    R_recycler_notifs.getAdapter().notifyDataSetChanged();





                }


            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(getContext(), json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(getContext(), json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(getContext(), finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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