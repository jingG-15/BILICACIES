package com.holanda.bilicacies.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class act_Password_Recovery extends AppCompatActivity {

    final int OTP_RQUEST = 101;
    final int PASSWORD_CHANGE_REQ = 102;

    EditText txtin_Username;
    Button btn_Confirm;

    private ProgressDialog pDialog;

    String Username_Given_Global, json_message, Contact_Replied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        txtin_Username = findViewById(R.id.pword_reco_txtin_Username);
        btn_Confirm = findViewById(R.id.pword_reco_btn_Confirm);




        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Username_Given = txtin_Username.getText().toString().trim();

                if(Username_Given.isEmpty()){

                    Toast.makeText(act_Password_Recovery.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();


                }else{

                    Username_Given_Global = Username_Given;
                    new Username_Checker_Existing().execute();


                }





            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode == OTP_RQUEST){
            if(resultCode == RESULT_OK){

                if (ContextCompat.checkSelfPermission(act_Password_Recovery.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Password_Recovery.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){




                    //TODO Require new password

                    Intent verificationAct = new Intent(getApplicationContext(), act_New_Password.class);

                    verificationAct.putExtra("newPassword_Username", Username_Given_Global);

                    startActivityForResult(verificationAct, PASSWORD_CHANGE_REQ);




                }else {
                    ActivityCompat.requestPermissions(act_Password_Recovery.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 107);


                }

            }

        }else if(requestCode == PASSWORD_CHANGE_REQ){
           if(resultCode == RESULT_OK){
               Toast.makeText(act_Password_Recovery.this, "Data Submission Success!", Toast.LENGTH_LONG).show();

               Intent intent = new Intent();

               intent.putExtra("Save_Username", Username_Given_Global);


               setResult(RESULT_OK, intent);
               finish();

           }






       }



    }




    class Username_Checker_Existing extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Password_Recovery.this);
            pDialog.setMessage("Verifying Username. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {

            if(internetConnectionAvailable(10000)){
                try {


                    String link="https://" + getString(R.string.Server_Web_Host_IP ) + "BILICACIES/CheckUsernameGetContact.php";
                    HashMap<String,String> data_1 = new HashMap<>();
                    data_1.put("Q_Username", Username_Given_Global);


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
                        Contact_Replied = obj.getString("Contact_Number");

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

                //Request OTP


                Intent verificationAct = new Intent(getApplicationContext(), act_Reg_Phone_Verification.class);

                verificationAct.putExtra("Reg_Mobile_Number", Contact_Replied);

                startActivityForResult(verificationAct, OTP_RQUEST);


            }else if(json_message.equals("ID is Valid")){

                Toast.makeText(act_Password_Recovery.this, "Username does not exists.", Toast.LENGTH_LONG).show();


            }else{


                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Password_Recovery.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


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