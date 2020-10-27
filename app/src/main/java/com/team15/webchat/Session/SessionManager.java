package com.team15.webchat.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.team15.webchat.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public Context context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String API_KEY = "API_KEY";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String USER_ID = "USER_ID";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String SELLER_ID = "SELLER_ID";


    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
        editor.commit();
    }

    public void createSession(String api_key,String key_type,String user_id, String user_type,String seller_id){

        editor.putBoolean(LOGIN, true);
        editor.putString(API_KEY, api_key);
        editor.putString(KEY_TYPE, key_type);
        editor.putString(USER_ID, user_id);
        editor.putString(USER_TYPE, user_type);
        editor.putString(SELLER_ID, seller_id);
        editor.apply();
    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (!this.isLogin()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP );
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public HashMap<String, String> get_user() {
        HashMap<String, String> user = new HashMap<>();
        user.put(API_KEY, sharedPreferences.getString(API_KEY, null));
        user.put(KEY_TYPE, sharedPreferences.getString(KEY_TYPE, null));
        user.put(USER_ID, sharedPreferences.getString(USER_ID, null));
        user.put(USER_TYPE, sharedPreferences.getString(USER_TYPE, null));
        user.put(SELLER_ID, sharedPreferences.getString(SELLER_ID, null));

        return user;
    }

}

