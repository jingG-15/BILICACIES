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
import android.widget.ImageButton;
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

public class act_Password_Confirmation extends AppCompatActivity {

    EditText txtin_Password;
    ImageButton imgbtn_Show_Password;
    TextView txt_Show_Password;
    Button btn_Confirm;

    String pass_password, logged_username, json_message;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirmation);

        logged_username = getIntent().getExtras().getString("Logged_Username");

        txtin_Password = findViewById(R.id.pword_con_txtin_Password);
        imgbtn_Show_Password = findViewById(R.id.pword_con_img_Show_Password);
        txt_Show_Password = findViewById(R.id.pword_con_txt_Show_Password);
        btn_Confirm = findViewById(R.id.pword_con_btn_Confirm);


        imgbtn_Show_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    imgbtn_Show_Password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_Show_Password.setText("Hide Password");
                }
                else{
                    imgbtn_Show_Password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_Show_Password.setText("Show Password");
                }
            }
        });

        txt_Show_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    imgbtn_Show_Password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_Show_Password.setText("Hide Password");
                }
                else{
                    imgbtn_Show_Password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_Show_Password.setText("Show Password");
                }
            }
        });

        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String in_password = txtin_Password.getText().toString();

                if(in_password.isEmpty()){
                    txtin_Password.setError("Password cannot be empty");
                    Toast.makeText(act_Password_Confirmation.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();

                }else{

                    pass_password = in_password;


                    new Check_Credentials().execute();

                }


            }
        });







    }



    class Check_Credentials extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Password_Confirmation.this);
            pDialog.setMessage("Checking Credentials. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {

            if(internetConnectionAvailable(10000)){
                try {


                    String link="https://" + getString(R.string.Server_Web_Host_IP ) + "BILICACIES/CheckLoginCredentials.php";
                    HashMap<String,String> data_1 = new HashMap<>();
                    data_1.put("Log_Username", logged_username);
                    data_1.put("Log_Password", pass_password);


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
            if(json_message.equals("Match Found")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

                Toast.makeText(act_Password_Confirmation.this, "Confirmation Success!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();

                setResult(RESULT_OK, intent);
                finish();

            }else if(json_message.equals("Entry Mismatch")){

                Toast.makeText(act_Password_Confirmation.this, "Password mismatch. Please confirm.", Toast.LENGTH_LONG).show();

            }else{


                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Password_Confirmation.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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