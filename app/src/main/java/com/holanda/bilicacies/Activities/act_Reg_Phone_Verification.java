package com.holanda.bilicacies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.holanda.bilicacies.R;

import java.util.concurrent.TimeUnit;

public class act_Reg_Phone_Verification extends AppCompatActivity {


    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String Auth_Phone_Number;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;

    TextView txt_Phone_Num_Display;
    EditText txtin_OTP;
    TextView txt_Resend_OTP;
    Button btn_Verify;

    CountDownTimer cTimer = null;
    Boolean tmr_in_progress = true;

    ProgressBar prog_send;
    TextView txt_Sending_Disp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_phone_verification);

        txt_Phone_Num_Display = findViewById(R.id.regver_txt_Phone_Number);
        txtin_OTP = findViewById(R.id.regver_txt_OTP);
        txt_Resend_OTP = findViewById(R.id.regver_txt_Resend);
        btn_Verify = findViewById(R.id.regver_btn_Verify);

        Auth_Phone_Number =  getIntent().getExtras().getString("Reg_Mobile_Number");
        txt_Phone_Num_Display.setText(Auth_Phone_Number);
        Auth_Phone_Number = "+63" + Auth_Phone_Number.substring(1,11);

        prog_send = findViewById(R.id.regver_progressBar);
        txt_Sending_Disp = findViewById(R.id.regver_txt_sending);


        mAuth = FirebaseAuth.getInstance();


        if (ContextCompat.checkSelfPermission(act_Reg_Phone_Verification.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

            sendVerificationCode(Auth_Phone_Number);




        }else {
            ActivityCompat.requestPermissions(act_Reg_Phone_Verification.this, new String[]{
                    Manifest.permission.INTERNET
            }, 106);


        }


        prog_send.setVisibility(View.VISIBLE);
        txt_Sending_Disp.setVisibility(View.VISIBLE);
        btn_Verify.setVisibility(View.INVISIBLE);




        btn_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP_Enter = txtin_OTP.getText().toString();
                if(!(OTP_Enter.isEmpty() || OTP_Enter.equals("") || OTP_Enter == null ||
                        mVerificationId == null || mVerificationId.isEmpty() || mVerificationId.equals(""))){

                    verifyCode(OTP_Enter);

                }

            }
        });



    }

    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.

                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            txtin_OTP.setError("Invalid OTP Entered!");
                            Toast.makeText(act_Reg_Phone_Verification.this, "Invalid OTP received. Please verify code and enter again.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, // first parameter is user's mobile number
                60, // second parameter is time limit for OTP
                // verification which is 60 seconds in our case.
                TimeUnit.SECONDS, // third parameter is for initializing units
                // for time period which is in seconds in our case.
                this, // this task will be excuted on Main thread.
                mCallBack // we are calling callback method when we recieve OTP for
                // auto verification of user.
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            mVerificationId = s;
            mResendToken = forceResendingToken;
            tmr_in_progress = true;
            startTimer();
            Toast.makeText(act_Reg_Phone_Verification.this, "OTP Code Sent! Please wait.", Toast.LENGTH_LONG).show();

            prog_send.setVisibility(View.INVISIBLE);
            txt_Sending_Disp.setVisibility(View.INVISIBLE);
            btn_Verify.setVisibility(View.VISIBLE);
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                txtin_OTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(act_Reg_Phone_Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };



    void startTimer() {
        cTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                String tmp = getString(R.string.timer_label) + " " + millisUntilFinished / 1000;
                txt_Resend_OTP.setText(tmp);

            }
            public void onFinish() {
                String tmp = "Resend OTP";
                txt_Resend_OTP.setText(tmp);
                txt_Resend_OTP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ContextCompat.checkSelfPermission(act_Reg_Phone_Verification.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                            prog_send.setVisibility(View.VISIBLE);
                            txt_Sending_Disp.setVisibility(View.VISIBLE);
                            btn_Verify.setVisibility(View.INVISIBLE);

                            sendVerificationCode(Auth_Phone_Number);



                        }else {
                            ActivityCompat.requestPermissions(act_Reg_Phone_Verification.this, new String[]{
                                    Manifest.permission.INTERNET
                            }, 106);


                        }


                    }
                });
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancelTimer();
    }
}