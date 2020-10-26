package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Model.Login;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.UserViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextPhone, editTextPassword;
    private Button btnLogin;
    private TextView btnRegistration;
    private UserViewModel userViewModel;
    SessionManager sessionManager;
    DeviceReg deviceReg;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistration = findViewById(R.id.btnRegistration);
        sessionManager = new SessionManager(this);
        token = FirebaseInstanceId.getInstance().getToken();

        btnLogin.setOnClickListener(this);
        btnRegistration.setOnClickListener(this);
    }

    private void submitForm() {
        final String phone = editTextPhone.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        if (phone.length() == 0) {
            editTextPhone.requestFocus();
            editTextPhone.setError("FIELD CANNOT BE EMPTY");
            return;
        } else if (password.length() == 0) {
            editTextPassword.requestFocus();
            editTextPassword.setError("FIELD CANNOT BE EMPTY");
            return;
        }


        userViewModel.getLogin(phone, password).observe(this, new Observer<Login>() {
            @Override
            public void onChanged(Login response) {
                int success = response.getSuccess();
                String message = response.getMessage();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                if (success == 1) {
                    String api_key = response.getLoginInfo().get(0).getApiToken();
                    String key_Type = response.getLoginInfo().get(0).getTokenType();
                    String user_id = response.getLoginInfo().get(0).getUserId().toString();
                    String user_type = response.getLoginInfo().get(0).getType();
                    deviceReg = new DeviceReg("Bearer " + api_key,token,user_id);
                    updateDeviceId();

                    sessionManager.createSession(api_key, key_Type, user_id, user_type);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            submitForm();
//            startActivity(new Intent(this,MainActivity.class));
        } else if (v == btnRegistration) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }
    }

    private void updateDeviceId() {
        userViewModel.updateDeviceId(deviceReg);
    }
}