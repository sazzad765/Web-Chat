package com.team15.webchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.Adapters.ChatListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.App.Config;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.LoginActivity;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.Model.ChatList;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatListFragment extends Fragment {
    private EditText search_users;
    private ProgressBar chatListProgressBar;
    private ImageView imgMenu;
    private ChatListAdapter chatListAdapter;
    private ChatViewModel chatViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerChatList;
    private String api,user_id;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    List<ChatList>chatLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerChatList = view.findViewById(R.id.recyclerChatList);
        chatListProgressBar = view.findViewById(R.id.chatListProgressBar);
        imgMenu = view.findViewById(R.id.img_menu);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);
        user_id= userInfo.get(SessionManager.USER_ID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        chatListAdapter = new ChatListAdapter(getActivity(),chatLists,user_id);
        recyclerChatList.setLayoutManager(linearLayoutManager);
        recyclerChatList.setAdapter(chatListAdapter);

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

        recyclerChatList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    loadFirstPage();
                }
            }
        };
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
        return view;
    }

    private void menuClick(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.inflate(R.menu.top_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        isOnline("0");
                        sessionManager.logout();
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(getActivity(), LoginActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
//                            case R.id.menu2:
//                                //handle menu2 click
//                                break;
                    default:
                        break;

                }
                return false;
            }
        });

        popup.show();
    }

    private void isOnline(String status){
        userViewModel.isOnline("Bearer " + api,user_id,status);
    }

    private void loadNextPage() {
        chatViewModel.getChatList("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(getActivity(), new Observer<ChatListPaging>() {
            @Override
            public void onChanged(ChatListPaging chatListPaging) {
                chatListAdapter.removeLoadingFooter();
                isLoading = false;
                if (chatListPaging != null) {
                    if (chatListPaging.getTotal() > 0) {
                        for (int i = 0; i < chatListPaging.getData().size(); i++) {
                            chatLists.add(chatListPaging.getData().get(i));
                        }
                        chatListAdapter.notifyDataSetChanged();
                    }
                    if (currentPage != TOTAL_PAGES) chatListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadFirstPage() {
        if (chatListAdapter.getItemCount()==0){
            chatListProgressBar.setVisibility(View.VISIBLE);
        }
        chatViewModel.getChatList("Bearer " + api, user_id, String.valueOf(currentPage)).observe(getActivity(), new Observer<ChatListPaging>() {
            @Override
            public void onChanged(ChatListPaging chatListPaging) {
                chatListProgressBar.setVisibility(View.INVISIBLE);
                if (chatListPaging != null) {
                    chatLists.clear();
                    TOTAL_PAGES = chatListPaging.getLastPage();
                    if (chatListPaging.getTotal() > 0) {
                        for (int i = 0; i < chatListPaging.getData().size(); i++) {
                            chatLists.add(chatListPaging.getData().get(i));
                        }
                        chatListAdapter.notifyDataSetChanged();
                    }

                    if (currentPage < TOTAL_PAGES) chatListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        loadFirstPage();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}