package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class act_Profile_Preview extends AppCompatActivity {


    String UsernameToView, json_message;


    String f_First_Name, f_Middle_Name, f_Last_Name, f_Mobile_Number, f_Gender, f_Birthday,
            f_Municipality, f_Barangay, f_Extra_Address, f_Date_Reg, f_Trans_Success,
            f_Trans_Unsuccess, f_Trans_Denied, f_Trans_Total, f_Total_Products;

    ImageView img_Cover_Photo, img_Profile_Photo, img_Valid_ID, img_View_Products_Arrow, img_Gender_Indic;
    TextView txt_Fullname, txt_Address, txt_Registration_Date, txt_Phone_Number, txt_Age,
            txt_Trans_Success, txt_Trans_Unsuccess, txt_Trans_Denied, txt_View_Products,
            txt_Total_Trans, txt_Total_Products;

    ConstraintLayout layout_container;
    SwipeRefreshLayout layout_Swipe_Refresher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview);

        UsernameToView = getIntent().getExtras().getString("profv_Username");

        img_Cover_Photo = findViewById(R.id.profv_img_Cover_Photo);
        img_Profile_Photo = findViewById(R.id.profv_img_profile_photo);
        img_Valid_ID = findViewById(R.id.profv_img_Valid_ID);
        img_View_Products_Arrow = findViewById(R.id.profv_img_View_Products_Arrow);
        img_Gender_Indic = findViewById(R.id.profv_img_Gender_Indicator);

        txt_Fullname = findViewById(R.id.profv_txt_Full_Name);
        txt_Address = findViewById(R.id.profv_txt_Address);
        txt_Registration_Date = findViewById(R.id.profv_txt_Date_of_registration);
        txt_Phone_Number = findViewById(R.id.profv_txt_Contact_Number);
        txt_Age = findViewById(R.id.profv_txt_Age);
        txt_Trans_Success = findViewById(R.id.profv_txt_Trans_Success);
        txt_Trans_Unsuccess = findViewById(R.id.profv_txt_Trans_Unsuccess);
        txt_Trans_Denied = findViewById(R.id.profv_txt_Trans_Denied);
        txt_View_Products = findViewById(R.id.profv_txt_View_Products);
        txt_Total_Trans = findViewById(R.id.profv_txt_Total_Transactions);
        txt_Total_Products = findViewById(R.id.profv_txt_Product_Count);


        layout_container = findViewById(R.id.profv_cons_Layout_Container);
        layout_Swipe_Refresher = findViewById(R.id.profv_Swipe_Refresher);

        layout_container.setVisibility(View.INVISIBLE);
        layout_Swipe_Refresher.setRefreshing(true);


        new Load_Profile_Details().execute();








        layout_Swipe_Refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                layout_container.setVisibility(View.INVISIBLE);
                layout_Swipe_Refresher.setRefreshing(true);


                new Load_Profile_Details().execute();




            }
        });







    }


    class Load_Profile_Details extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();





        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/GetProfileDetailsFull.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("FP_Username", UsernameToView);

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


                        f_First_Name = obj.getString("First_Name");
                        f_Middle_Name = obj.getString("Middle_Name");
                        f_Last_Name = obj.getString("Last_Name");
                        f_Mobile_Number = obj.getString("Mobile_Number");
                        f_Gender = obj.getString("Gender");
                        f_Birthday = obj.getString("Birthday");
                        f_Municipality = obj.getString("Municipality");
                        f_Barangay = obj.getString("Barangay");
                        f_Extra_Address = obj.getString("Extra_Address");
                        f_Date_Reg = obj.getString("Date_Registered");
                        f_Trans_Success = obj.getString("Trans_Success");
                        f_Trans_Denied = obj.getString("Trans_Denied");
                        f_Trans_Unsuccess = obj.getString("Trans_Unsuccessful");
                        f_Trans_Total = obj.getString("Trans_Total");

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

                new Load_Product_Count_Profile_View().execute();


                Glide.with(act_Profile_Preview.this)
                        .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" +
                                UsernameToView + "_profile.png")
                        .circleCrop()
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(img_Profile_Photo);

                Glide.with(act_Profile_Preview.this)
                        .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/cover_photos/" +
                                UsernameToView + "_cover.png")
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(img_Cover_Photo);

                Glide.with(act_Profile_Preview.this)
                        .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/ID_photos/" +
                                UsernameToView + "_ID.png")
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                        .into(img_Valid_ID);



                txt_Fullname.setText(new StringBuilder().append(f_Last_Name).append(", " +
                        "").append(f_First_Name).append(" " +
                        "").append(f_Middle_Name.substring(0, 1)).append(".").toString());

                txt_Address.setText(new StringBuilder().append(f_Extra_Address).append(", " +
                        "").append(f_Barangay).append(", " +
                        "").append(f_Municipality).toString());

                SimpleDateFormat dateFormat_reg = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date_reg = dateFormat_reg.parse(f_Date_Reg);
                    String datename = (String) android.text.format.DateFormat.format("MMMM dd, yyyy", date_reg);
                    txt_Registration_Date.setText(datename);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                txt_Phone_Number.setText(f_Mobile_Number);

                String strDate = f_Birthday;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date_sel = dateFormat.parse(strDate);
                    txt_Age.setText(String.valueOf(getDiffYears(date_sel, Calendar.getInstance().getTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(act_Profile_Preview.this, "Selected: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


                txt_Total_Trans.setText(f_Trans_Total);


                txt_Total_Products.setText("----");


                //TODO set onClickListener on Viewing All Products Sold of User

                txt_Trans_Success.setText(f_Trans_Success);
                txt_Trans_Unsuccess.setText(f_Trans_Unsuccess);
                txt_Trans_Denied.setText(f_Trans_Denied);

                if(f_Gender.equals("Male")){
                    img_Gender_Indic.setImageResource(R.drawable.ic_male);

                }else{
                    img_Gender_Indic.setImageResource(R.drawable.ic_female);

                }


                layout_container.setVisibility(View.VISIBLE);
                layout_Swipe_Refresher.setRefreshing(false);



                img_Valid_ID.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent SinglePhotoPreview = new Intent(act_Profile_Preview.this, act_Single_Photo_Fullscreen.class);

                        SinglePhotoPreview.putExtra("SPP_Media_Type", "URL");
                        SinglePhotoPreview.putExtra("SPP_Media_Data", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/ID_photos/" +
                                UsernameToView + "_ID.png");

                        startActivity(SinglePhotoPreview);



                    }
                });

                img_Cover_Photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent SinglePhotoPreview = new Intent(act_Profile_Preview.this, act_Single_Photo_Fullscreen.class);

                        SinglePhotoPreview.putExtra("SPP_Media_Type", "URL");
                        SinglePhotoPreview.putExtra("SPP_Media_Data", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/cover_photos/" +
                                UsernameToView + "_cover.png");

                        startActivity(SinglePhotoPreview);

                    }
                });

                img_Profile_Photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent SinglePhotoPreview = new Intent(act_Profile_Preview.this, act_Single_Photo_Fullscreen.class);

                        SinglePhotoPreview.putExtra("SPP_Media_Type", "URL");
                        SinglePhotoPreview.putExtra("SPP_Media_Data", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" +
                                UsernameToView + "_profile.png");

                        startActivity(SinglePhotoPreview);

                    }
                });


                txt_View_Products.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ViewSpecificProduct = new Intent(act_Profile_Preview.this, act_Product_per_Category_User.class);

                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from " + new StringBuilder().append(f_Last_Name).append(", " +
                                "").append(f_First_Name).append(" " +
                                "").append(f_Middle_Name.substring(0, 1)).append(".").toString() + "'s Store");
                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Username_Specific");
                        ViewSpecificProduct.putExtra("PPCU_Search_Term", UsernameToView);
                        ViewSpecificProduct.putExtra("PPCU_Fullname_Only", new StringBuilder().append(f_Last_Name).append(", " +
                                "").append(f_First_Name).append(" " +
                                "").append(f_Middle_Name.substring(0, 1)).append(".").toString());

                        startActivity(ViewSpecificProduct);

                    }
                });


                img_View_Products_Arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ViewSpecificProduct = new Intent(act_Profile_Preview.this, act_Product_per_Category_User.class);

                        ViewSpecificProduct.putExtra("PPCU_Title", "Showing products from " + new StringBuilder().append(f_Last_Name).append(", " +
                                "").append(f_First_Name).append(" " +
                                "").append(f_Middle_Name.substring(0, 1)).append(".").toString() + "'s Store");
                        ViewSpecificProduct.putExtra("PPCU_Search_Type", "Username_Specific");
                        ViewSpecificProduct.putExtra("PPCU_Search_Term", UsernameToView);
                        ViewSpecificProduct.putExtra("PPCU_Fullname_Only", new StringBuilder().append(f_Last_Name).append(", " +
                                "").append(f_First_Name).append(" " +
                                "").append(f_Middle_Name.substring(0, 1)).append(".").toString());

                        startActivity(ViewSpecificProduct);


                    }
                });


            }else if(json_message.equals("User Not Found")){
                Toast.makeText(act_Profile_Preview.this, json_message, Toast.LENGTH_LONG).show();



            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Profile_Preview.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class Load_Product_Count_Profile_View extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();




            txt_Total_Products.setText("----");


        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/GetProductCountSeller.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("G_Username", UsernameToView);

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


                            if(c.getString("User_Assigned").equals(UsernameToView)){

                                f_Total_Products = c.getString("TotalProds");
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

                txt_Total_Products.setText(f_Total_Products);


            }else if(json_message.equals("Internal Server Error 404")){

                Toast.makeText(act_Profile_Preview.this, json_message, Toast.LENGTH_LONG).show();


            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Profile_Preview.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }


}