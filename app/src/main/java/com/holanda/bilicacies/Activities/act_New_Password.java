package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holanda.bilicacies.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class act_New_Password extends AppCompatActivity {

    private ProgressDialog pDialog;

    Button btn_Change_Password;
    EditText txtin_New_Password, txtin_Confirm_Password;
    ImageView img_Show_New_Password, img_Show_Confirm_Password;
    TextView txt_Show_New_Password, txt_Show_Confirm_Password;

    String username_to_change, New_ToSave_Password, json_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        username_to_change = getIntent().getExtras().getString("newPassword_Username");

        btn_Change_Password = findViewById(R.id.newpword_btn_Change);
        txtin_New_Password = findViewById(R.id.newpword_txtin_New_Password);
        txtin_Confirm_Password = findViewById(R.id.newpword_txtin_Confirm_Password);
        img_Show_New_Password = findViewById(R.id.newpword_img_Show_New_Password);
        img_Show_Confirm_Password = findViewById(R.id.newpword_img_Show_Confirm_Password);
        txt_Show_New_Password = findViewById(R.id.newpword_txt_Show_New_Password);
        txt_Show_Confirm_Password = findViewById(R.id.newpword_txt_Show_Confirm_Password);

        txt_Show_New_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_New_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_Show_New_Password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_New_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_Show_New_Password.setText("Hide Password");
                }
                else{
                    img_Show_New_Password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_New_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_Show_New_Password.setText("Show Password");
                }




            }
        });

        img_Show_New_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_New_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_Show_New_Password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_New_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_Show_New_Password.setText("Hide Password");
                }
                else{
                    img_Show_New_Password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_New_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_Show_New_Password.setText("Show Password");
                }
            }
        });


        txt_Show_Confirm_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Confirm_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_Show_Confirm_Password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Confirm_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_Show_Confirm_Password.setText("Hide Password");
                }
                else{
                    img_Show_Confirm_Password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Confirm_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_Show_Confirm_Password.setText("Show Password");
                }

            }
        });

        img_Show_Confirm_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Confirm_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_Show_Confirm_Password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Confirm_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_Show_Confirm_Password.setText("Hide Password");
                }
                else{
                    img_Show_Confirm_Password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Confirm_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_Show_Confirm_Password.setText("Show Password");
                }
            }
        });


        btn_Change_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_password = txtin_New_Password.getText().toString();
                String confirm_password = txtin_Confirm_Password.getText().toString();

                if(new_password.equals(confirm_password)){

                    New_ToSave_Password = new_password;

                    //TODO Call Backend to change the password;


                    new Change_Password_with_Username().execute();


                }else{
                    txtin_New_Password.setError("Passwords do not match");
                    txtin_Confirm_Password.setError("Passwords do not match");
                }






            }
        });








    }




    class Change_Password_with_Username extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_New_Password.this);
            pDialog.setMessage("Contacting Server. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {

            if(internetConnectionAvailable(10000)){
                try {


                    String link="https://" + getString(R.string.Server_Web_Host_IP ) + "BILICACIES/ChangePassword.php";
                    HashMap<String,String> data_1 = new HashMap<>();
                    data_1.put("Ch_Username", username_to_change);
                    data_1.put("Ch_New_Password", New_ToSave_Password);


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

            }else {
                //Toast.makeText(act_Logging_User.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                json_message = "Please check your internet connection";

            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(json_message.equals("Password Update Successful")){

                //Request OTP

                Toast.makeText(act_New_Password.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();

                setResult(RESULT_OK, intent);
                finish();


            }else{



                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_New_Password.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Logging_User.this, "Server Error!\nException: 250", Toast.LENGTH_LONG).show();
            }
        }

    }



    private boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("www.google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
        return inetAddress!=null && !inetAddress.equals("");
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