package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.User;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText edtName, edtPhone;
    private ImageView updateProfileImage, imgBack;
    private Button btnSave;

    UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_update_profile);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        imgBack = findViewById(R.id.imgBack);
        updateProfileImage = findViewById(R.id.updateProfileImage);
        btnSave = findViewById(R.id.btnSave);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        final String userId = userInfo.get(SessionManager.USER_ID);
        final String api = userInfo.get(SessionManager.API_KEY);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getUser("Bearer " + api, userId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                edtName.setText(user.getName());
                edtPhone.setText(user.getPhone());
                Glide
                        .with(UpdateProfileActivity.this)
                        .load(user.getImage())
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(updateProfileImage);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm(api, userId);
            }
        });
    }

    private void submitForm(String apiKey, String userId) {
        final String name = edtName.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();
        final String email = "default";

        if (name.length() == 0) {
            edtName.requestFocus();
            edtName.setError("FIELD CANNOT BE EMPTY");
            return;
        }
        if (phone.length() == 0) {
            edtPhone.requestFocus();
            edtPhone.setError("FIELD CANNOT BE EMPTY");
            return;
        }


        userViewModel.profileUpdate("Bearer " + apiKey,userId,name,phone,email).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                String message = apiResponse.getMessage();
                Toast.makeText(UpdateProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}