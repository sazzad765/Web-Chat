package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.team15.webchat.Adapters.ChatAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.User;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerChat;
    private ChatViewModel chatViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private ChatAdapter chatAdapter;
    private String api, userId, receiverId;
    private EditText edit_send;
    private ImageView btn_send, profile_image,imgSelect;
    private TextView username;
    List<Chat> chat;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private static final int IMAGE_REQUEST = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        chat = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        receiverId = extras.getString("receiverId");



        chatAdapter = new ChatAdapter(this, chat, userId);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra("message")) {
            String message = extras.getString("message");
            sendMessage(message, "image");
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edit_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, "text");
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                edit_send.setText("");
            }
        });
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openImage();
            }
        });

        recyclerChat.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
//                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();
        setProfile();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    loadFirstPage();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void setProfile(){
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getUser("Bearer " + api, receiverId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                username.setText(user.getName());
                Glide.with(ChatActivity.this).load(user.getImage()).apply(RequestOptions.centerCropTransform()).into(profile_image);

            }
        });
    }

    private void openImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }
    }
    private void uploadFile(Uri fileUri, String desc) {
        File file = new File(getRealPathFromURI(fileUri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


        APIInterface api = APIClient.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = api.uploadFile(fileToUpload, filename,"user_id");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });


    }


//    private void loadNextPage() {
//        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(getActivity(), new Observer<ActiveUser>() {
//            @Override
//            public void onChanged(ActiveUser activeUser) {
//                activeListAdapter.removeLoadingFooter();
//                isLoading = false;
//                List<ActiveUserList> results = activeUser.getData();
//                activeListAdapter.addAll(results);
//                if (currentPage != TOTAL_PAGES) activeListAdapter.addLoadingFooter();
//                else isLastPage = true;
//            }
//        });
//    }

    private void sendMessage(String message, String type) {
        Chat chat1 = new Chat(receiverId, userId, message, Integer.parseInt(Config.APP_ID), 0, " ", type);
        chat.add(chat1);
        chatAdapter.notifyDataSetChanged();
        if (chatAdapter.getItemCount() > 1) {
            recyclerChat.getLayoutManager().smoothScrollToPosition(recyclerChat, null, chatAdapter.getItemCount() - 1);
        }
        chatViewModel.sendMessage("Bearer " + api, userId, receiverId, message, type);
    }

    private void loadFirstPage() {
        chat.clear();
        chatViewModel.messageData("Bearer " + api, userId, receiverId, Config.APP_ID).observe(this, new Observer<ChatPag>() {
            @Override
            public void onChanged(ChatPag chatPag) {
                if (chatPag.getTotal() > 0) {
                    Collections.reverse(chatPag.getData());
                    for (int i = 0; i < chatPag.getData().size(); i++) {
                        chat.add(chatPag.getData().get(i));
                    }
                    chatAdapter.notifyDataSetChanged();
                }
                TOTAL_PAGES = chatPag.getLastPage();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            //the image URI
            Uri selectedImage = data.getData();

            //calling the upload file method after choosing the file
            uploadFile(selectedImage, "My Image");
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void init(){
        recyclerChat = findViewById(R.id.recyclerChat);
        edit_send = findViewById(R.id.text_send);
        btn_send = findViewById(R.id.btn_send);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        imgSelect = findViewById(R.id.imgSelect);
    }
}