package com.holanda.bilicacies.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.holanda.bilicacies.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    int LOGIN_REQUEST_CODE = 101;

    String logged_username, json_message_1;


    final String PREFS_NAME = "BilicaciesPrefFile";
    String version;
    String got_Token, Token_Saved;

    private static final String MSG_TAG = "MyFirebaseMsgService";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MyApplication);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(MSG_TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        got_Token = task.getResult();
                        //got_Token = token;
                        // Log and toast
                        //got_Token = getString(R.string.msg_token_fmt, token);
                        Log.d(MSG_TAG, got_Token);



                        settings.edit().putString("logged_Firebase_Token_"  + version, got_Token).apply();
                        //Toast.makeText(MainActivity.this, got_Token, Toast.LENGTH_SHORT).show();
                        //create method to save Token to database PHP MySql



                    }
                });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){





            if (settings.getBoolean("registered_already_" + version, false)) {

                logged_username = settings.getString("logged_Username_" + version, "Not Logged in");
                Token_Saved = settings.getString("logged_Firebase_Token_" + version, "None_Saved");
                if(!logged_username.isEmpty() &&  !Token_Saved.isEmpty()){

                    Log.d(MSG_TAG, "Saved");
                    new Upload_Token_new_Token_WAN().execute();


                }



                setContentView(R.layout.activity_main);
                BottomNavigationView navView = findViewById(R.id.nav_view);
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_home, R.id.navigation_messages, R.id.navigation_notifications, R.id.navigation_profile)
                        .build();
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                //           NavigationUI.setupActionBarWithNavController(this, navController );
                NavigationUI.setupWithNavController(navView, navController);

            }else{

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                    logged_username = "";
                    //txtLogInState.setText("Logged in as: " + logged_username);
                    Intent loggingActivity = new Intent(getApplicationContext(), act_Login.class);
                    startActivityForResult(loggingActivity, LOGIN_REQUEST_CODE);







                }else {

                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET
                    }, 103);

                }




            }





        }else {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 103);

            Toast.makeText(this, "Please restart app to apply changes.", Toast.LENGTH_LONG).show();
            finish();



        }









    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

                settings.edit().putBoolean("registered_already_"  + version, true).apply();
                settings.edit().putString("logged_First_Name_"  + version, data.getStringExtra("Log_First_Name")).apply();
                settings.edit().putString("logged_Middle_Name_"  + version, data.getStringExtra("Log_Middle_Name")).apply();
                settings.edit().putString("logged_Last_Name_"  + version, data.getStringExtra("Log_Last_Name")).apply();
                settings.edit().putString("logged_Mobile_Number_"  + version, data.getStringExtra("Log_Mobile_Number")).apply();
                settings.edit().putString("logged_Gender_"  + version, data.getStringExtra("Log_Gender")).apply();
                settings.edit().putString("logged_Birthday_"  + version, data.getStringExtra("Log_Birthday")).apply();
                settings.edit().putString("logged_Municipality_"  + version, data.getStringExtra("Log_Municipality")).apply();
                settings.edit().putString("logged_Barangay_"  + version, data.getStringExtra("Log_Barangay")).apply();
                settings.edit().putString("logged_Extra_Address_"  + version, data.getStringExtra("Log_Extra_Address")).apply();
                settings.edit().putString("logged_Username_"  + version, data.getStringExtra("Log_Username")).apply();
                settings.edit().putString("logged_Password_"  + version, data.getStringExtra("Log_Password")).apply();
                settings.edit().putString("logged_Date_Registered_"  + version, data.getStringExtra("Log_Date_Registered")).apply();



                logged_username = settings.getString("logged_Username_" + version, "Not Logged in");
                Token_Saved = settings.getString("logged_Firebase_Token_" + version, "None_Saved");

                if(!logged_username.isEmpty() && !Token_Saved.isEmpty()){

                    Log.d(MSG_TAG, "Saved Logged");
                    new Upload_Token_new_Token_WAN().execute();


                }






                setContentView(R.layout.activity_main);
                BottomNavigationView navView = findViewById(R.id.nav_view);
                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_home, R.id.navigation_messages, R.id.navigation_notifications)
                        .build();
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);

            }else if(resultCode == RESULT_CANCELED){

                finish();

            }


        }




    }



    class Upload_Token_new_Token_WAN extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        protected String doInBackground(String... args) {
            try {


                String link="https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/Insert_New_Firebase_Token.php";
                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("And_Username", logged_username);
                data_1.put("And_Token",  Token_Saved);

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
                    json_message_1 = obj.getString("message");
                }else{
                    json_message_1 = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                json_message_1 = new String("Exception: " + e.getMessage());
                //Toast.makeText(getContext(), new String("Exception: " + e.getMessage()), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //pDialog_1.dismiss();
            Log.d("Token_Uploader_Mes_Svc",  json_message_1);
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