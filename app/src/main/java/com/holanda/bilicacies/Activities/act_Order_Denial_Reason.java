package com.holanda.bilicacies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.holanda.bilicacies.R;

public class act_Order_Denial_Reason extends AppCompatActivity {

    Button btn_Proceed;
    EditText txtin_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_denial_reason);

        btn_Proceed = findViewById(R.id.orddeny_btn_Proceed);
        txtin_reason = findViewById(R.id.orddeny_txtin_Denial_reason);

        btn_Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason_deny = txtin_reason.getText().toString();


                if(reason_deny.isEmpty()){

                    Toast.makeText(act_Order_Denial_Reason.this, "Denial purpose cannot be empty", Toast.LENGTH_LONG).show();

                }else{



                    Intent intent = new Intent();
                    intent.putExtra("Denial_Purpose", reason_deny);
                    setResult(RESULT_OK, intent);
                    finish();



                }



            }
        });




    }
}