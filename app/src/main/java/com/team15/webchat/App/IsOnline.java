package com.team15.webchat.App;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class IsOnline extends Service {
//    private UserRepository repository;
//    private SessionManager sessionManager;
//    private String api_key, user_id;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
//        repository = UserRepository.getInstance();
//        sessionManager = new SessionManager(this);
//        HashMap<String, String> userInfo = sessionManager.get_user();
//
//        user_id = userInfo.get(SessionManager.USER_ID);
//        api_key = userInfo.get(SessionManager.API_KEY);
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    private void onAppBackgrounded() {
//        repository.isOnline("Bearer " + api_key, user_id,"0");
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    private void onAppForegrounded() {
//        repository.isOnline("Bearer " + api_key, user_id,"1");
//    }




        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("ClearFromRecentService", "Service Started");
            return START_NOT_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("ClearFromRecentService", "Service Destroyed");
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            Log.e("ClearFromRecentService", "END");
            //Code here
            stopSelf();
        }
}