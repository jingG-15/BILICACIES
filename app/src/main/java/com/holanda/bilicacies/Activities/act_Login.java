package com.holanda.bilicacies.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.BounceInterpolator;
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

public class act_Login extends AppCompatActivity {

    Button btn_Login, btn_register;
    ImageView img_show_password, img_left, img_right, img_logo;
    TextView txt_show_password, txt_Forgot_Password;
    EditText txtin_Password, txtin_Username;

    String pass_password, pass_username;
    int REGISTER_REQUEST_CODE = 106;
    int CHANGEPASSWORD_REQUEST_CODE = 107;
    private ProgressDialog pDialog;

    String json_message;

    String logged_First_Name;
    String logged_Middle_Name;
    String logged_Last_Name;
    String logged_Mobile_Number;
    String logged_Gender;
    String logged_Birthday;
    String logged_Municipality;
    String logged_Barangay;
    String logged_Extra_Address;
    String logged_Username;
    String logged_Password;
    String logged_Date_Registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btn_Login = findViewById(R.id.log_btn_login);

        img_show_password = findViewById(R.id.log_img_show_password);
        txt_show_password = findViewById(R.id.log_txt_show_password);
        txtin_Password = findViewById(R.id.log_txtin_Password);
        img_left = findViewById(R.id.log_img_left);
        img_right = findViewById(R.id.log_img_right);
        img_logo = findViewById(R.id.log_img_Logo);
        btn_register = findViewById(R.id.log_btn_register);
        txtin_Username = findViewById(R.id.log_txtin_Username);
        txt_Forgot_Password = findViewById(R.id.log_txt_forgot_password);




        img_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_show_password.setText("Hide Password");
                }
                else{
                    img_show_password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_show_password.setText("Show Password");
                }
            }
        });

        txt_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_password.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_show_password.setText("Hide Password");
                }
                else{
                    img_show_password.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_show_password.setText("Show Password");
                }
            }
        });


        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String in_username = txtin_Username.getText().toString();
                String in_password = txtin_Password.getText().toString();

                if(in_password.isEmpty() || in_username.isEmpty()){
                    if(in_password.isEmpty()){
                        txtin_Password.setError("Password cannot be empty");
                        Toast.makeText(act_Login.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }

                    if(in_username.isEmpty()){
                        txtin_Username.setError("Username cannot be empty");
                        Toast.makeText(act_Login.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();

                    }
                }else{

                    pass_password = in_password;
                    pass_username = in_username;

                    new Check_Username_Validity().execute();

                }


//                Intent intent = new Intent();
//
//
//                setResult(RESULT_OK, intent);
//                finish();


            }
        });

        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator anim_1 = (ObjectAnimator) AnimatorInflater.loadAnimator(act_Login.this, R.animator.flip);
                anim_1.setTarget( img_logo);
                anim_1.setDuration(1000);
                anim_1.setInterpolator(new BounceInterpolator());
//                anim_1.setRepeatCount(ValueAnimator.INFINITE);
                anim_1.start();


            }
        });

        img_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator anim_1 = (ObjectAnimator) AnimatorInflater.loadAnimator(act_Login.this, R.animator.flip_rev);
                anim_1.setTarget( img_logo);
                anim_1.setDuration(1000);
                anim_1.setInterpolator(new BounceInterpolator());
//                anim_1.setRepeatCount(ValueAnimator.INFINITE);
                anim_1.start();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent registerAct = new Intent(getApplicationContext(), act_Registration.class);
                startActivityForResult(registerAct, REGISTER_REQUEST_CODE);


            }

        });

        txt_Forgot_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerAct = new Intent(getApplicationContext(), act_Password_Recovery.class);
                startActivityForResult(registerAct, CHANGEPASSWORD_REQUEST_CODE);



            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REGISTER_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                txtin_Username.setText(data.getStringExtra("Save_Username"));


            }


        }else if(requestCode == CHANGEPASSWORD_REQUEST_CODE){

            if(resultCode == RESULT_OK){
                txtin_Username.setText(data.getStringExtra("Save_Username"));


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



    class Check_Username_Validity extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Login.this);
            pDialog.setMessage("Logging in. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {

            if(internetConnectionAvailable(10000)){
                try {


                    String link="https://" + getString(R.string.Server_Web_Host_IP ) + "BILICACIES/CheckLoginCredentials.php";
                    HashMap<String,String> data_1 = new HashMap<>();
                    data_1.put("Log_Username", pass_username);
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




                        if(json_message.equals("Match Found")){
                            logged_First_Name = obj.getString("First_Name");
                            logged_Middle_Name = obj.getString("Middle_Name");
                            logged_Last_Name = obj.getString("Last_Name");
                            logged_Mobile_Number = obj.getString("Mobile_Number");
                            logged_Gender = obj.getString("Gender");
                            logged_Birthday = obj.getString("Birthday");
                            logged_Municipality = obj.getString("Municipality");
                            logged_Barangay = obj.getString("Barangay");
                            logged_Extra_Address = obj.getString("Extra_Address");
                            logged_Username = obj.getString("Username");
                            logged_Password = obj.getString("Password");
                            logged_Date_Registered = obj.getString("Date_Registered");

                        }


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

                Toast.makeText(act_Login.this, "Login Success!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();

                intent.putExtra("Log_First_Name", logged_First_Name);
                intent.putExtra("Log_Middle_Name", logged_Middle_Name);
                intent.putExtra("Log_Last_Name", logged_Last_Name);
                intent.putExtra("Log_Mobile_Number", logged_Mobile_Number);
                intent.putExtra("Log_Gender", logged_Gender);
                intent.putExtra("Log_Birthday", logged_Birthday);
                intent.putExtra("Log_Municipality", logged_Municipality);
                intent.putExtra("Log_Barangay", logged_Barangay);
                intent.putExtra("Log_Extra_Address", logged_Extra_Address);
                intent.putExtra("Log_Username", logged_Username);
                intent.putExtra("Log_Password", logged_Password);
                intent.putExtra("Log_Date_Registered", logged_Date_Registered);


                setResult(RESULT_OK, intent);
                finish();



            }else if(json_message.equals("Entry Mismatch")){

                Toast.makeText(act_Login.this, "Username or Password mismatch. Please check your input", Toast.LENGTH_LONG).show();


            }else{


                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Login.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Logging_User.this, "Server Error!\nException: 250", Toast.LENGTH_LONG).show();
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