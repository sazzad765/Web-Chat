package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.team15.webchat.App.ImageDownload;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

public class ImageViewActivity extends AppCompatActivity {

    private Button btnSave;
    ImageView imgBack;
    PhotoView imgChat;
    PhotoViewAttacher photoViewAttacher ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        btnSave = findViewById(R.id.btnSave);
        imgChat= findViewById(R.id.imgChat);
        imgBack = findViewById(R.id.imgBack);

        Bundle extras = getIntent().getExtras();
        final String url = extras.getString("url");

        Glide.with(this)
                .load(url)
                .into(imgChat);
        photoViewAttacher = new PhotoViewAttacher(imgChat);

        photoViewAttacher.update();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    new ImageDownload(ImageViewActivity.this).downloadImage(url);
                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do somethings
        }
    }

}