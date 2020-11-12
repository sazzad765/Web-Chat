package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Model.User;
import com.team15.webchat.ViewModel.UserViewModel;

public class RegistrationActivity extends AppCompatActivity {
private ProgressBar regProgressBar;
    private TextView editTextPersonName, editTextPhone, editTextPassword;
    private Button btnReg;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        init();
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        final String name = editTextPersonName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String email = "default";
        final String appId = "21212";

        if (name.length() == 0) {
            editTextPersonName.requestFocus();
            editTextPersonName.setError("FIELD CANNOT BE EMPTY");
            return;
        }
        if (phone.length() == 0) {
            editTextPhone.requestFocus();
            editTextPhone.setError("FIELD CANNOT BE EMPTY");
            return;
        }
        if (phone.length() < 10) {
            editTextPhone.requestFocus();
            editTextPhone.setError("enter valid number");
            return;
        }
        if (password.length() == 0) {
            editTextPassword.requestFocus();
            editTextPassword.setError("FIELD CANNOT BE EMPTY");
            return;
        }

        regProgressBar.setVisibility(View.VISIBLE);
        userViewModel.registration(name,phone,password,appId,email).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                regProgressBar.setVisibility(View.INVISIBLE);
                String message = apiResponse.getMessage();
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                if (apiResponse.getSuccess().equals(1)){
                    finish();
                }
            }
        });
    }


    private void init() {
        regProgressBar = findViewById(R.id.regProgressBar);
        editTextPersonName = findViewById(R.id.editTextPersonName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnReg = findViewById(R.id.btnReg);
    }
}