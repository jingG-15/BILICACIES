package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.holanda.bilicacies.Adapters.chat_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.chat_variable_links;
import com.holanda.bilicacies.Adapters.convo_Recycler_Adapter;
import com.holanda.bilicacies.Adapters.convo_variable_links;
import com.holanda.bilicacies.R;
import com.wang.avi.AVLoadingIndicatorView;

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

public class act_conversations_proper extends AppCompatActivity {

    int CAPTURE_ID_REQUEST = 101;
    int GET_PHOTO_CODE = 102;

    Bitmap Photo_to_Send;
    String encodedImage;

    Uri imageUri_camera;
    Uri imageUri_Gallery;
    ContentValues values;

    private ProgressDialog pDialog;

    CountDownTimer cTimer_1 = null;
    CountDownTimer cTimer_2 = null;

    Boolean New_Message_Fetcher_busy = false;
    Boolean Sender_is_Busy = false;



    String Convo_ID, Supplier_Name, Supplier_ID, logged_Username, json_message;

    RecyclerView R_recycler_chats;
    SwipeRefreshLayout swiper_item_container;
    GridLayoutManager mGridLayoutmanager;
    ImageView img_Profile_Photo;
    TextView txt_Supplier_Name;
    ImageView img_Back;
    EditText txtin_MessageStr_ToSend;
    ImageView img_Send;
    ImageView img_Attach_image;


    int last_index_array;
    String last_ID;
    int Load_times;

    ArrayList<HashMap<String, String>> Resulting_Chat_List;
    ArrayList<chat_variable_links> chat_list;

    ArrayList<HashMap<String, String> > Job_Sequence;


    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private int previousTotal = 0;
    private int visibleThreshold = 5;

    String Buyer_ID;

