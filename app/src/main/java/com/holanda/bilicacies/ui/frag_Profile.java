package com.holanda.bilicacies.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.holanda.bilicacies.Activities.MainActivity;
import com.holanda.bilicacies.Activities.act_Login;
import com.holanda.bilicacies.Activities.act_Selling_Products_View;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link frag_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frag_Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int GET_COVER_PHOTO_CODE = 109;
    int GET_PROFILE_PHOTO_CODE = 108;

    final String PREFS_NAME = "BilicaciesPrefFile";
    String version;
    int LOGIN_REQUEST_CODE = 101;

    ImageView imgProfilePic;
    ImageView imgChangeCover, imgChangeProfile;

    Uri imageUri_Cover, imageUri_Profile_Photo;

    Bitmap Save_Cover_Photo, Save_Profile_Photo;

    String result_string;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ImageView img_cover;
    TextView txtFullName, txtAddress, txtDateReg, txtCellNumber, txtAge, txtLogOut;

    TextView txtViewProducts;
    ImageView imgViewProducts;
    ImageView imgViewProductsArrow;

    private ProgressDialog pDialog;

    String logged_username;



    public frag_Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag_Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_Profile newInstance(String param1, String param2) {
        frag_Profile fragment = new frag_Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFullName = root.findViewById(R.id.prof_txt_Full_Name);
        txtAddress = root.findViewById(R.id.profv_txt_Address);
        txtDateReg = root.findViewById(R.id.prof_txt_Date_of_registration);
        txtAge = root.findViewById(R.id.prof_txt_Age);
        txtCellNumber = root.findViewById(R.id.prof_txt_Contact_Number);

        txtLogOut = root.findViewById(R.id.prof_txt_Logout);
        imgProfilePic = root.findViewById(R.id.prof_img_profile_photo);
        imgChangeCover = root.findViewById(R.id.prof_img_Change_Cover_Photo);
        img_cover = root.findViewById(R.id.prof_img_Cover_Photo);
        imgChangeProfile = root.findViewById(R.id.prof_img_Change_Profile_Photo);

        txtViewProducts = root.findViewById(R.id.profv_txt_View_Products);
        imgViewProductsArrow = root.findViewById(R.id.prof_img_View_Products_Arrow);
        imgViewProducts = root.findViewById(R.id.prof_imgicon_View_Products);




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
            txtFullName.setText(new StringBuilder().append(settings.getString("logged_Last_Name_" + version, "Not Logged in")).append(", " +
                    "").append(settings.getString("logged_First_Name_" + version, "Not Logged in")).append(" " +
                    "").append(settings.getString("logged_Middle_Name_" + version, "Not Logged in")).toString());

            txtAddress.setText(new StringBuilder().append(settings.getString("logged_Extra_Address_" + version, "Not Logged in")).append(", " +
                    "").append(settings.getString("logged_Barangay_" + version, "Not Logged in")).append(", " +
                    "").append(settings.getString("logged_Municipality_" + version, "Not Logged in")).toString());


            SimpleDateFormat dateFormat_reg = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date_reg = dateFormat_reg.parse(settings.getString("logged_Date_Registered_" + version, "Not Logged in"));
                String datename = (String) android.text.format.DateFormat.format("MMMM dd, yyyy", date_reg);
                txtDateReg.setText(datename);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            txtCellNumber.setText(settings.getString("logged_Mobile_Number_" + version, "Not Logged in"));

            String strDate = settings.getString("logged_Birthday_" + version, "Not Logged in");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date_sel = dateFormat.parse(strDate);
                txtAge.setText(String.valueOf(getDiffYears(date_sel, Calendar.getInstance().getTime())));
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Selected: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            Glide.with(getActivity())
                    .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" +
                            logged_username + "_profile.png")
                    .circleCrop()
                    .error(R.drawable.ic_baseline_error_24)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(imgProfilePic);


            Glide.with(getActivity())
                    .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/cover_photos/" +
                            logged_username + "_cover.png")
                    .error(R.drawable.ic_baseline_error_24)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(img_cover);









        }






        imgViewProductsArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!logged_username.isEmpty()){

                    //Toast.makeText(getContext(), "Click was here",Toast.LENGTH_LONG).show();
                    Intent ViewSellProducts = new Intent(getContext(), act_Selling_Products_View.class);

                    ViewSellProducts.putExtra("Logged_Username", logged_username);

                    startActivity(ViewSellProducts);

                }else{

                    Toast.makeText(getContext(), "No username",Toast.LENGTH_LONG).show();
                }

            }
        });

        imgViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logged_username.isEmpty()){
                    //Toast.makeText(getContext(), "Click was here",Toast.LENGTH_LONG).show();
                    Intent ViewSellProducts = new Intent(getContext(), act_Selling_Products_View.class);

                    ViewSellProducts.putExtra("Logged_Username", logged_username);

                    startActivity(ViewSellProducts);

                }else{
                    Toast.makeText(getContext(), "No username",Toast.LENGTH_LONG).show();
                }
            }
        });

        txtViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!logged_username.isEmpty()){


                    Intent ViewSellProducts = new Intent(getContext(), act_Selling_Products_View.class);

                    ViewSellProducts.putExtra("Logged_Username", logged_username);

                    startActivity(ViewSellProducts);
                    //Toast.makeText(getContext(), "Click was here",Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getContext(), "No username",Toast.LENGTH_LONG).show();

                }
            }
        });


        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure to Log Out?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

                        settings.edit().putBoolean("registered_already_"  + version, false).apply();

                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){


                            //txtLogInState.setText("Logged in as: " + logged_username);
                            Intent loggingActivity = new Intent(getContext(), act_Login.class);
                            startActivityForResult(loggingActivity, LOGIN_REQUEST_CODE);


                        }else {

                            ActivityCompat.requestPermissions(getActivity(), new String[]{
                                    Manifest.permission.INTERNET
                            }, 103);

                        }




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

        imgChangeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Confirm");
                    builder.setMessage("Upload new Cover Photo?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog

                            imageUri_Cover = null;

                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GET_COVER_PHOTO_CODE);

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

                }else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 106);


                }



            }
        });


        imgChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Confirm");
                    builder.setMessage("Upload new Profile Photo?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog

                            imageUri_Profile_Photo = null;

                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GET_PROFILE_PHOTO_CODE);

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

                }else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 107);


                }



            }
        });




        return root;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK) {
                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

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


            }else if(resultCode == MainActivity.RESULT_CANCELED){

                getActivity().finish();

            }


        }else if(requestCode == GET_COVER_PHOTO_CODE){
            if (resultCode == MainActivity.RESULT_OK && data != null) {

                imageUri_Cover = data.getData();
                new Load_Cap_Image_Cover().execute();

            }else {
                Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
            }


        }else if(requestCode == GET_PROFILE_PHOTO_CODE){
            if (resultCode == MainActivity.RESULT_OK && data != null) {

                imageUri_Profile_Photo = data.getData();
                new Load_Cap_Image_Profile().execute();

            }else {
                Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
            }


        }




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

    class Load_Cap_Image_Profile extends AsyncTask<String, String, String> {

        Bitmap thumbnail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Processing your Photo. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {

                String file_path = getRealPathFromURI(imageUri_Profile_Photo);
                ExifInterface ei = new ExifInterface(file_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                thumbnail = MediaStore.Images.Media.getBitmap(
                        getContext().getContentResolver(), imageUri_Profile_Photo);
                Save_Profile_Photo = thumbnail;

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Save_Profile_Photo = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Save_Profile_Photo = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Save_Profile_Photo = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        Save_Profile_Photo = thumbnail;
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
            if(result_string.equals("Done") &&  Save_Profile_Photo != null){

                new Upload_New_Profile_Photo().execute();


            }else{
                Toast.makeText(getContext(), result_string, Toast.LENGTH_LONG).show();
            }



        }

    }


    class Upload_New_Profile_Photo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Uploading Profile Photo. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Save_Profile_Photo.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                String encodedImage_Profile = Base64.encodeToString(byteArray, Base64.DEFAULT);


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/SaveProfilePhoto.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("P_Username",logged_username);
                data_1.put("P_Profile_Photo",encodedImage_Profile);


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

                    result_string = obj.getString("message");


                }else{
                    result_string = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result_string = new String("Exception: " + e.getMessage());
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

            if(result_string.equals("Data Submit Successfully")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();
                Glide.with(getActivity())
                        .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" +
                                logged_username + "_profile.png")
                        .circleCrop()
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(imgProfilePic);
                Toast.makeText(getContext(), "Upload Success!", Toast.LENGTH_LONG).show();

            }else if(result_string.equals("No Response from server")){
                Toast.makeText(getContext(), result_string + ". Please check your internet connection.", Toast.LENGTH_LONG).show();

            }else{

                String finmsg = result_string;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(getContext(), finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }



    class Load_Cap_Image_Cover extends AsyncTask<String, String, String> {

        Bitmap thumbnail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Processing your Photo. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {

                String file_path = getRealPathFromURI(imageUri_Cover);
                ExifInterface ei = new ExifInterface(file_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                thumbnail = MediaStore.Images.Media.getBitmap(
                        getContext().getContentResolver(), imageUri_Cover);
                Save_Cover_Photo = thumbnail;

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Save_Cover_Photo = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Save_Cover_Photo = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Save_Cover_Photo = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        Save_Cover_Photo = thumbnail;
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
            if(result_string.equals("Done") &&  Save_Cover_Photo != null){

                new Upload_New_Cover().execute();


            }else{
                Toast.makeText(getContext(), result_string, Toast.LENGTH_LONG).show();
            }



        }

    }



    class Upload_New_Cover extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Uploading Cover Photo. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Save_Cover_Photo.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                String encodedImage_Cover = Base64.encodeToString(byteArray, Base64.DEFAULT);


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/SaveCoverPhoto.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("P_Username",logged_username);
                data_1.put("P_Cover_Photo",encodedImage_Cover);


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

                    result_string = obj.getString("message");


                }else{
                    result_string = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result_string = new String("Exception: " + e.getMessage());
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

            if(result_string.equals("Data Submit Successfully")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();



                Glide.with(getActivity())
                        .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/cover_photos/" +
                                logged_username + "_cover.png")
                        .error(R.drawable.ic_baseline_photo_size_select_actual_24)
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(img_cover);
                Toast.makeText(getContext(), "Upload Success!", Toast.LENGTH_LONG).show();

            }else if(result_string.equals("No Response from server")){
                Toast.makeText(getContext(), result_string + ". Please check your internet connection.", Toast.LENGTH_LONG).show();

            }else{

                String finmsg = result_string;
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

    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
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
}