package com.holanda.bilicacies.Fragments.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.holanda.bilicacies.Activities.act_Buyer_Product_View;
import com.holanda.bilicacies.Activities.act_Cart_View;
import com.holanda.bilicacies.Activities.act_Product_per_Category_User;
import com.holanda.bilicacies.Adapters.home_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.home_sell_item_var_link;
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

public class HomeFragment extends Fragment {


    final String PREFS_NAME = "BilicaciesPrefFile";
    String version, logged_username, json_message;

    String last_ID;
    int last_index_array;
    int Load_times;
    ArrayList<HashMap<String, String>> Resulting_Product_List;
    ArrayList<home_sell_item_var_link> prod_list;
    SwipeRefreshLayout recyclerHolder;

    String Last_DB_ID;
    RecyclerView rec_product_list;
    int selected_pos_seller_perspective;

    GridLayoutManager mGridLayoutmanager;

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    EditText home_Search_Term;
    ImageView home_img_Search_Button, home_img_Cart_Button;



//    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        rec_product_list = root.findViewById(R.id.home_recycler_view);
        //progBar = findViewById(R.id.sell_progressbar);
        //progBar.setVisibility(View.GONE);

//        txtinSearchTerm = findViewById(R.id.sell_txtin_Search_Term);
//        imgSearchbtn = findViewById(R.id.sell_img_Search_Product);
//        txtEndofProd = findViewById(R.id.sell_txt_End_of_Prod);
        recyclerHolder = root.findViewById(R.id.home_swipe_ref);


        home_img_Search_Button = root.findViewById(R.id.home_img_Search);
        home_Search_Term = root.findViewById(R.id.home_txtin_Search_Term);
        home_img_Cart_Button = root.findViewById(R.id.home_img_Cart);

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



            mGridLayoutmanager = new GridLayoutManager(getContext(), 2);
            rec_product_list.setLayoutManager(mGridLayoutmanager);



            last_index_array = 0;
            last_ID = "0";
            Load_times = 1;
            Resulting_Product_List = new ArrayList<>();
            prod_list = new ArrayList<>();
            //txtEndofProd.setVisibility(View.INVISIBLE);
            recyclerHolder.setRefreshing(true);
            new Load_Home_Screen().execute();

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
                        if((Load_times >= 1) && (Resulting_Product_List.size() >= (30 * Load_times))){
                            Load_times = Load_times + 1;
                            new Load_Next_Sell_Products().execute();

                        }else{
                            //txtEndofProd.setVisibility(View.VISIBLE);


                        }







