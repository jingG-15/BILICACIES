package com.holanda.bilicacies.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;


import com.holanda.bilicacies.Adapters.MyAdapter;
import com.holanda.bilicacies.R;





public class act_Add_Product_Form extends AppCompatActivity {


    EditText txtinProductName, txtinPrice, txtinDesc;

    Button btnProceed;

    String loggedUsername;

    int UPLOAD_ACT_REQ = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_form);

        txtinDesc = findViewById(R.id.prod_add_txtin_Description);
        txtinPrice = findViewById(R.id.prod_add_txtin_Price);
        txtinProductName = findViewById(R.id.prod_add_txtin_Product_Name);
        btnProceed = findViewById(R.id.prod_add_btn_Proceed);

        loggedUsername = getIntent().getExtras().getString("Logged_Username");

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strProdName = txtinProductName.getText().toString().trim();
                String strPrice = txtinPrice.getText().toString().trim();
                String strDesc = txtinDesc.getText().toString().trim();

                if(!(strProdName.equals("") || strProdName.isEmpty() || strProdName.isEmpty() ||
                        strDesc.equals("") || strDesc.isEmpty() || strDesc.isEmpty() ||
                        strPrice.equals("") || strPrice.isEmpty() || strPrice.isEmpty())){

                    Intent UploadActivity = new Intent(act_Add_Product_Form.this, act_Upload_Product_Photos.class);

                    UploadActivity.putExtra("Upd_Product_Name", strProdName);
                    UploadActivity.putExtra("Upd_Product_Price", strPrice);
                    UploadActivity.putExtra("Upd_Product_Desc", strDesc);
                    UploadActivity.putExtra("Logged_Username", loggedUsername);

                    startActivityForResult(UploadActivity, UPLOAD_ACT_REQ);


                }


            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPLOAD_ACT_REQ){
            if(resultCode == RESULT_OK){

                Intent intent = new Intent();

                setResult(RESULT_OK, intent);

                finish();


            }


        }




  }



}