package com.team15.webchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Adapters.ChatAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.Data.DBHelper;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.User;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerChat;
    private ChatViewModel chatViewModel;
    private AppViewModel appViewModel;
    private SessionManager sessionManager;
    private ChatAdapter chatAdapter;
    private String api, userId, receiverId, agentId, userType;
    private EditText edit_send;
    private ImageView btn_send, profile_image, imgSelect, imgDefaultText, imgPurchase, imgFavourite;
    private TextView username, txtRef;
    private ProgressDialog pDialog;
    private RelativeLayout toolbarLayout;
    User mUser;

    List<Chat> chat;
    private ArrayList arrayChat;
    private DBHelper mydb;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PICK_IMAGE = 1;
    private int isFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        agentId = userInfo.get(SessionManager.SELLER_ID);
        userType = userInfo.get(SessionManager.USER_TYPE);

        chat = new ArrayList<>();
        mydb = new DBHelper(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Sending...");

        Bundle extras = getIntent().getExtras();
        receiverId = extras.getString("receiverId");

        chatAdapter = new ChatAdapter(this, chat, agentId);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setReverseLayout(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        btn_send.setOnClickListener(this);
        imgSelect.setOnClickListener(this);
        imgDefaultText.setOnClickListener(this);
        toolbarLayout.setOnClickListener(this);
        imgPurchase.setOnClickListener(this);


        recyclerChat.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
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
        seen();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.MESSAGE_NOTIFICATION)) {
                    Chat mChat = (Chat) intent.getSerializableExtra("chat");
                    if (mChat.getReciverId().equals(agentId) && mChat.getSenderId().equals(receiverId)) {
                        chat.add(0, mChat);
                        chatAdapter.notifyDataSetChanged();
                        if (chatAdapter.getItemCount() > 1) {
                            recyclerChat.getLayoutManager().smoothScrollToPosition(recyclerChat, null, 0);
                        }
                    }
                    seen();
                }
            }
        };
    }

    private void setProfile() {
        appViewModel.getSeller("Bearer " + api, receiverId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mUser = user;
                    username.setText(user.getName());
                    Glide.with(ChatActivity.this).load(user.getImage()).apply(RequestOptions.centerCropTransform()).into(profile_image);

                    isFav = user.getFavorite();
                    if (user.getFavorite() == 1) {
                        imgFavourite.setImageResource(R.drawable.star_gd);
                    } else {
                        imgFavourite.setImageResource(R.drawable.star);
                    }
                    if (user.getRef().equals("0")) {
                        txtRef.setVisibility(View.GONE);
                    }
                    txtRef.setText("Ref By: " + user.getRef());
                }
            }
        });
    }

    public void sendMessage(String message, String type, String re_id) {
        Chat chat1 = new Chat(re_id, agentId, message, Integer.parseInt(Config.APP_ID), 1, " ", type);
        chat.add(0, chat1);
        chatAdapter.notifyDataSetChanged();
        if (chatAdapter.getItemCount() > 1) {
            recyclerChat.getLayoutManager().smoothScrollToPosition(recyclerChat, null, 0);
        }
        chatViewModel.sendMessage("Bearer " + api, userId, re_id, message, type, userType);
    }


    private void loadNextPage() {
        chatViewModel.messageData("Bearer " + api, agentId, receiverId, String.valueOf(currentPage)).observe(this, new Observer<ChatPag>() {
            @Override
            public void onChanged(ChatPag chatPag) {
                chatAdapter.removeLoadingFooter();
                isLoading = false;
                if (chatPag != null) {
                    if (chatPag.getTotal() > 0) {
                        for (int i = 0; i < chatPag.getData().size(); i++) {
                            chat.add(chatPag.getData().get(i));
                        }
                        chatAdapter.notifyDataSetChanged();
                    }
                    if (currentPage != TOTAL_PAGES) chatAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadFirstPage() {
        chatViewModel.messageData("Bearer " + api, agentId, receiverId, "1").observe(this, new Observer<ChatPag>() {
            @Override
            public void onChanged(ChatPag chatPag) {
                if (chatPag != null) {
                    TOTAL_PAGES = chatPag.getLastPage();
                    chat.clear();
                    if (chatPag.getTotal() > 0) {
                        for (int i = 0; i < chatPag.getData().size(); i++) {
                            chat.add(chatPag.getData().get(i));
                        }
                        chatAdapter.notifyDataSetChanged();
                        if (chatAdapter.getItemCount() > 1) {
                            recyclerChat.getLayoutManager().smoothScrollToPosition(recyclerChat, null, 0);
                        }
                    }
                    if (currentPage != TOTAL_PAGES) chatAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });

    }

    private void seen() {
        chatViewModel.seenMessage("Bearer " + api, agentId, receiverId);
    }

    private void chatData() {
        mydb.numberOfRows();
        arrayChat = mydb.getList();

        AlertDialog.Builder alertDialog = new
                AlertDialog.Builder(this);
        View rowList = getLayoutInflater().inflate(R.layout.row, null);
        ListView listView = rowList.findViewById(R.id.listView);
        TextView btnAdd = rowList.findViewById(R.id.btnAdd);
        TextView btnClose = rowList.findViewById(R.id.btnClose);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, arrayChat);
        listView.setAdapter(adapter);
        ;
        adapter.notifyDataSetChanged();
        alertDialog.setView(rowList);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit_send.setText(arrayChat.get(position).toString());
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ChatDataActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }

        } else {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri postImageUri = data.getData();
            uploadFile(postImageUri);
        }
    }

    private void uploadFile(Uri uri) {
        pDialog.show();
        chatViewModel.sendImageMessage(this, api, uri, userId, receiverId, userType).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                pDialog.dismiss();
                if (apiResponse != null) {
                    loadFirstPage();
                }
            }
        });
    }

    private void isPurchase() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Purchase")
                .setMessage("Purchase complete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        appViewModel.purchase("Bearer " + api, receiverId).observe(ChatActivity.this, new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                if (apiResponse != null) {
                                    Toast.makeText(ChatActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_NOTIFICATION));
        setProfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void init() {
        recyclerChat = findViewById(R.id.recyclerChat);
        edit_send = findViewById(R.id.text_send);
        btn_send = findViewById(R.id.btn_send);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        imgSelect = findViewById(R.id.imgSelect);
        imgDefaultText = findViewById(R.id.imgDefaultText);
        toolbarLayout = findViewById(R.id.toolbarLayout);
        imgPurchase = findViewById(R.id.imgPurchase);
        imgFavourite = findViewById(R.id.imgFavourite);
        txtRef = findViewById(R.id.txtRef);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_send:
                String msg = edit_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, "text", receiverId);
                } else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                edit_send.setText("");
                break;
            case R.id.imgSelect:
                pickImage();
                break;
            case R.id.imgDefaultText:
                chatData();
                break;
            case R.id.toolbarLayout:
                if (mUser != null) {
                    if (mUser.getId() != null) {
                        intent = new Intent(ChatActivity.this, UserInfoActivity.class);
                        intent.putExtra("id", mUser.getId().toString());
                        intent.putExtra("name", mUser.getName());
                        intent.putExtra("phone", mUser.getPhone());
                        intent.putExtra("image", mUser.getImage());
                        startActivity(intent);
                    }
                }
                break;
            case R.id.imgPurchase:
                isPurchase();
                break;

            default:
                break;
        }
    }
}