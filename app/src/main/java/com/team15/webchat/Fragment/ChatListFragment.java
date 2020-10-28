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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.Adapters.ChatListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.App.Config;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.Model.ChatList;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.HashMap;
import java.util.List;

public class ChatListFragment extends Fragment {
    private EditText search_users;
    private RelativeLayout chat_layout;
    ChatListAdapter chatListAdapter;
    ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerChatList;
    private String api,user_id;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerChatList = view.findViewById(R.id.recyclerChatList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        chatListAdapter = new ChatListAdapter(getActivity());
        recyclerChatList.setLayoutManager(linearLayoutManager);
        recyclerChatList.setAdapter(chatListAdapter);

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);
        user_id= userInfo.get(SessionManager.USER_ID);
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

        loadFirstPage();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    loadFirstPage();
                }
            }
        };
        return view;
    }

    private void loadNextPage() {
        chatViewModel.getChatList("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(getActivity(), new Observer<ChatListPaging>() {
            @Override
            public void onChanged(ChatListPaging chatListPaging) {
                chatListAdapter.removeLoadingFooter();
                isLoading = false;
                List<ChatList> results = chatListPaging.getData();
                chatListAdapter.addAll(results);
                if (currentPage != TOTAL_PAGES) chatListAdapter.addLoadingFooter();
                else isLastPage = true;
            }
        });
    }

    private void loadFirstPage() {
        chatViewModel.getChatList("Bearer " + api, user_id, String.valueOf(currentPage)).observe(getActivity(), new Observer<ChatListPaging>() {
            @Override
            public void onChanged(ChatListPaging chatListPaging) {
                List<ChatList> results = chatListPaging.getData();
                chatListAdapter.addAll(results);
                TOTAL_PAGES = chatListPaging.getLastPage();

                if (currentPage < TOTAL_PAGES) chatListAdapter.addLoadingFooter();
                else isLastPage = true;
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}