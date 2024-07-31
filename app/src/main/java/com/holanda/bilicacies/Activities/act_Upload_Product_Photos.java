package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.holanda.bilicacies.Adapters.MyAdapter;
import com.holanda.bilicacies.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class act_Upload_Product_Photos extends AppCompatActivity {

    private static final String TAG = "ErrorJay";

    GridView gridPhotosAdded;
    Button btnSave;
    ImageView imgBrowsePhoto;

    ArrayList<Uri> arrayList;


    int REQUEST_CODE_READ_STORAGE = 100;

    private ProgressDialog pDialog_1;
    private ProgressDialog pDialog_2;


    String Product_Name, Product_Desc, Product_Price, Logged_Username, Resulting_Product_ID;

    String json_message;

    int upd_seq = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product_photos);

        imgBrowsePhoto = findViewById(R.id.produpd_img_add_photo);
        gridPhotosAdded = findViewById(R.id.produpd_gridPhotosAdded);
        btnSave = findViewById(R.id.produpd_btn_Save);


        Product_Name = getIntent().getExtras().getString("Upd_Product_Name");
        Product_Desc = getIntent().getExtras().getString("Upd_Product_Desc");
        Product_Price = getIntent().getExtras().getString("Upd_Product_Price");
        Logged_Username = getIntent().getExtras().getString("Logged_Username");


        arrayList = new ArrayList<>();

        imgBrowsePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(act_Upload_Product_Photos.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                        showChooser();

                    }else {

                        ActivityCompat.requestPermissions(act_Upload_Product_Photos.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, 101);

                    }
                } else {
                    showChooser();

                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(act_Upload_Product_Photos.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(act_Upload_Product_Photos.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    if(!arrayList.isEmpty()){


                        btnSave.setEnabled(false);

                        new Query_Server_Product_ID().execute();


                    }else{

                        Toast.makeText(act_Upload_Product_Photos.this, "Images cannot be empty!", Toast.LENGTH_LONG).show();

                    }



                }else {

                    ActivityCompat.requestPermissions(act_Upload_Product_Photos.this, new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 102);

                }


            }
        });



    }



    private void showChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*/*");
        intent.setType("video/*, image/*");
        String[] mimetypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_READ_STORAGE);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_READ_STORAGE) {
                if (resultData != null) {

                    if (resultData.getClipData() != null) {

                        int count = resultData.getClipData().getItemCount();
                        int currentItem = 0;

                        while (currentItem < count) {
                            Uri imageUri = resultData.getClipData().getItemAt(currentItem).getUri();
                            currentItem = currentItem + 1;

                            Log.d("Uri Selected", imageUri.toString());

                            try {
                                arrayList.add(imageUri);
                                MyAdapter mAdapter = new MyAdapter(this, arrayList);
                                mAdapter.setArrayList(arrayList);

                                gridPhotosAdded.setAdapter(mAdapter);


                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }

                    } else if (resultData.getData() != null) {

                        final Uri uri = resultData.getData();
                        Log.i(TAG, "Uri = " + uri.toString());

                        try {

                            arrayList.add(uri);
                            Log.i(TAG, "Uri = " + arrayList.toString());
                            MyAdapter mAdapter = new MyAdapter(this, arrayList);
                            mAdapter.setArrayList(arrayList);

                            gridPhotosAdded.setAdapter(mAdapter);

                        } catch (Exception e) {

                            Log.e(TAG, "File select error", e);

                        }
                    }
                }
            }
        }
    }




    class Query_Server_Product_ID extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();



            pDialog_1 = new ProgressDialog(act_Upload_Product_Photos.this);
            pDialog_1.setMessage("Contacting Server. Please wait.");
            pDialog_1.setIndeterminate(false);
            pDialog_1.setCancelable(false);
            pDialog_1.show();

        }


        protected String doInBackground(String... args) {



            try {

                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/NewProductListing.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("P_Product_Name",Product_Name);
                data_1.put("P_Product_Price",Product_Price);
                data_1.put("P_Product_Desc",Product_Desc);
                data_1.put("P_User_Assigned",Logged_Username);


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

                    if(json_message.equals("Data Submit Successfully")){

                        Resulting_Product_ID = obj.getString("result_prod_id");

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


            if(json_message.equals("Data Submit Successfully")){


//                Toast.makeText(act_Upload_Product_Photos.this, "Data Submission Success!", Toast.LENGTH_LONG).show();
//
//                Intent intent = new Intent();
//
//                setResult(RESULT_OK, intent);
//                finish();


                upd_seq = 1;

                pDialog_1.dismiss();
                pDialog_2 = new ProgressDialog(act_Upload_Product_Photos.this);
                //Set the progress dialog to display a horizontal progress bar
                pDialog_2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //Set the dialog title to 'Loading...'
                pDialog_2.setTitle("Uploading.");
                //Set the dialog message to 'Loading application View, please wait...'
                pDialog_2.setMessage("Uploading Photos. Please wait...");
                //This dialog can't be canceled by pressing the back key
                pDialog_2.setCancelable(false);
                //This dialog isn't indeterminate
                pDialog_2.setIndeterminate(false);
                //The maximum number of items is 100
                pDialog_2.setMax(arrayList.size());
                //Set the current progress to zero
                pDialog_2.setProgress(0);
                //Display the progress dialog
                pDialog_2.show();

                new Upload_New_Product_1().execute();

            }else if(json_message.equals("No Response from server")){
                Toast.makeText(act_Upload_Product_Photos.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
                pDialog_1.dismiss();

            }else if(json_message.equals("Try Again Err: 1")){
                //new Create_New_Profile().execute();
                Toast.makeText(act_Upload_Product_Photos.this, "Internal Server Error 10." , Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
                pDialog_1.dismiss();
            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Upload_Product_Photos.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
                pDialog_1.dismiss();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }











    class Upload_New_Product_1 extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        protected String doInBackground(String... args) {



            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(act_Upload_Product_Photos.this.getContentResolver(), arrayList.get(upd_seq - 1));

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                String encodedImage_Prod_Photo = Base64.encodeToString(byteArray, Base64.DEFAULT);


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/UploadProductPhotos.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("U_Assigned_Username",Logged_Username);
                data_1.put("U_Photo_Name",Resulting_Product_ID);
                data_1.put("U_Photo_Sequence", String.valueOf(upd_seq));
                data_1.put("U_Photo_Data",encodedImage_Prod_Photo);

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


            if(json_message.equals("Data Submit Successfully")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();


                if(upd_seq < arrayList.size()){
                    pDialog_2.setProgress(upd_seq);
                    Log.i("ProgressDebug", String.valueOf(upd_seq));

                    upd_seq = upd_seq + 1;

                    new Upload_New_Product_2().execute();


                }else{
                    pDialog_2.setProgress(upd_seq);

                    pDialog_2.dismiss();

                    Toast.makeText(act_Upload_Product_Photos.this, "Product Added.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();


                }







            }else if(json_message.equals("No Response from server")){
                pDialog_2.dismiss();
                Toast.makeText(act_Upload_Product_Photos.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);

            }else if(json_message.equals("Try Again Err: 10")){
                pDialog_2.dismiss();
                //new Create_New_Profile().execute();
                Toast.makeText(act_Upload_Product_Photos.this, "Internal Server Error 10." , Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
            }else{
                pDialog_2.dismiss();
                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Upload_Product_Photos.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    class Upload_New_Product_2 extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        protected String doInBackground(String... args) {



            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(act_Upload_Product_Photos.this.getContentResolver(), arrayList.get(upd_seq - 1));

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                String encodedImage_Prod_Photo = Base64.encodeToString(byteArray, Base64.DEFAULT);


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/UploadProductPhotos.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("U_Assigned_Username",Logged_Username);
                data_1.put("U_Photo_Name",Resulting_Product_ID);
                data_1.put("U_Photo_Sequence", String.valueOf(upd_seq));
                data_1.put("U_Photo_Data",encodedImage_Prod_Photo);

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


            if(json_message.equals("Data Submit Successfully")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();


                if(upd_seq < arrayList.size()){


                    pDialog_2.setProgress(upd_seq);
                    Log.i("ProgressDebug", String.valueOf(upd_seq));

                    upd_seq = upd_seq + 1;

                    new Upload_New_Product_1().execute();


                }else{

                    pDialog_2.setProgress(upd_seq);

                    pDialog_2.dismiss();

                    Toast.makeText(act_Upload_Product_Photos.this, "Product Added.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();


                }







            }else if(json_message.equals("No Response from server")){
                pDialog_2.dismiss();
                Toast.makeText(act_Upload_Product_Photos.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);

            }else if(json_message.equals("Try Again Err: 10")){
                pDialog_2.dismiss();
                //new Create_New_Profile().execute();
                Toast.makeText(act_Upload_Product_Photos.this, "Internal Server Error 10." , Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
            }else{
                pDialog_2.dismiss();
                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Upload_Product_Photos.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);


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