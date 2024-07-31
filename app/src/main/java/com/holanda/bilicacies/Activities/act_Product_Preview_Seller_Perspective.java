package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class act_Product_Preview_Seller_Perspective extends AppCompatActivity {

    String pv_Product_ID, pv_Date_Added, pv_Photo_Count, pv_Price, pv_Product_Name, pv_Product_Desc;

    TextView txt_Product_Seq, txt_Product_Name, txt_Price, txt_Product_Desc, txt_Date_Added;

    ViewPager viewPager;
    ViewPagerAdapter myCustomPagerAdapter;


    ArrayList<HashMap<String, String>> Image_URL_List;

    ImageButton imgbtn_Delete_Product;

    private ProgressDialog pDialog;

    String json_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preview_seller_perspective);

        pv_Product_ID = getIntent().getExtras().getString("pv_Product_ID");
        pv_Date_Added = getIntent().getExtras().getString("pv_Date_Added");
        pv_Photo_Count = getIntent().getExtras().getString("pv_Photo_Count");
        pv_Price = getIntent().getExtras().getString("pv_Price");
        pv_Product_Name = getIntent().getExtras().getString("pv_Product_Name");
        pv_Product_Desc = getIntent().getExtras().getString("pv_Product_Desc");


        txt_Product_Seq = findViewById(R.id.pv_txt_Photo_Seq);

        txt_Date_Added = findViewById(R.id.pv_txt_Date_Added);
        txt_Price = findViewById(R.id.pv_txt_Price);
        txt_Product_Name = findViewById(R.id.pv_txt_Product_Name);
        txt_Product_Desc = findViewById(R.id.pv_txt_Description);

        imgbtn_Delete_Product = findViewById(R.id.prdusr_imgbtn_Delete_Product);

        try {
            String timeStamp = pv_Date_Added;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            //System.out.println("Unix timestamp: " + dateFormat.parse(timeStamp).getTime());
            SimpleDateFormat temp1 = new SimpleDateFormat("MMMM-dd-yyyy", Locale.getDefault());
            String month = temp1.format(dateFormat.parse(timeStamp));

            txt_Date_Added.setText(month);
        } catch (ParseException e) {
            txt_Date_Added.setText(pv_Date_Added);
            e.printStackTrace();

        }



        txt_Price.setText(pv_Price);
        txt_Product_Name.setText(pv_Product_Name);
        txt_Product_Desc.setText(pv_Product_Desc);



        txt_Product_Seq.setText("1/" + pv_Photo_Count);

        Image_URL_List = new ArrayList<>();


        for (int i = 0; i < Integer.parseInt(pv_Photo_Count); i++) {
            String curr_photo_seq = String.valueOf(i + 1);
            HashMap<String, String> result_arr = new HashMap<>();

            result_arr.put("URL", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/product_photos/" + pv_Product_ID + "_" + curr_photo_seq + ".png");

            Image_URL_List.add(result_arr);

        }



        viewPager = (ViewPager)findViewById(R.id.viewPager);
        myCustomPagerAdapter = new ViewPagerAdapter(this, Image_URL_List);
        viewPager.setAdapter(myCustomPagerAdapter);

        PageListener pageListener = new PageListener();
        viewPager.addOnPageChangeListener(pageListener);




        imgbtn_Delete_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(act_Product_Preview_Seller_Perspective.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to delete this product?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        new Delete_Selected_Product().execute();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();





            }
        });

    }


    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            String pageseq = (position + 1) + "/" + pv_Photo_Count;
            txt_Product_Seq.setText(pageseq);
        }
    }




    class Delete_Selected_Product extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Product_Preview_Seller_Perspective.this);
            pDialog.setMessage("Deleting Item. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/DeleteSpecifiedProduct.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Del_Product_ID", pv_Product_ID);
                data_1.put("Del_Photo_Count", pv_Photo_Count);

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

            if(json_message.equals("Data Delete Goods")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

                Intent intent = new Intent();


                setResult(2, intent);

                finish();

            }else if(json_message.equals("No Response from server")){
                Toast.makeText(act_Product_Preview_Seller_Perspective.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();


            }else if(json_message.equals("Try Again Err: 11")){
                //new Create_New_Profile().execute();
                Toast.makeText(act_Product_Preview_Seller_Perspective.this, "Internal Server Error 11." , Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Product_Preview_Seller_Perspective.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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