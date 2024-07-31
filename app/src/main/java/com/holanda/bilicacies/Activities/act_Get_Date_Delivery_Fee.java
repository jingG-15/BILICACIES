package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.holanda.bilicacies.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class act_Get_Date_Delivery_Fee extends AppCompatActivity {

    EditText txtin_Delivery_Date, txtin_Delivery_Fee;
    Button btn_Confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_date_delivery_fee);


        txtin_Delivery_Date = findViewById(R.id.gdd_txtin_Delivery_Date);
        txtin_Delivery_Fee = findViewById(R.id.gdd_txtin_Delivery_Fee);
        btn_Confirm = findViewById(R.id.gdd_btn_Confirm);


        final Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                String myFormat = "yyyy-MM-dd HH:mm:ss"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

                txtin_Delivery_Date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        txtin_Delivery_Date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(act_Get_Date_Delivery_Fee.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String del_date = txtin_Delivery_Date.getText().toString();
                String del_fee = txtin_Delivery_Fee.getText().toString();

                if(del_date.isEmpty() || del_fee.isEmpty()){

                    Toast.makeText(act_Get_Date_Delivery_Fee.this, "Date and fee cannot be empty.", Toast.LENGTH_LONG).show();

                }else{

                    Intent intent = new Intent();
                    intent.putExtra("Delivery_Date", del_date);
                    intent.putExtra("Delivery_Fee", del_fee);
                    setResult(RESULT_OK, intent);
                    finish();

                }


            }
        });



    }
}