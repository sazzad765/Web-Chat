package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.team15.webchat.Session.SessionManager;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        sessionManager= new SessionManager(this);
        Timer timer = new Timer();
        long delay = 1000;
        timer.schedule(task, delay);


    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            if (sessionManager.isLogin()){
                Intent intent = new Intent(SplashActivity.this,
                        MainActivity.class).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                Intent in = new Intent().setClass(SplashActivity.this,
                        LoginActivity.class).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                finish();
            }


        }
    };
}