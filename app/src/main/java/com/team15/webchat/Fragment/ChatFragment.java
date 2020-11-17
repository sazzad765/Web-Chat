package com.team15.webchat.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.team15.webchat.Adapters.ChatAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Api.APIClient;
import com.team15.webchat.Api.APIInterface;
import com.team15.webchat.App.Config;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Chat;
import com.team15.webchat.Model.ChatPag;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerChat;
    private ChatViewModel chatViewModel;
    private AppViewModel appViewModel;
    private SessionManager sessionManager;
    private ChatAdapter chatAdapter;
    private String api, userId, receiverId, userType;
    private EditText edit_send;
    private ImageView btn_send, profile_image, imgSelect;
    private TextView username;
    private ProgressDialog pDialog;
    User mUser;

    List<Chat> chat;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PICK_IMAGE = 1;

    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        init(view);

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        sessionManager = new SessionManager(getActivity());

        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        receiverId = userInfo.get(SessionManager.SELLER_ID);
        userType = userInfo.get(SessionManager.USER_TYPE);

        chat = new ArrayList<>();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Sending...");
        mHandler = new Handler();

        chatAdapter = new ChatAdapter(getActivity(), chat, userId);
        recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setReverseLayout(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edit_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, userId, receiverId, "text");
                } else {
                    Toast.makeText(getActivity(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                edit_send.setText("");
            }
        });
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

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
                    if (mChat.getReciverId().equals(userId)) {
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
        return view;
    }

    private final Runnable m_Runnable = new Runnable() {
        public void run() {
            mHandler.postDelayed(m_Runnable, 50000);
        }
    };

    private void setProfile() {
        appViewModel.getSeller("Bearer " + api, receiverId).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mUser = user;
                    username.setText(user.getName());
                    Glide.with(getActivity()).load(user.getImage()).apply(RequestOptions.centerCropTransform()).into(profile_image);
                }
            }
        });
    }

    public void sendMessage(String message, String senderId, String receiverId, String type) {
        Chat chat1 = new Chat(receiverId, senderId, message, Integer.parseInt(Config.APP_ID), 1, " ", type);
        chat.add(0, chat1);
        chatAdapter.notifyDataSetChanged();
        if (chatAdapter.getItemCount() > 1) {
            recyclerChat.getLayoutManager().smoothScrollToPosition(recyclerChat, null, 0);
        }
        chatViewModel.sendMessage("Bearer " + api, userId, receiverId, message, type, userType);
    }

    private void loadNextPage() {
        chatViewModel.messageData("Bearer " + api, userId, receiverId, String.valueOf(currentPage)).observe(getActivity(), new Observer<ChatPag>() {
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
        chat.clear();
        chatViewModel.messageData("Bearer " + api, userId, receiverId, "1").observe(getActivity(), new Observer<ChatPag>() {
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
        chatViewModel.seenMessage("Bearer " + api, userId, receiverId);
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
        chatViewModel.sendImageMessage(getActivity(), api, uri, userId, receiverId, userType).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                pDialog.dismiss();
                if (apiResponse != null) {
                    loadFirstPage();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_NOTIFICATION));
        m_Runnable.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        mHandler.removeCallbacks(m_Runnable);
    }

    private void init(View v) {
        recyclerChat = v.findViewById(R.id.recyclerChat);
        edit_send = v.findViewById(R.id.text_send);
        btn_send = v.findViewById(R.id.btn_send);
        profile_image = v.findViewById(R.id.profile_image);
        username = v.findViewById(R.id.username);
        imgSelect = v.findViewById(R.id.imgSelect);
    }
}