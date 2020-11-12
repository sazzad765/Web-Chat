package com.team15.webchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.User;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText edtName, edtPhone;
    private ImageView updateProfileImage, imgBack;
    private Button btnSave;
    private ProgressBar updateProgressBar;

    UserViewModel userViewModel;
    private SessionManager sessionManager;
    private String userId, api;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        imgBack = findViewById(R.id.imgBack);
        updateProfileImage = findViewById(R.id.updateProfileImage);
        btnSave = findViewById(R.id.btnSave);
        updateProgressBar = findViewById(R.id.updateProgressBar);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        setProfile();

    }

    private void setProfile() {
        updateProgressBar.setVisibility(View.VISIBLE);
        userViewModel.getUser("Bearer " + api, userId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                updateProgressBar.setVisibility(View.INVISIBLE);

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
        updateProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
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
        updateProgressBar.setVisibility(View.VISIBLE);

        userViewModel.profileUpdate("Bearer " + apiKey, userId, name, phone, email).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                updateProgressBar.setVisibility(View.INVISIBLE);
                String message = apiResponse.getMessage();
                Toast.makeText(UpdateProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadFile(Uri uri) {
        String filePath = getRealPathFromURIPath(uri, this);
        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), userId);

        updateProgressBar.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);
        Call<ApiResponse> call = apiInterface.uploadProfileImg("Bearer " + api, fileToUpload, filename, id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                updateProgressBar.setVisibility(View.INVISIBLE);
                setProfile();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                updateProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void pickImage() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PICK_IMAGE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(UpdateProfileActivity.this, "you must be accept", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri postImageUri = data.getData();
            uploadFile(postImageUri);
        }
    }

}