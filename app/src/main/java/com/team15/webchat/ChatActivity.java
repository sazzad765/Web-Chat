package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_chat);
    }
}