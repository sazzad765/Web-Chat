package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText editNumber;
    private Button btnDone;
    private ProgressBar regProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        editNumber = findViewById(R.id.editNumber);
        btnDone = findViewById(R.id.btnDone);
        regProgressBar = findViewById(R.id.regProgressBar);


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void check(){
        String number = editNumber.getText().toString();

        if (number.length()<11){
            editNumber.requestFocus();
            editNumber.setError("Enter valid number");
            return;
        }
        if (number.length()>11){
            editNumber.requestFocus();
            editNumber.setError("Enter valid number");
            return;
        }
        if (!TextUtils.isDigitsOnly(number)){
            editNumber.requestFocus();
            editNumber.setError("Enter valid number");
            return;
        }

//        Intent intent = new Intent(ForgetPasswordActivity.this,OTPVerificationActivity.class);
//        intent.putExtra("number",number);
//        startActivity(intent);
    }
}