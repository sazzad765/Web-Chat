package com.team15.webchat.Heads;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.team15.webchat.Adapters.ChatAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeadViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private boolean isOpen;

    private RecyclerView recyclerChat;
    private SessionManager sessionManager;
    private ChatAdapter chatAdapter;
    private String api, userId, receiverId;
    private EditText edit_send;
    private ImageView btn_send, profile_image, chatHeadImage;
    private TextView username;
    List<Chat> chat;
    APIInterface apiInterface;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public HeadViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.head_layout, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        isOpen = false;

        chatHeadImage = mFloatingView.findViewById(R.id.collapsed_iv);
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            if (isOpen) {
                                expandedView.setVisibility(View.GONE);
                                isOpen = false;
                            } else {
                                expandedView.setVisibility(View.VISIBLE);
                                isOpen = true;
                            }

                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        receiverId = userInfo.get(SessionManager.SELLER_ID);

        chat = new ArrayList<>();

        recyclerChat = mFloatingView.findViewById(R.id.recyclerChat);
        edit_send = mFloatingView.findViewById(R.id.text_send);
        btn_send = mFloatingView.findViewById(R.id.btn_send);
        profile_image = mFloatingView.findViewById(R.id.profile_image);
        username = mFloatingView.findViewById(R.id.username);

        chatAdapter = new ChatAdapter(this, chat, userId);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        apiInterface = APIClient.getRetrofitInstance().create(APIInterface.class);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edit_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, "text");
                }
                edit_send.setText("");
            }
        });

        loadFirstPage();
        setProfile();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {

                }
                loadFirstPage();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

    }


    private void setProfile() {
        Call<User> call2 = apiInterface.getUser("Bearer " + api, receiverId);
        call2.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                assert response.body() != null;
                username.setText(response.body().getName());
                Glide.with(HeadViewService.this).load(response.body().getImage()).apply(RequestOptions.centerCropTransform()).into(chatHeadImage);
                Glide.with(HeadViewService.this).load(response.body().getImage()).apply(RequestOptions.centerCropTransform()).into(profile_image);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void sendMessage(String message, String type) {

        Chat chat1 = new Chat(receiverId, userId, message, Integer.parseInt(Config.APP_ID), 0, " ", type);
        chat.add(chat1);
        chatAdapter.notifyDataSetChanged();
        if (chatAdapter.getItemCount() > 1) {
            recyclerChat.getLayoutManager().smoothScrollToPosition(recyclerChat, null, chatAdapter.getItemCount() - 1);
        }

        Call<JsonObject> call = apiInterface.sendMessage("Bearer " + api, userId, receiverId, Config.APP_ID, message, type);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void loadFirstPage() {

        chat.clear();
        Call<ChatPag> call2 = apiInterface.messageData("Bearer " + api, userId, receiverId, Config.APP_ID);
        call2.enqueue(new Callback<ChatPag>() {
            @Override
            public void onResponse(Call<ChatPag> call, Response<ChatPag> response) {
                assert response.body() != null;
                if (response.body().getTotal() > 0) {
                    Collections.reverse(response.body().getData());
                    chat.addAll(response.body().getData());
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ChatPag> call, Throwable t) {

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }




}