                        loading = true;
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
                    //txtEndofProd.setVisibility(View.INVISIBLE);
                    recyclerHolder.setRefreshing(true);
                    new Load_Home_Screen().execute();




                }
            });


            home_Search_Term.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        switch (keyCode)
                        {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:

                                String search_term = home_Search_Term.getText().toString().trim();

                                if(search_term.isEmpty() || search_term.equals("") || search_term == null){

                                    Toast.makeText(getContext(), "Search box cannot be empty", Toast.LENGTH_LONG).show();

                                }else{

                                    Intent ViewSpecificProduct = new Intent(getContext(), act_Product_per_Category_User.class);

                                    ViewSpecificProduct.putExtra("PPCU_Title", "Search Result for '" + search_term + "'");
                                    ViewSpecificProduct.putExtra("PPCU_Search_Type", "Name_Search");
                                    ViewSpecificProduct.putExtra("PPCU_Search_Term", search_term);

                                    startActivity(ViewSpecificProduct);

                                }

                                return true;
                            default:
                                break;
                        }
                    }

                    return false;
                }
            });

            home_img_Search_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String search_term = home_Search_Term.getText().toString().trim();

                    if(search_term.isEmpty() || search_term.equals("") || search_term == null){

                        Toast.makeText(getContext(), "Search box cannot be empty", Toast.LENGTH_LONG).show();

                    }else{

                        Intent ViewSpecificProduct = new Intent(getContext(), act_Product_per_Category_User.class);

                        ViewSpecificProduct.putExtra("PPCU_Title", "Search Result for '" + search_term + "'");
                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Name_Search");
                        ViewSpecificProduct.putExtra("PPCU_Search_Term", search_term);

                        startActivity(ViewSpecificProduct);

                    }


                }
            });

            home_img_Cart_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent ViewCart = new Intent(getContext(), act_Cart_View.class);

                    ViewCart.putExtra("Cart_Current_Username", logged_username);

                    startActivity(ViewCart);



                }
            });


        }

        return root;
    }





    class Load_Home_Screen extends AsyncTask<String, String, String> {
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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadProcductsHomeScreen.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("S_Last_ID", last_ID);
                data_1.put("S_First_Load_Flag", "True");

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

                        result_arr_temp.put("ID", "Biliran_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Biliran_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Biliran_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        // adding contact to contact list
                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Naval_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Naval_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Naval_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Almeria_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Almeria_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Almeria_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Kawayan_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Kawayan_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Kawayan_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);


                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Culaba_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Culaba_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Culaba_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Caibiran_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Caibiran_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Caibiran_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);


                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Cabucgayan_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Cabucgayan_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Cabucgayan_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);


                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Maripipi_Delicacies_011593");
                        result_arr_temp.put("Product_ID", "Maripipi_Delicacies_011593");
                        result_arr_temp.put("Product_Name", "Maripipi_Delicacies_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Spacer_011593");
                        result_arr_temp.put("Product_ID", "Spacer_011593");
                        result_arr_temp.put("Product_Name", "Spacer_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Spacer_011593");
                        result_arr_temp.put("Product_ID", "Spacer_011593");
                        result_arr_temp.put("Product_Name", "Spacer_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Product_Like_1_011593");
                        result_arr_temp.put("Product_ID", "Product_Like_1_011593");
                        result_arr_temp.put("Product_Name", "Product_Like_1_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Product_Like_2_011593");
                        result_arr_temp.put("Product_ID", "Product_Like_2_011593");
                        result_arr_temp.put("Product_Name", "Product_Like_2_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Spacer_011593");
                        result_arr_temp.put("Product_ID", "Spacer_011593");
                        result_arr_temp.put("Product_Name", "Spacer_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);

                        result_arr_temp = new HashMap<>();

                        result_arr_temp.put("ID", "Spacer_011593");
                        result_arr_temp.put("Product_ID", "Spacer_011593");
                        result_arr_temp.put("Product_Name", "Spacer_011593");
                        result_arr_temp.put("Product_Price", " ");
                        result_arr_temp.put("Date_Added", " ");
                        result_arr_temp.put("Photo_Count", "1");
                        result_arr_temp.put("Product_Description"," ");

                        Resulting_Product_List.add(result_arr_temp);


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

                        String prod_Price;
                        String prod_Date_Added;
                        String prod_Cover_URL;
                        String prod_ID;
                        String prod_Photo_Count;
                        String prod_Product_Desc;


                        HashMap<String, String> currentRow = Resulting_Product_List.get(i);

                        String prod_Name = currentRow.get("Product_Name");


                        if(prod_Name.equals("Biliran_Delicacies_011593")){

                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Biliran_Delicacies_011593";
                            prod_ID = "Biliran_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Naval_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Naval_Delicacies_011593";
                            prod_ID = "Naval_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Almeria_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Almeria_Delicacies_011593";
                            prod_ID = "Almeria_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Kawayan_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Kawayan_Delicacies_011593";
                            prod_ID = "Kawayan_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Culaba_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Culaba_Delicacies_011593";
                            prod_ID = "Culaba_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Caibiran_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Caibiran_Delicacies_011593";
                            prod_ID = "Caibiran_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Cabucgayan_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Cabucgayan_Delicacies_011593";
                            prod_ID = "Cabucgayan_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else if(prod_Name.equals("Maripipi_Delicacies_011593")){


                            prod_Price = " ";
                            prod_Date_Added = " ";
                            prod_Cover_URL = "Maripipi_Delicacies_011593";
                            prod_ID = "Maripipi_Delicacies_011593";
                            prod_Photo_Count = "1";
                            prod_Product_Desc = " ";

                        }else{
                            prod_Price = currentRow.get("Product_Price");
                            prod_Date_Added = currentRow.get("Date_Added");
                            prod_Cover_URL = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + currentRow.get("Product_ID") + "_1.png";
                            prod_ID = currentRow.get("Product_ID");
                            prod_Photo_Count = currentRow.get("Photo_Count");
                            prod_Product_Desc = currentRow.get("Product_Description");


                        }





                        Last_DB_ID = currentRow.get("Product_ID");

                        prod_list.add(new home_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    rec_product_list.setAdapter(new home_Recycler_Adapter(getContext(), prod_list, new home_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {

                            selected_pos_seller_perspective = position;



                            if(position >=0 && position < 8){
                                Intent ViewSpecificProduct = new Intent(getContext(), act_Product_per_Category_User.class);
                                switch (position){


                                    case 0:



                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Biliran");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Biliran");

                                        startActivity(ViewSpecificProduct);


                                        break;
                                    case 1:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Naval");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Naval");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                    case 2:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Almeria");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Almeria");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                    case 3:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Kawayan");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Kawayan");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                    case 4:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Culaba");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Culaba");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                    case 5:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Caibiran");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Caibiran");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                    case 6:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Cabucgayan");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Cabucgayan");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                    case 7:

                                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from the Town of Maripipi");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Category");
                                        ViewSpecificProduct.putExtra("PPCU_Search_Term", "Maripipi");

                                        startActivity(ViewSpecificProduct);

                                        break;
                                }



                            }else if(position > 13){

                                Intent ViewSpecificProduct = new Intent(getContext(), act_Buyer_Product_View.class);

                                ViewSpecificProduct.putExtra("bv_Product_ID", prod_list.get(position).getProdID());
                                ViewSpecificProduct.putExtra("bv_Date_Added", prod_list.get(position).getDate());
                                ViewSpecificProduct.putExtra("bv_Photo_Count", prod_list.get(position).getPhotoCount());
                                ViewSpecificProduct.putExtra("bv_Price", prod_list.get(position).getPrice());
                                ViewSpecificProduct.putExtra("bv_Product_Name", prod_list.get(position).getProductName());
                                ViewSpecificProduct.putExtra("bv_Product_Desc", prod_list.get(position).getProductDesc());
                                ViewSpecificProduct.putExtra("bv_Logged_Username", logged_username);

                                startActivity(ViewSpecificProduct);

                            }





                        }
                    }));


                    last_index_array = Resulting_Product_List.size() ;





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

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadProcductsHomeScreen.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("S_Last_ID", Last_DB_ID);
                data_1.put("S_First_Load_Flag", "False");

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

                        prod_list.add(new home_sell_item_var_link(prod_Name, prod_Price, prod_Date_Added, prod_Cover_URL, prod_ID, prod_Photo_Count, prod_Product_Desc));



                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list);
//                    rec_product_list.setAdapter(prod_adapter);
                    rec_product_list.getAdapter().notifyDataSetChanged();


                    last_index_array = Resulting_Product_List.size() - 1;



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