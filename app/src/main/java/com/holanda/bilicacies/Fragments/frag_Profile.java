package com.holanda.bilicacies.Fragments;

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
import android.util.Log;
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
import com.holanda.bilicacies.Activities.act_Add_Product_Form;
import com.holanda.bilicacies.Activities.act_Login;
import com.holanda.bilicacies.Activities.act_Order_Tracker_List;
import com.holanda.bilicacies.Activities.act_Product_Preview_Seller_Perspective;
import com.holanda.bilicacies.Activities.act_Profile_Preview;
import com.holanda.bilicacies.Activities.act_Selling_Products_View;
import com.holanda.bilicacies.Activities.act_Single_Photo_Fullscreen;
import com.holanda.bilicacies.Activities.act_conversations_proper;
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
    int NEW_PROD_REQUEST = 110;

    final String PREFS_NAME = "BilicaciesPrefFile";
    String version;
    int LOGIN_REQUEST_CODE = 101;

    ImageView imgProfilePic;
    ImageView imgChangeCover, imgChangeProfile;

    Uri imageUri_Cover, imageUri_Profile_Photo;

    Bitmap Save_Cover_Photo, Save_Profile_Photo;

    String result_string, json_message, json_message_1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ImageView img_cover;
    TextView txtFullName, txtAddress, txtDateReg, txtCellNumber, txtAge, txtLogOut;

    private ProgressDialog pDialog;

    String logged_username, Token_Saved;

    ImageView imgAddProduct;

    TextView txtViewSellingProducts;

    String T_Negotiating, T_To_Send, T_To_Receive, T_Completed;

    TextView OT_Negotiating, OT_To_Send, OT_To_Receive, OT_Completed;

    ImageView img_Negotiating_Tab, img_To_Send_Tab, img_To_Receive_Tab, img_Completed_Tab;

    String current_Token;

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

        imgAddProduct = root.findViewById(R.id.prof_img_Add_Product);

        txtViewSellingProducts = root.findViewById(R.id.profv_txt_View_Products);

        OT_Negotiating = root.findViewById(R.id.prof_txt_Nego_Count);
        OT_To_Send = root.findViewById(R.id.prof_txt_To_Send_Count);
        OT_To_Receive = root.findViewById(R.id.prof_txt_To_Receive_Count);
        OT_Completed = root.findViewById(R.id.prof_txt_Completed_Count);


        OT_Completed.setVisibility(View.GONE);
        OT_Negotiating.setVisibility(View.GONE);
        OT_To_Receive.setVisibility(View.GONE);
        OT_To_Send.setVisibility(View.GONE);


        img_Negotiating_Tab = root.findViewById(R.id.prof_img_Negotiating);
        img_To_Send_Tab = root.findViewById(R.id.prof_img_To_Send);
        img_To_Receive_Tab = root.findViewById(R.id.prof_img_To_Receive);
        img_Completed_Tab = root.findViewById(R.id.prof_img_Completed);

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
            current_Token = settings.getString("logged_Firebase_Token" + version, "No_Token_Saved");


            txtViewSellingProducts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loggingActivity = new Intent(getContext(), act_Selling_Products_View.class);

                    loggingActivity.putExtra("Logged_Username", logged_username);
                    startActivity(loggingActivity);
                }
            });


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
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(imgProfilePic);



            Glide.with(getActivity())
                    .load("https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/cover_photos/" +
                            logged_username + "_cover.png")
                    .error(R.drawable.ic_baseline_photo_size_select_actual_24)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(img_cover);


            imgProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent SinglePhotoPreview = new Intent(getContext(), act_Single_Photo_Fullscreen.class);

                    SinglePhotoPreview.putExtra("SPP_Media_Type", "URL");
                    SinglePhotoPreview.putExtra("SPP_Media_Data", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/profile_photos/" +
                            logged_username + "_profile.png");

                    startActivity(SinglePhotoPreview);

                }
            });

            img_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent SinglePhotoPreview = new Intent(getContext(), act_Single_Photo_Fullscreen.class);

                    SinglePhotoPreview.putExtra("SPP_Media_Type", "URL");
                    SinglePhotoPreview.putExtra("SPP_Media_Data", "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/cover_photos/" +
                            logged_username + "_cover.png");

                    startActivity(SinglePhotoPreview);

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

                            new DeleteLoggedToken().execute();







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


            imgAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    Intent NewProductActivity = new Intent(getContext(), act_Add_Product_Form.class);
                    NewProductActivity.putExtra("Logged_Username", logged_username);
                    startActivityForResult(NewProductActivity, NEW_PROD_REQUEST);



                }
            });


            img_Negotiating_Tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Negotiation");


                    startActivity(OrderTrackingList);




                }
            });

            img_To_Send_Tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "To_Send");


                    startActivity(OrderTrackingList);
                }
            });

            img_To_Receive_Tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "To_Receive");


                    startActivity(OrderTrackingList);
                }
            });

            img_Completed_Tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent OrderTrackingList = new Intent(getContext(), act_Order_Tracker_List.class);

                    OrderTrackingList.putExtra("OrdList_Logged_Username", logged_username);
                    OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Completed");


                    startActivity(OrderTrackingList);
                }
            });



            new Load_Order_Tracking_Counts().execute();


        }






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


                logged_username = data.getStringExtra("Log_Username");
                Token_Saved = settings.getString("logged_Firebase_Token_" + version, "None_Saved");

                if(!logged_username.isEmpty() && !Token_Saved.isEmpty()){

                    new Upload_Token_new_Token_WAN().execute();


                }


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


        }else if(requestCode == NEW_PROD_REQUEST){
            if(resultCode == MainActivity.RESULT_OK){
                //TODO Set refreshing of product list here

                Toast.makeText(getContext(), "Product Uploaded!",Toast.LENGTH_LONG).show();

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




    class Load_Order_Tracking_Counts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/GetOrderTrackingCounts.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("T_UsernameLogged",logged_username);

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
                    if (jsonObj.getString("OrderStatusCategories").equals("No Records Found.")){
                        result_string = "No Records Found.";

                    }else{
                        JSONArray Reg_Json = jsonObj.getJSONArray("OrderStatusCategories");
                        T_Negotiating = "0";
                        T_To_Send = "0";
                        T_To_Receive = "0";
                        T_Completed = "0";
                        for (int i = 0; i < Reg_Json.length(); i++) {

                            JSONObject c = Reg_Json.getJSONObject(i);


                            if(c.getString("Order_Status").equals("Negotiation")){

                                T_Negotiating = c.getString("TotalOrderStatus");
                            }else if(c.getString("Order_Status").equals("To_Send")){
                                T_To_Send = c.getString("TotalOrderStatus");

                            }else if(c.getString("Order_Status").equals("To_Receive")){
                                T_To_Receive = c.getString("TotalOrderStatus");

                            }else if(c.getString("Order_Status").equals("Completed|Success") ||
                                    c.getString("Order_Status").equals("Completed|Denied") ||
                                    c.getString("Order_Status").equals("Completed|Failed")){
                                T_Completed = c.getString("TotalOrderStatus");

                            }


                        }

                        result_string = "Count Loaded";
                    }
                    // Getting JSON Array node

                }else {
                    result_string = "No Response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result_string= new String("Exception: " + e.getMessage());
                //Toast.makeText(getContext(), new String("Exception: " + e.getMessage()), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
//            pDialog.dismiss();



            if(result_string.equals("Count Loaded")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

                if(!T_Negotiating.equals("0")){
                    OT_Negotiating.setText(T_Negotiating);
                    OT_Negotiating.setVisibility(View.VISIBLE);

                }else{
                    OT_Negotiating.setVisibility(View.GONE);
                }

                if(!T_To_Send.equals("0")){
                    OT_To_Send.setText(T_To_Send);
                    OT_To_Send.setVisibility(View.VISIBLE);

                }else{
                    OT_To_Send.setVisibility(View.GONE);
                }

                if(!T_To_Receive.equals("0")){
                    OT_To_Receive.setText(T_To_Receive);
                    OT_To_Receive.setVisibility(View.VISIBLE);

                }else{
                    OT_To_Receive.setVisibility(View.GONE);
                }

                if(!T_Completed.equals("0")){
                    OT_Completed.setText(T_Completed);
                    OT_Completed.setVisibility(View.VISIBLE);

                }else{
                    OT_Completed.setVisibility(View.GONE);
                }


            }else if(result_string.equals("No Response from server")){
                //Toast.makeText(getContext(), result_string + ". Please check your internet connection.", Toast.LENGTH_LONG).show();

            }else{
//
//                String finmsg = result_string;
//                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
//                finmsg = finmsg.replace("/", "");
//                Toast.makeText(getContext(), finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }
        }

    }




    class DeleteLoggedToken extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Logging out. Please wait.");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        protected String doInBackground(String... args) {



            try {


                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/DeleteFirebaseToken.php";

                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Token_to_Delete", current_Token);

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

            if(json_message.equals("Log out Goods")){
                //Toast.makeText(getContext(), json_message_1 , Toast.LENGTH_LONG).show();

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

            }else if(json_message.equals("No Response from server")){
                Toast.makeText(getContext(), json_message + ". Please check your internet connection.", Toast.LENGTH_LONG).show();


            }else if(json_message.equals("Try Again Err: 11")){
                //new Create_New_Profile().execute();
                Toast.makeText(getContext(), "Internal Server Error 11." , Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(getContext(), finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
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