    AVLoadingIndicatorView prog_Sending_Msg;
    TextView txt_Sending_Msg_Label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_proper);

        Convo_ID = getIntent().getExtras().getString("Chat_Convo_ID");
        Supplier_Name = getIntent().getExtras().getString("Chat_Supplier_Name");
        Supplier_ID = getIntent().getExtras().getString("Chat_Supplier_ID");
        logged_Username = getIntent().getExtras().getString("Chat_Logged_Username");
        Buyer_ID = getIntent().getExtras().getString("Chat_Buyer_ID");


        R_recycler_chats = findViewById(R.id.conpro_recycler_view);
        swiper_item_container = findViewById(R.id.conpro_swipe_ref);
        img_Profile_Photo = findViewById(R.id.conpro_img_Seller_Profile_Photo);
        img_Back = findViewById(R.id.conpro_img_Back);
        txt_Supplier_Name = findViewById(R.id.conpro_txt_Seller_FullName);
        txt_Supplier_Name.setText(Supplier_Name);
        txtin_MessageStr_ToSend = findViewById(R.id.conpro_txtin_Message_Body);
        img_Send = findViewById(R.id.conpro_img_Send);
        img_Attach_image = findViewById(R.id.conpro_img_Attach_Image);
        prog_Sending_Msg = findViewById(R.id.conpro_progress_avi);
        txt_Sending_Msg_Label = findViewById(R.id.conpro_txt_Sending_Msg_Label);

        prog_Sending_Msg.setVisibility(View.INVISIBLE);
        txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);


        Glide.with(this)
                .load("https://" + getString(R.string.Server_Web_Host_IP) + "BILICACIES/profile_photos/" + Supplier_ID + "_profile.png")
                .circleCrop()
                .into(img_Profile_Photo);


        Job_Sequence = new ArrayList<>();
        startTimer_Job_Sequencer();



        mGridLayoutmanager = new GridLayoutManager(this, 1);
        R_recycler_chats.setLayoutManager(mGridLayoutmanager);


        New_Message_Fetcher_busy = true;
        last_index_array = 0;
        last_ID = "0";
        Load_times = 1;
        Resulting_Chat_List = new ArrayList<>();
        chat_list = new ArrayList<>();
        //txtEndofProd.setVisibility(View.INVISIBLE);
        swiper_item_container.setRefreshing(true);
        new Load_Messages().execute();

        R_recycler_chats.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = R_recycler_chats.getChildCount();
                totalItemCount = mGridLayoutmanager.getItemCount();
                firstVisibleItem = mGridLayoutmanager.findFirstVisibleItemPosition();

                if (loading) {

                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;

                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("Yaeye!", "end called");

                    // Do something
                    if((Load_times >= 1) && (Resulting_Chat_List.size() >= (50 * Load_times))){
                        Load_times = Load_times + 1;
                        New_Message_Fetcher_busy = true;
                        new Load_Next_Messages().execute();

                    }else{
                        //txtEndofProd.setVisibility(View.VISIBLE);


                    }


                    loading = true;
                }

            }
        });



        swiper_item_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                previousTotal = 0;
                loading = true;
                visibleThreshold = 5;

                New_Message_Fetcher_busy = true;
                last_index_array = 0;
                last_ID = "0";
                Load_times = 0;
                Resulting_Chat_List = new ArrayList<>();
                chat_list = new ArrayList<>();
                //txtEndofProd.setVisibility(View.INVISIBLE);
                swiper_item_container.setRefreshing(true);
                new Load_Messages().execute();




            }
        });

        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        img_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message_toSend = txtin_MessageStr_ToSend.getText().toString();

                if(message_toSend.isEmpty() || message_toSend.equals(null) || message_toSend.equals("")){

                    Toast.makeText(act_conversations_proper.this, "Message cannot be empty", Toast.LENGTH_LONG).show();


                }else{

//                    HashMap<String, String> result_arr = new HashMap<>();
//
//                    // adding each child node to HashMap key => value
//                    last_ID = String.valueOf(Integer.parseInt(last_ID) + 1);
//                    result_arr.put("ID", last_ID);
//                    result_arr.put("Convo_ID", Convo_ID);
//                    result_arr.put("Message_Type", "Sender_Message_String");
//                    result_arr.put("Message_Content", message_toSend);
//                    result_arr.put("Date_Sent", "0");
//                    result_arr.put("Sel_Prod_ID", "0");
//                    result_arr.put("Sel_Product_Name", "0");
//                    result_arr.put("Sel_Product_Price", "0");
//                    result_arr.put("Sel_Prod_Date_Added", "0");
//                    result_arr.put("Sel_Seller_ID", "0");
//
//                    // adding contact to contact list
//                    Resulting_Chat_List.add(result_arr);
//
//
//                    chat_list.add(new chat_variable_links(last_ID + 1, "Sender_Message_String", message_toSend, "0", Convo_ID,
//                            "0", "0", "0", "0", "0", logged_Username,
//                            Buyer_ID));
//
//                    last_index_array = Resulting_Chat_List.size();
//
//                    R_recycler_chats.getAdapter().notifyDataSetChanged();

                    HashMap<String, String> result_arr_2 = new HashMap<>();


                    result_arr_2.put("Message_Type", "Message_String");
                    result_arr_2.put("Message_Content", message_toSend);
                    result_arr_2.put("last_ID", last_ID);

                    Job_Sequence.add(result_arr_2);



                    R_recycler_chats.scrollToPosition(Resulting_Chat_List.size() - 1);
//
//                    RecyclerView.ViewHolder holder = R_recycler_chats.getChildViewHolder(R_recycler_chats.getChildAt(Resulting_Chat_List.size() - 1));
//                    ProgressBar progSending = holder.itemView.findViewById(R.id.chatitem_sender_progbar_msgstr_Sending);
//                    progSending.setVisibility(View.VISIBLE);

                    txtin_MessageStr_ToSend.setText("");

                    prog_Sending_Msg.setVisibility(View.VISIBLE);
                    txt_Sending_Msg_Label.setVisibility(View.VISIBLE);

                }


            }
        });

        img_Attach_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(act_conversations_proper.this);
                builder.setTitle("Choose an action:");

                // add a list
                String[] animals = {"Open Camera", "Open Gallery"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (ContextCompat.checkSelfPermission(act_conversations_proper.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                                        ContextCompat.checkSelfPermission(act_conversations_proper.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                                        ContextCompat.checkSelfPermission(act_conversations_proper.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                                    imageUri_camera = null;
                                    values = new ContentValues();
                                    values.put(MediaStore.Images.Media.TITLE, "Snapshot");
                                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                    imageUri_camera  = act_conversations_proper.this.getContentResolver().insert(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri_camera );

                                    startActivityForResult(intent, CAPTURE_ID_REQUEST);
                                    //Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_LONG).show();





                                }else {
                                    ActivityCompat.requestPermissions(act_conversations_proper.this, new String[]{
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                    }, 103);


                                }

                                break;

                            case 1:


                                if (ContextCompat.checkSelfPermission(act_conversations_proper.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                                        ContextCompat.checkSelfPermission(act_conversations_proper.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){


                                    imageUri_Gallery = null;

                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, GET_PHOTO_CODE);


                                }else {
                                    ActivityCompat.requestPermissions(act_conversations_proper.this, new String[]{
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                    }, 102);


                                }

                                break;





                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        prog_Sending_Msg.setVisibility(View.INVISIBLE);
                        txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);
                    }
                });





            }
        });




        img_Profile_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent ViewProfileAct = new Intent(act_conversations_proper.this, act_Profile_Preview.class);

                if(logged_Username.equals(Supplier_ID)){
                    ViewProfileAct.putExtra("profv_Username", Buyer_ID);
                }else{
                    ViewProfileAct.putExtra("profv_Username", Supplier_ID);
                }
                startActivity(ViewProfileAct);



            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_PHOTO_CODE) {
            if (resultCode == RESULT_OK && data != null) {

                imageUri_Gallery = data.getData();
                new Load_Cap_Image().execute();

            }else {
                Toast.makeText(act_conversations_proper.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }


        }else if(requestCode == CAPTURE_ID_REQUEST && imageUri_camera != null){
            if(resultCode == RESULT_OK){

                new Load_Cap_ID().execute();

            }


        }



    }


    class Load_Cap_Image extends AsyncTask<String, String, String> {

        Bitmap thumbnail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_conversations_proper.this);
            pDialog.setMessage("Processing your Photo. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected String doInBackground(String... args) {
            try {

                String file_path = getRealPathFromURI(imageUri_Gallery);
                ExifInterface ei = new ExifInterface(file_path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                thumbnail = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), imageUri_Gallery);
                Photo_to_Send = thumbnail;

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Photo_to_Send = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Photo_to_Send = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Photo_to_Send = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        Photo_to_Send = thumbnail;
                }

                Photo_to_Send = Bitmap.createScaledBitmap(Photo_to_Send, Photo_to_Send.getWidth() / 2, Photo_to_Send.getHeight() / 2, false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Photo_to_Send.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);



                json_message = "Done";

            } catch (Exception e) {
                e.printStackTrace();
                json_message = e.getMessage().toString();
                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(json_message.equals("Done") && Photo_to_Send != null){




                HashMap<String, String> result_arr_2 = new HashMap<>();


                result_arr_2.put("Message_Type", "Message_Image");
                result_arr_2.put("Message_Content", encodedImage);
                result_arr_2.put("last_ID", last_ID);

                Job_Sequence.add(result_arr_2);


//
//                    RecyclerView.ViewHolder holder = R_recycler_chats.getChildViewHolder(R_recycler_chats.getChildAt(Resulting_Chat_List.size() - 1));
//                    ProgressBar progSending = holder.itemView.findViewById(R.id.chatitem_sender_progbar_msgstr_Sending);
//                    progSending.setVisibility(View.VISIBLE);

                txtin_MessageStr_ToSend.setText("");


                prog_Sending_Msg.setVisibility(View.VISIBLE);
                txt_Sending_Msg_Label.setVisibility(View.VISIBLE);




                Toast.makeText(act_conversations_proper.this, "Done!", Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();
            }



        }

    }



    class Load_Cap_ID extends AsyncTask<String, String, String> {

        Bitmap thumbnail;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(act_conversations_proper.this);
            pDialog.setMessage("Processing Photo. Please wait...");
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
                Photo_to_Send = thumbnail;

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Photo_to_Send = rotateImage(thumbnail, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Photo_to_Send = rotateImage(thumbnail, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Photo_to_Send = rotateImage(thumbnail, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        Photo_to_Send = thumbnail;
                }

                Photo_to_Send = Bitmap.createScaledBitmap(Photo_to_Send, Photo_to_Send.getWidth() / 2, Photo_to_Send.getHeight() / 2, false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Photo_to_Send.compress(Bitmap.CompressFormat.JPEG,70,stream);
                byte[] byteArray = stream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);


                json_message = "Done";

            } catch (Exception e) {
                e.printStackTrace();
                json_message = e.getMessage().toString();
                //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            if(json_message.equals("Done") && Photo_to_Send != null){

                //TODO Send image to Database, Add it up to the Adapter (RecyclerView)






                HashMap<String, String> result_arr_2 = new HashMap<>();


                result_arr_2.put("Message_Type", "Message_Image");
                result_arr_2.put("Message_Content", encodedImage);
                result_arr_2.put("last_ID", last_ID);

                Job_Sequence.add(result_arr_2);

//
//                    RecyclerView.ViewHolder holder = R_recycler_chats.getChildViewHolder(R_recycler_chats.getChildAt(Resulting_Chat_List.size() - 1));
//                    ProgressBar progSending = holder.itemView.findViewById(R.id.chatitem_sender_progbar_msgstr_Sending);
//                    progSending.setVisibility(View.VISIBLE);

                txtin_MessageStr_ToSend.setText("");

                prog_Sending_Msg.setVisibility(View.VISIBLE);
                txt_Sending_Msg_Label.setVisibility(View.VISIBLE);


            }else{
                Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();
            }



        }

    }








    class Load_Messages extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
            //progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {

                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadMessages.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Ch_Last_Chat_ID", last_ID);
                data_1.put("Ch_First_Load_Flag", "True");
                data_1.put("Ch_User_Logged", logged_Username);
                data_1.put("Ch_Convo_ID", Convo_ID);


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
                    if (jsonObj.getString("ChatResult").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("ChatResult");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Convo_ID", c.getString("Convo_ID"));
                            result_arr.put("Message_Type", c.getString("Message_Type"));
                            result_arr.put("Message_Content", c.getString("Message_Content"));
                            result_arr.put("Date_Sent", c.getString("Date_Sent"));
                            result_arr.put("Sel_Prod_ID", c.getString("Sel_Prod_ID"));
                            result_arr.put("Sel_Product_Name", c.getString("Sel_Product_Name"));
                            result_arr.put("Sel_Product_Price", c.getString("Sel_Product_Price"));
                            result_arr.put("Sel_Prod_Date_Added", c.getString("Sel_Prod_Date_Added"));
                            result_arr.put("Sel_Seller_ID", c.getString("Sel_Seller_ID"));

                            // adding contact to contact list
                            Resulting_Chat_List.add(result_arr);



                        }

                        json_message = "List Loaded";
                    }
                    // Getting JSON Array node

                }else {
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
            //progBar.setVisibility(View.INVISIBLE);
            swiper_item_container.setRefreshing(false);

            if(json_message.equals("List Loaded")){

                if(Resulting_Chat_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Chat_List.size(); i++){

                        String Chat_ID;
                        String Convo_ID;
                        String Message_Type;
                        String Message_Content;
                        String Date_Sent;
                        String Sel_Prod_ID;
                        String Sel_Product_Name;
                        String Sel_Product_Price;
                        String Sel_Prod_Date_Added;
                        String Sel_Seller_ID;




                        HashMap<String, String> currentRow = Resulting_Chat_List.get(i);

                        Chat_ID = currentRow.get("ID");
                        last_ID = Chat_ID;
                        Convo_ID = currentRow.get("Convo_ID");
                        Message_Type = currentRow.get("Message_Type");
                        Message_Content = currentRow.get("Message_Content");
                        Date_Sent = currentRow.get("Date_Sent");
                        Sel_Prod_ID = currentRow.get("Sel_Prod_ID");
                        Sel_Product_Name = currentRow.get("Sel_Product_Name");
                        Sel_Product_Price = currentRow.get("Sel_Product_Price");
                        Sel_Prod_Date_Added = currentRow.get("Sel_Prod_Date_Added");
                        Sel_Seller_ID = currentRow.get("Sel_Seller_ID");



                        chat_list.add(new chat_variable_links(Chat_ID, Message_Type, Message_Content, Date_Sent, Convo_ID,
                                Sel_Prod_ID, Sel_Product_Name, Sel_Product_Price, Sel_Prod_Date_Added,
                                Sel_Seller_ID, logged_Username, Buyer_ID));


                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);

                    R_recycler_chats.setAdapter(new chat_Recycler_Adapter(act_conversations_proper.this, chat_list, new chat_Recycler_Adapter.OnItemClickListener() {

                        @Override
                        public void onItemClick(int position, String Type_Clicked, String Univ_Var) {


                            if(Type_Clicked.equals("Image_Message")){

                                Intent SinglePhotoPreview = new Intent(act_conversations_proper.this, act_Single_Photo_Fullscreen.class);

                                SinglePhotoPreview.putExtra("SPP_Media_Type", "URL");
                                SinglePhotoPreview.putExtra("SPP_Media_Data", Univ_Var);

                                startActivity(SinglePhotoPreview);




                            }else if(Type_Clicked.equals("Order_Link")){



                                Intent OrderTrackingList = new Intent(act_conversations_proper.this, act_Order_Tracker_List.class);

                                OrderTrackingList.putExtra("OrdList_Logged_Username", logged_Username);
                                OrderTrackingList.putExtra("OrdList_Order_Status_Display", "Negotiation");


                                startActivity(OrderTrackingList);






                            }

                        }

                    }));



                    last_index_array = Resulting_Chat_List.size() ;

                    R_recycler_chats.scrollToPosition(Resulting_Chat_List.size() - 1);
                    startTimer_MessageQuery();



                }




            }else if(json_message.equals("No Records Found.")){


                Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

                String finmsg = json_message;
                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
                finmsg = finmsg.replace("/", "");
                Toast.makeText(act_conversations_proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }

            New_Message_Fetcher_busy = false;
        }

    }







    class Load_Next_Messages extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
            //progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/LoadMessages.php";


                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Ch_Last_Chat_ID", last_ID);
                data_1.put("Ch_First_Load_Flag", "False");
                data_1.put("Ch_User_Logged", logged_Username);
                data_1.put("Ch_Convo_ID", Convo_ID);


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
                    if (jsonObj.getString("ChatResult").equals("No Records Found.")){
                        json_message = "No Records Found.";

                    }else{
                        JSONArray Cart_Json = jsonObj.getJSONArray("ChatResult");
                        // adding each child node to HashMap key => value

                        for (int i = 0; i < Cart_Json.length(); i++) {

                            JSONObject c = Cart_Json.getJSONObject(i);


                            HashMap<String, String> result_arr = new HashMap<>();

                            // adding each child node to HashMap key => value

                            result_arr.put("ID", c.getString("ID"));
                            result_arr.put("Convo_ID", c.getString("Convo_ID"));
                            result_arr.put("Message_Type", c.getString("Message_Type"));
                            result_arr.put("Message_Content", c.getString("Message_Content"));
                            result_arr.put("Date_Sent", c.getString("Date_Sent"));
                            result_arr.put("Sel_Prod_ID", c.getString("Sel_Prod_ID"));
                            result_arr.put("Sel_Product_Name", c.getString("Sel_Product_Name"));
                            result_arr.put("Sel_Product_Price", c.getString("Sel_Product_Price"));
                            result_arr.put("Sel_Prod_Date_Added", c.getString("Sel_Prod_Date_Added"));
                            result_arr.put("Sel_Seller_ID", c.getString("Sel_Seller_ID"));

                            // adding contact to contact list
                            Resulting_Chat_List.add(result_arr);



                        }

                        json_message = "List Loaded";
                    }
                }else {
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
            //progBar.setVisibility(View.INVISIBLE);
            swiper_item_container.setRefreshing(false);



            if(json_message.equals("List Loaded")){



                if(Resulting_Chat_List.size() > 0){

                    for(int i = last_index_array; i < Resulting_Chat_List.size(); i++){

                        String Chat_ID;
                        String Convo_ID;
                        String Message_Type;
                        String Message_Content;
                        String Date_Sent;
                        String Sel_Prod_ID;
                        String Sel_Product_Name;
                        String Sel_Product_Price;
                        String Sel_Prod_Date_Added;
                        String Sel_Seller_ID;




                        HashMap<String, String> currentRow = Resulting_Chat_List.get(i);

                        Chat_ID = currentRow.get("ID");
                        last_ID = Chat_ID;
                        Convo_ID = currentRow.get("Convo_ID");
                        Message_Type = currentRow.get("Message_Type");
                        Message_Content = currentRow.get("Message_Content");
                        Date_Sent = currentRow.get("Date_Sent");
                        Sel_Prod_ID = currentRow.get("Sel_Prod_ID");
                        Sel_Product_Name = currentRow.get("Sel_Product_Name");
                        Sel_Product_Price = currentRow.get("Sel_Product_Price");
                        Sel_Prod_Date_Added = currentRow.get("Sel_Prod_Date_Added");
                        Sel_Seller_ID = currentRow.get("Sel_Seller_ID");


                        chat_list.add(new chat_variable_links(Chat_ID, Message_Type, Message_Content, Date_Sent, Convo_ID,
                                Sel_Prod_ID, Sel_Product_Name, Sel_Product_Price, Sel_Prod_Date_Added,
                                Sel_Seller_ID, logged_Username, Buyer_ID));


                    }


//                    prod_adapter = new prod_Recycler_Adapter(act_Selling_Products_View.this, prod_list, mListener);
//                    rec_product_list.setAdapter(prod_adapter);



                    last_index_array = Resulting_Chat_List.size() ;

                    R_recycler_chats.getAdapter().notifyDataSetChanged();
                    R_recycler_chats.scrollToPosition(Resulting_Chat_List.size() - 1);



                }


            }else if(json_message.equals("No Records Found.")){


                //Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();

            }else if(json_message.equals("No Response from server")){


                //Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

//                String finmsg = json_message;
//                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
//                finmsg = finmsg.replace("/", "");
//                Toast.makeText(act_conversations_proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }


            New_Message_Fetcher_busy = false;
        }

    }

    void startTimer_MessageQuery() {
        cTimer_1 = new CountDownTimer(1000, 100) {
            public void onTick(long millisUntilFinished) {
//                String tmp = getString(R.string.timer_label) + " " + millisUntilFinished / 1000;
//                txt_Resend_OTP.setText(tmp);

                if(!New_Message_Fetcher_busy){

                    New_Message_Fetcher_busy = true;
                    new Load_Next_Messages().execute();

                }

            }
            public void onFinish() {
                startTimer_MessageQuery();


            }
        };
        cTimer_1.start();
    }



    void startTimer_Job_Sequencer() {
        cTimer_2 = new CountDownTimer(1000, 100) {
            public void onTick(long millisUntilFinished) {
//                String tmp = getString(R.string.timer_label) + " " + millisUntilFinished / 1000;
//                txt_Resend_OTP.setText(tmp);

                if(!Sender_is_Busy){

                    if(Job_Sequence.size() > 0) {
                        //New_Message_Fetcher_busy = true;
                        Sender_is_Busy = true;

                        HashMap<String, String> currentRow = Job_Sequence.get(0);

                        if (currentRow.get("Message_Type").equals("Message_String")) {

                            prog_Sending_Msg.setVisibility(View.VISIBLE);
                            txt_Sending_Msg_Label.setVisibility(View.VISIBLE);

                            new Send_Message_String().execute();
                        } else if (currentRow.get("Message_Type").equals("Message_Image")) {

                            prog_Sending_Msg.setVisibility(View.VISIBLE);
                            txt_Sending_Msg_Label.setVisibility(View.VISIBLE);

                            new Send_Message_Image().execute();

                        }

                    }

//                    }else{
//                        //New_Message_Fetcher_busy = false;
//                    }



                }

            }
            public void onFinish() {
                startTimer_Job_Sequencer();


            }
        };
        cTimer_2.start();
    }



    class Send_Message_Image extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
            //progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/NewMessage_Image.php";

                HashMap<String, String> currentRow = Job_Sequence.get(0);



                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Msg_logged_Username", logged_Username);

                if(logged_Username.equals(Supplier_ID)){

                    data_1.put("Msg_Seller_Username", Buyer_ID);
                }else{
                    data_1.put("Msg_Seller_Username", Supplier_ID);

                }

                data_1.put("Msg_Convo_ID", Convo_ID);
                data_1.put("Msg_Message_Content", currentRow.get("Message_Content"));


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


                }else {
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
            //progBar.setVisibility(View.INVISIBLE);
            //swiper_item_container.setRefreshing(false);
            if(json_message.equals("Message_Sent")){


                Job_Sequence.remove(0);

                if(Job_Sequence.size() > 0){

                    prog_Sending_Msg.setVisibility(View.VISIBLE);
                    txt_Sending_Msg_Label.setVisibility(View.VISIBLE);
                }else{
                    prog_Sending_Msg.setVisibility(View.INVISIBLE);
                    txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);

                }



            }else if(json_message.equals("No Response from server")){

                prog_Sending_Msg.setVisibility(View.INVISIBLE);
                txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);


                //Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();

            }else{

//                String finmsg = json_message;
//                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
//                finmsg = finmsg.replace("/", "");
//                Toast.makeText(act_conversations_proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();

                prog_Sending_Msg.setVisibility(View.INVISIBLE);
                txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);


                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }

            Sender_is_Busy = false;
        }

    }



    class Send_Message_String extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Uploading Profile Photo. Please wait.");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
            //progBar.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(String... args) {



            try {



                String link;

                link = "https://" +  getString(R.string.Server_Web_Host_IP ) + "BILICACIES/NewMessage_String.php";

                HashMap<String, String> currentRow = Job_Sequence.get(0);



                HashMap<String,String> data_1 = new HashMap<>();
                data_1.put("Msg_logged_Username", logged_Username);
                if(logged_Username.equals(Supplier_ID)){

                    data_1.put("Msg_Seller_Username", Buyer_ID);
                }else{
                    data_1.put("Msg_Seller_Username", Supplier_ID);

                }

                data_1.put("Msg_Convo_ID", Convo_ID);
                data_1.put("Msg_Message_Content", currentRow.get("Message_Content"));


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


                }else {
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
            //progBar.setVisibility(View.INVISIBLE);
            //swiper_item_container.setRefreshing(false);
            if(json_message.equals("Message_Sent")){



                Job_Sequence.remove(0);

                if(Job_Sequence.size() > 0){

                    prog_Sending_Msg.setVisibility(View.VISIBLE);
                    txt_Sending_Msg_Label.setVisibility(View.VISIBLE);
                }else{
                    prog_Sending_Msg.setVisibility(View.INVISIBLE);
                    txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);

                }



            }else if(json_message.equals("No Response from server")){


//                Toast.makeText(act_conversations_proper.this, json_message, Toast.LENGTH_LONG).show();
                prog_Sending_Msg.setVisibility(View.INVISIBLE);
                txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);
            }else{

//                String finmsg = json_message;
//                finmsg = finmsg.replace( getResources().getString(R.string.Server_Web_Host_IP) + ":8080", "Server");
//                finmsg = finmsg.replace("/", "");
//                Toast.makeText(act_conversations_proper.this, finmsg + "\nException L253", Toast.LENGTH_LONG).show();

                prog_Sending_Msg.setVisibility(View.INVISIBLE);
                txt_Sending_Msg_Label.setVisibility(View.INVISIBLE);
                //Toast.makeText(act_Photo_ID.this,  json_message.replace(getString(R.string.Webshost_IP ), "Server").replace("/", "") , Toast.LENGTH_LONG).show();
                //Toast.makeText(act_Photo_ID.this, "Server Error!\nException: 584", Toast.LENGTH_LONG).show();
            }

            Sender_is_Busy = false;
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
}