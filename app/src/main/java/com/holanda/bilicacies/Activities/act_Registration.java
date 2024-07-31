package com.holanda.bilicacies.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.holanda.bilicacies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class act_Registration extends AppCompatActivity {

    int OTP_RQUEST = 108;

    ImageView img_profile_photo, img_capture_ID;
    ImageButton img_show_password_new, img_show_password_con;
    TextView txt_show_password_new, txt_show_password_con;
    EditText txtin_New_Password, txtin_Con_Password, txtin_Username;
    TextView txt_add_photo;
    EditText txtin_First_Name, txtin_Middle_Name, txtin_Last_Name, txtin_Mobile_Number, txtin_Street_etc;
    int GET_PHOTO_CODE = 102;
    int CAPTURE_ID_REQUEST = 104;

    Uri imageUri;
    Uri imageUri_camera;

    Spinner spin_brgy, spin_municipalities, spin_gender, spin_BirthYEar, spin_BirthMonth, spin_BirthDay;

    JSONArray refArray_muni;
    JSONObject json_muni;

    JSONArray refArray_brgy;
    JSONObject json_brgy;

    List<String> brgy;

    List<String> municipalities;
    List<String> muni_code;

    ContentValues values;


    int Selected_Year, Selected_Month, Selected_Day;


    private ProgressDialog pDialog;

    Bitmap Save_Profile_Pic;
    Bitmap Save_ID;
    String result_string;

    Button btn_Proceed, btn_retry;

    String json_message;

    String Sel_First_Name, Sel_Middle_Name, Sel_Last_Name, Sel_Mobile_Number, Sel_Gender,
            Sel_Birth_Year, Sel_Birth_Month, Sel_Birth_Day, Sel_Municipality,
            Sel_Barangay, Sel_Street_etc, Sel_Username, Sel_Password = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        img_profile_photo = findViewById(R.id.reg_img_sel_prof_photo);
        txt_add_photo = findViewById(R.id.reg_txt_add_photo);

        spin_municipalities = findViewById(R.id.reg_spinner_Municipality);
        spin_brgy = findViewById(R.id.reg_spinner_Barangay);
        spin_gender = findViewById(R.id.reg_spinner_Gender);
        spin_BirthYEar = findViewById(R.id.reg_spinner_BirthYear);
        spin_BirthMonth = findViewById(R.id.reg_spinner_BirthMonth);
        spin_BirthDay = findViewById(R.id.reg_spinner_BirthDay);

        btn_Proceed = findViewById(R.id.reg_btn_Proceed);

        txtin_First_Name = findViewById(R.id.reg_txtin_First_Name);
        txtin_Middle_Name = findViewById(R.id.reg_txtin_Middle_Name);
        txtin_Last_Name = findViewById(R.id.reg_txtin_Last_Name);
        txtin_Mobile_Number = findViewById(R.id.reg_txtin_Contact_Number);
        txtin_Street_etc = findViewById(R.id.reg_txtin_Landmarks);

        img_capture_ID = findViewById(R.id.reg_img_Valid_ID);

        img_show_password_new = findViewById(R.id.reg_img_Show_Password_New);
        img_show_password_con = findViewById(R.id.reg_img_Show_Password_Confirmed);

        txt_show_password_new = findViewById(R.id.reg_txt_Show_Password_New);
        txt_show_password_con = findViewById(R.id.reg_txt_Show_Password_Confirmed);

        txtin_New_Password = findViewById(R.id.reg_txtin_New_Password);
        txtin_Con_Password = findViewById(R.id.reg_txtin_Confirm_Password);
        txtin_Username = findViewById(R.id.reg_txtin_Username);

        btn_retry = findViewById(R.id.reg_btn_Retry);


        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                    btn_Proceed.setVisibility(View.INVISIBLE);

                    new Save_Reg_Data().execute();




                }else {
                    ActivityCompat.requestPermissions(act_Registration.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 107);


                }



            }
        });


        img_show_password_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_New_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_password_new.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_New_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_show_password_new.setText("Hide Password");
                }
                else{
                    img_show_password_new.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_New_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_show_password_new.setText("Show Password");
                }
            }
        });

        txt_show_password_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_New_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_password_new.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_New_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_show_password_new.setText("Hide Password");
                }
                else{
                    img_show_password_new.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_New_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_show_password_new.setText("Show Password");
                }
            }
        });

        img_show_password_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Con_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_password_con.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Con_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_show_password_con.setText("Hide Password");
                }
                else{
                    img_show_password_con.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Con_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_show_password_con.setText("Show Password");
                }
            }
        });

        txt_show_password_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtin_Con_Password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    img_show_password_con.setImageResource(R.drawable.ic_baseline_visibility_off_24);

                    //Show Password
                    txtin_Con_Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_show_password_con.setText("Hide Password");
                }
                else{
                    img_show_password_con.setImageResource(R.drawable.ic_baseline_visibility_24);

                    //Hide Password
                    txtin_Con_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_show_password_con.setText("Show Password");
                }
            }
        });



        img_capture_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    imageUri_camera = null;
                    values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "Snapshot");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri_camera  = act_Registration.this.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri_camera );

                    startActivityForResult(intent, CAPTURE_ID_REQUEST);
                    //Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_LONG).show();




                }else {
                    ActivityCompat.requestPermissions(act_Registration.this, new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 103);


                }




            }
        });

        img_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                    imageUri = null;

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GET_PHOTO_CODE);


                }else {
                    ActivityCompat.requestPermissions(act_Registration.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 105);


                }

            }
        });



        txt_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                    imageUri = null;

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GET_PHOTO_CODE);


                }else {
                    ActivityCompat.requestPermissions(act_Registration.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 102);


                }






            }
        });

        List<String> Years = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        for(int ind = (year - 17); ind > (year - 70 - 17); ind--){
            Years.add(String.valueOf(ind));

        }
        ArrayAdapter<String>  Year_arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Years);
        spin_BirthYEar.setAdapter(Year_arrayAdapter);
        spin_BirthYEar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Selected_Year = Integer.parseInt(Years.get(position));

                List<String> Months = new ArrayList<>();
                Months.add("January");
                Months.add("February");
                Months.add("March");
                Months.add("April");
                Months.add("May");
                Months.add("June");
                Months.add("July");
                Months.add("August");
                Months.add("September");
                Months.add("October");
                Months.add("November");
                Months.add("December");
                ArrayAdapter<String>  Months_arrayAdapter = new ArrayAdapter<>(
                        act_Registration.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Months);
                spin_BirthMonth.setAdapter(Months_arrayAdapter);
                spin_BirthMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Selected_Month = position + 1;

                        List<String> Days = new ArrayList<>();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, Selected_Year);
                        calendar.set(Calendar.MONTH, position);
                        int numDays = calendar.getActualMaximum(Calendar.DATE);
                        for(int d = 1; d <= numDays; d++){
                            Days.add(String.valueOf(d));

                        }
                        ArrayAdapter<String>  Days_arrayAdapter = new ArrayAdapter<>(
                                act_Registration.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                Days);

                        spin_BirthDay.setAdapter(Days_arrayAdapter);
                        spin_BirthDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Selected_Day = Integer.parseInt(Days.get(position));




                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });



                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        List<String> Gender = new ArrayList<>();
        Gender.add("Male");
        Gender.add("Female");
        ArrayAdapter<String> Gender_arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Gender);
        spin_gender.setAdapter(Gender_arrayAdapter);



        json_muni = loadJsonObjectFromAsset("refcitymun.json");
        municipalities = new ArrayList<>();
        muni_code = new ArrayList<>();

        new Load_Addresses().execute();








        btn_Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean prohibited_char = false;

                Sel_First_Name = txtin_First_Name.getText().toString();
                Sel_Middle_Name = txtin_Middle_Name.getText().toString();
                Sel_Last_Name = txtin_Last_Name.getText().toString();
                Sel_Gender = spin_gender.getSelectedItem().toString();
                Sel_Birth_Year = String.valueOf(Selected_Year);
                Sel_Birth_Month = String.valueOf(Selected_Month);
                Sel_Birth_Day = String.valueOf(Selected_Day);
                Sel_Municipality = spin_municipalities.getSelectedItem().toString();
                Sel_Barangay = spin_brgy.getSelectedItem().toString();
                Sel_Street_etc = txtin_Street_etc.getText().toString();
                Sel_Mobile_Number = txtin_Mobile_Number.getText().toString();
                Sel_Username = txtin_Username.getText().toString().trim();




                if(txtin_New_Password.getText().toString().equals(txtin_Con_Password.getText().toString())){

                    Sel_Password = txtin_Con_Password.getText().toString();


                }else{
                    txtin_New_Password.setError("Passwords do not match");
                    txtin_Con_Password.setError("Passwords do not match");
                }



                if(Sel_Username.equals("") || Sel_Username.isEmpty() || Sel_Username == null){
                    txtin_Username.setError("Username must not be empty.");
                }else if(Sel_Username.contains("#") || Sel_Username.contains("%") || Sel_Username.contains("&") ||
                        Sel_Username.contains("{") || Sel_Username.contains("}") || Sel_Username.contains("\\") ||
                        Sel_Username.contains("<") || Sel_Username.contains(">") || Sel_Username.contains("*") ||
                        Sel_Username.contains("?") || Sel_Username.contains("/") || Sel_Username.contains(" ") ||
                        Sel_Username.contains("$") || Sel_Username.contains("!") || Sel_Username.contains("'") ||
                        Sel_Username.contains("\"") || Sel_Username.contains(":") || Sel_Username.contains("@") ||
                        Sel_Username.contains("+") || Sel_Username.contains("`") || Sel_Username.contains("|") ||
                        Sel_Username.contains("=")){
                    prohibited_char = true;
                    txtin_Username.setError("Username must not contain spaces and prohibited symbols. eg: #%&{}\\<>*?/$!'\":@+`|=");


                }else{
                    prohibited_char = false;
                }

                if(Sel_First_Name.equals("") || Sel_First_Name.isEmpty() || Sel_First_Name == null){
                    txtin_First_Name.setError("First Name must not be empty.");
                }

                if(Sel_Middle_Name.equals("") || Sel_Middle_Name.isEmpty() || Sel_Middle_Name == null){
                    txtin_Middle_Name.setError("Middle Name must not be empty.");
                }

                if(Sel_Last_Name.equals("") || Sel_Last_Name.isEmpty() || Sel_Last_Name == null){
                    txtin_Last_Name.setError("Last Name must not be empty.");
                }

                if(Sel_Street_etc.equals("") || Sel_Street_etc.isEmpty() || Sel_Street_etc == null){
                    txtin_Street_etc.setError("This must not be empty.");
                }
                if(Sel_Mobile_Number.equals("") || Sel_Mobile_Number.isEmpty() || Sel_Mobile_Number == null){
                    txtin_Mobile_Number.setError("Mobile Number must not be empty.");
                }

                if(Save_Profile_Pic == null){
                    Toast.makeText(act_Registration.this, "Profile picture must not be empty", Toast.LENGTH_LONG).show();


                }
                if(Save_ID == null){
                    Toast.makeText(act_Registration.this, "ID picture must not be empty", Toast.LENGTH_LONG).show();


                }

                if(!(Sel_First_Name.equals("") || Sel_First_Name.isEmpty() || Sel_First_Name == null ||
                        Sel_Last_Name.equals("") || Sel_Last_Name.isEmpty() || Sel_Last_Name == null ||
                        Sel_Middle_Name.equals("") || Sel_Middle_Name.isEmpty() || Sel_Middle_Name == null ||
                        Sel_Gender.equals("") || Sel_Gender.isEmpty() || Sel_Gender == null ||
                        Sel_Birth_Year.equals("") || Sel_Birth_Year.isEmpty() || Sel_Birth_Year == null ||
                        Sel_Birth_Month.equals("") || Sel_Birth_Month.isEmpty() || Sel_Birth_Month == null ||
                        Sel_Birth_Day.equals("") || Sel_Birth_Day.isEmpty() || Sel_Birth_Day == null ||
                        Sel_Municipality.equals("") || Sel_Municipality.isEmpty() || Sel_Municipality == null ||
                        Sel_Barangay.equals("") || Sel_Barangay.isEmpty() || Sel_Barangay == null ||
                        Sel_Street_etc.equals("") || Sel_Street_etc.isEmpty() || Sel_Street_etc == null ||
                        Sel_Mobile_Number.equals("") || Sel_Mobile_Number.isEmpty() || Sel_Mobile_Number == null ||
                        Sel_Username.equals("") || Sel_Username.isEmpty() || Sel_Username == null ||
                        Sel_Password.equals("") || Sel_Password.isEmpty() || Sel_Password == null ||
                        imageUri_camera == null || imageUri == null || prohibited_char == true)){

                    //TODO Set Username duplicate checking method before code below
                    new Check_Username_Availability().execute();

//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    Save_ID.compress(Bitmap.CompressFormat.JPEG,70,stream);
//                    byte[] byteArray = stream.toByteArray();
//                    String encodedImage_ID = Base64.encodeToString(byteArray, Base64.DEFAULT);
//
//                    ByteArrayOutputStream stream_1 = new ByteArrayOutputStream();
//                    Save_Profile_Pic.compress(Bitmap.CompressFormat.JPEG,70,stream_1);
//                    byte[] byteArray_1 = stream.toByteArray();
//                    String encodedImage_Profile_Pic = Base64.encodeToString(byteArray_1, Base64.DEFAULT);




                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_PHOTO_CODE) {
            if (resultCode == RESULT_OK && data != null) {

                imageUri = data.getData();
                new Load_Cap_Image().execute();

            }else {
                Toast.makeText(act_Registration.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }


        }else if(requestCode == CAPTURE_ID_REQUEST && imageUri_camera != null){
            if(resultCode == RESULT_OK){
                new Load_Cap_ID().execute();

            }


        }else if(requestCode == OTP_RQUEST){
            if(resultCode == RESULT_OK){

                if (ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(act_Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                    btn_Proceed.setVisibility(View.INVISIBLE);

                    new Save_Reg_Data().execute();




                }else {
                    ActivityCompat.requestPermissions(act_Registration.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 107);


                }

            }else if(resultCode == RESULT_CANCELED){

                btn_Proceed.setVisibility(View.VISIBLE);
            }

        }



    }

    class Load_Addresses extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Processing Municipalities. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {
                refArray_muni = json_muni.getJSONArray("RECORDS");

                for(int i = 0; i< refArray_muni.length(); i++){
                    if(refArray_muni.getJSONObject(i).getString("provCode").equals("0878")){
                        municipalities.add(refArray_muni.getJSONObject(i).getString("citymunDesc"));
                        muni_code.add(refArray_muni.getJSONObject(i).getString("citymunCode"));
                    }

                }
                result_string = "Done";

            } catch (Exception e) {
                e.printStackTrace();
                result_string = e.getMessage().toString();
                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(result_string.equals("Done")){


                ArrayAdapter<String> arrayAdapter_muni = new ArrayAdapter<>(
                        act_Registration.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        municipalities);
                spin_municipalities.setAdapter(arrayAdapter_muni);

                spin_municipalities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                        json_brgy = loadJsonObjectFromAsset("refbrgy.json");
                        brgy = new ArrayList<>();

                        new Load_Brgys().execute(String.valueOf(position));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });






                Toast.makeText(act_Registration.this, "Done!", Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(act_Registration.this, result_string, Toast.LENGTH_LONG).show();
            }



        }

    }

    class Load_Cap_ID extends AsyncTask<String, String, String> {

        Bitmap thumbnail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Processing ID. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {

                String file_path = getRealPathFromURI(imageUri_camera);
                ExifInterface ei = new ExifInterface(file_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                thumbnail = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), imageUri_camera);
                Save_ID = thumbnail;

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Save_ID = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Save_ID = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Save_ID = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        Save_ID = thumbnail;
                }


                result_string = "Done";

            } catch (Exception e) {
                e.printStackTrace();
                result_string = e.getMessage().toString();
                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(result_string.equals("Done") && Save_ID != null){
                Bitmap temp_bitmap = Bitmap.createScaledBitmap(Save_ID, Save_ID.getWidth() / 2, Save_ID.getHeight() / 2, false);
                img_capture_ID.setImageBitmap(temp_bitmap);






                Toast.makeText(act_Registration.this, "Done!", Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(act_Registration.this, result_string, Toast.LENGTH_LONG).show();
            }



        }

    }

    //Load_Brgys
    class Load_Brgys extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Processing Barangays. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {
                refArray_brgy = json_brgy.getJSONArray("RECORDS");

                for(int i = 0; i< refArray_brgy.length(); i++){
                    if(refArray_brgy.getJSONObject(i).getString("citymunCode").equals(muni_code.get(Integer.parseInt(args[0])))){
                        brgy.add(refArray_brgy.getJSONObject(i).getString("brgyDesc"));
                    }

                }
                result_string = "Done";

            } catch (Exception e) {
                e.printStackTrace();
                result_string = e.getMessage().toString();
                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(result_string.equals("Done")){


                ArrayAdapter<String> arrayAdapter_brgy = new ArrayAdapter<>(
                        act_Registration.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        brgy);
                spin_brgy.setAdapter(arrayAdapter_brgy);






                Toast.makeText(act_Registration.this, "Done!", Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(act_Registration.this, result_string, Toast.LENGTH_LONG).show();
            }



        }

    }



    class Load_Cap_Image extends AsyncTask<String, String, String> {

        Bitmap thumbnail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Processing your Photo. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {

                String file_path = getRealPathFromURI(imageUri);
                ExifInterface ei = new ExifInterface(file_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                thumbnail = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), imageUri);
                Save_Profile_Pic = thumbnail;

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Save_Profile_Pic = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Save_Profile_Pic = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Save_Profile_Pic = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        Save_Profile_Pic = thumbnail;
                }


                result_string = "Done";

            } catch (Exception e) {
                e.printStackTrace();
                result_string = e.getMessage().toString();
                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(result_string.equals("Done") && Save_Profile_Pic != null){
                Bitmap temp_bitmap = Bitmap.createScaledBitmap(Save_Profile_Pic, Save_Profile_Pic.getWidth() / 2, Save_Profile_Pic.getHeight() / 2, false);
                img_profile_photo.setImageBitmap(temp_bitmap);






                Toast.makeText(act_Registration.this, "Done!", Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(act_Registration.this, result_string, Toast.LENGTH_LONG).show();
            }



        }

    }



    class Check_Username_Availability extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Checking Username Availability. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/CheckUsernameAvailability.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Q_Username",Sel_Username);

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

            if(json_message.equals("ID is Valid")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

                new Check_Contact_Availability().execute();




            }else if(json_message.equals("Match Found")){
                Toast.makeText(act_Registration.this, json_message + ". Please use another username", Toast.LENGTH_LONG).show();
                txtin_Username.setError(json_message + ". Please use another username");

            }else if(json_message.equals("No Response from server")){
                Toast.makeText(act_Registration.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();


            }else if(json_message.equals("Try Again Err: 10")){
                //new Create_New_Profile().execute();
                Toast.makeText(act_Registration.this, "Internal Server Error 10." , Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace("192.168.254.10:8080", "Server 0");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Registration.this, finmsg + "\nException L535", Toast.LENGTH_LONG).show();



                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }

    class Check_Contact_Availability extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Checking Username Availability. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link ="https://" + getString(R.string.Server_Web_Host_IP ) + "BILICACIES/CheckContactAvailability.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Q_Contact",Sel_Mobile_Number);

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

            if(json_message.equals("Contact is Valid")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();



                Intent verificationAct = new Intent(getApplicationContext(), act_Reg_Phone_Verification.class);

                verificationAct.putExtra("Reg_Mobile_Number", Sel_Mobile_Number);

                startActivityForResult(verificationAct, OTP_RQUEST);



            }else if(json_message.equals("Match Found")){
                Toast.makeText(act_Registration.this, "This mobile number is already in use.", Toast.LENGTH_LONG).show();
                txtin_Mobile_Number.setError("This mobile number is already in use.");

            }else if(json_message.equals("No Response from server")){
                Toast.makeText(act_Registration.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();


            }else if(json_message.equals("Try Again Err: 10")){
                //new Create_New_Profile().execute();
                Toast.makeText(act_Registration.this, "Internal Server Error 10." , Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace("192.168.254.10:8080", "Server 0");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Registration.this, finmsg + "\nException L535", Toast.LENGTH_LONG).show();



                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    class Save_Reg_Data extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(act_Registration.this);
            pDialog.setMessage("Saving Registration Data. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {






                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Save_ID.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                String encodedImage_ID = Base64.encodeToString(byteArray, Base64.DEFAULT);

                stream = new ByteArrayOutputStream();
                Save_Profile_Pic.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byteArray = stream.toByteArray();
                String encodedImage_Profile_Pic = Base64.encodeToString(byteArray, Base64.DEFAULT);




                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/NewRegistration.php";





                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("R_Username",Sel_Username);
                data_1.put("R_Password",Sel_Password);
                data_1.put("R_First_Name",Sel_First_Name);
                data_1.put("R_Middle_Name",Sel_Middle_Name);
                data_1.put("R_Last_Name",Sel_Last_Name);
                data_1.put("R_Gender",Sel_Gender);
                data_1.put("R_Birth_Year",Sel_Birth_Year);
                data_1.put("R_Birth_Month",Sel_Birth_Month);
                data_1.put("R_Birth_Day",Sel_Birth_Day);
                data_1.put("R_Municipalities",Sel_Municipality);
                data_1.put("R_Barangay",Sel_Barangay);
                data_1.put("R_Street",Sel_Street_etc);
                data_1.put("R_Mobile_Number",Sel_Mobile_Number);
                data_1.put("R_Photo_ID",encodedImage_ID);
                data_1.put("R_Photo_Profile",encodedImage_Profile_Pic);



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

            if(json_message.equals("Data Submit Successfully")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

                Toast.makeText(act_Registration.this, "Data Submission Success!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();

                intent.putExtra("Save_Username",Sel_Username);
                intent.putExtra("Save_Password",Sel_Password);
                intent.putExtra("Save_First_Name",Sel_First_Name);
                intent.putExtra("Save_Middle_Name",Sel_Middle_Name);
                intent.putExtra("Save_Last_Name",Sel_Last_Name);
                intent.putExtra("Save_Gender",Sel_Gender);
                intent.putExtra("Save_Birth_Year",Sel_Birth_Year);
                intent.putExtra("Save_Birth_Month",Sel_Birth_Month);
                intent.putExtra("Save_Birth_Day",Sel_Birth_Day);
                intent.putExtra("Save_Municipalities",Sel_Municipality);
                intent.putExtra("Save_Barangay",Sel_Barangay);
                intent.putExtra("Save_Street",Sel_Street_etc);
                intent.putExtra("Save_Mobile_Number",Sel_Mobile_Number);

                setResult(RESULT_OK, intent);
                finish();






            }else if(json_message.equals("No Response from server")){
                Toast.makeText(act_Registration.this, json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();
                btn_retry.setVisibility(View.VISIBLE);

            }else if(json_message.equals("Try Again Err: 10")){
                //new Create_New_Profile().execute();
                Toast.makeText(act_Registration.this, "Internal Server Error 10." , Toast.LENGTH_LONG).show();
                btn_retry.setVisibility(View.VISIBLE);
            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_Registration.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();
                btn_retry.setVisibility(View.VISIBLE);


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }


    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    public  JSONObject loadJsonObjectFromAsset(String assetName) {
        try {
            String json = loadStringFromAsset(assetName);
            if (json != null)
                return new JSONObject(json);
        } catch (Exception e) {
            Log.e("JsonUtils", e.toString());
        }

        return null;
    }

    private String loadStringFromAsset(String assetName) throws Exception {
        InputStream is = this.getAssets().open(assetName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
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