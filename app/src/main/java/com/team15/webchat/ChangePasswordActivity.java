package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editOldPass,editNewPass,editConfirmPass;
    private Button btnDone;
    private TextView txtWarning;
    SessionManager sessionManager;

    private AppViewModel appViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        final String userId = userInfo.get(SessionManager.USER_ID);
        final String api = userInfo.get(SessionManager.API_KEY);


        editOldPass = findViewById(R.id.editOldPass);
        editNewPass = findViewById(R.id.editNewPass);
        editConfirmPass = findViewById(R.id.editConfirmPass);
        btnDone = findViewById(R.id.btnDone);
        txtWarning = findViewById(R.id.txtWarning);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change(userId,api);
            }
        });

    }

    private void change(String userId,String api){
        if (userId==null){
            return;
        }
        txtWarning.setVisibility(View.GONE);

        final String oldPass = editOldPass.getText().toString().trim();
        final String newPass = editNewPass.getText().toString().trim();
        final String ConfirmPass = editConfirmPass.getText().toString().trim();
        if (oldPass.length() == 0) {
            editOldPass.requestFocus();
            editOldPass.setError("FIELD CANNOT BE EMPTY");
            return;
        }
        if (newPass.length() == 0) {
            editNewPass.requestFocus();
            editNewPass.setError("FIELD CANNOT BE EMPTY");
            return;
        }
        if (newPass.length() < 5) {
            editNewPass.requestFocus();
            editNewPass.setError("PASSWORD MINIMUM 6 DIGIT");
            return;
        }
        if (ConfirmPass.length() == 0) {
            editConfirmPass.requestFocus();
            editConfirmPass.setError("FIELD CANNOT BE EMPTY");
            return;
        }
        if (!newPass.equals(ConfirmPass)) {
            editConfirmPass.requestFocus();
            editConfirmPass.setError("CONFIRM PASSWORD NOT MATCH");
            return;
        }
        appViewModel.password_change("Bearer " + api,userId,oldPass,newPass).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (apiResponse!= null){
                    if (apiResponse.getSuccess()==0){
                        txtWarning.setVisibility(View.VISIBLE);
                        txtWarning.setText(apiResponse.getMessage());
                    }else {
                        txtWarning.setVisibility(View.GONE);
                        editNewPass.setText("");
                        editConfirmPass.setText("");
                        editOldPass.setText("");
                        Toast.makeText(ChangePasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}