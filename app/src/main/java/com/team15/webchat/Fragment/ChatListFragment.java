package com.team15.webchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.team15.webchat.Adapters.ChatListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.WaitingListAdapter;
import com.team15.webchat.App.Config;
import com.team15.webchat.LoginActivity;

import com.team15.webchat.Model.ChatList;
import com.team15.webchat.Model.ChatListPaging;
import com.team15.webchat.Model.WaitingList;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListFragment extends Fragment {
    private NestedScrollView nestedScrollView;
    private EditText search_users;
    private ProgressBar chatListProgressBar;
    private ImageView imgMenu;
    private ChatListAdapter chatListAdapter;
    private WaitingListAdapter waitingListAdapter;
    private ChatViewModel chatViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerChatList, recyclerWaitingList;
    private String api, user_id, agentId;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    List<ChatList> chatLists = new ArrayList<>();
    List<WaitingList> waitingLists = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerChatList = view.findViewById(R.id.recyclerChatList);
        recyclerWaitingList = view.findViewById(R.id.recyclerWaitingList);
        chatListProgressBar = view.findViewById(R.id.chatListProgressBar);
        imgMenu = view.findViewById(R.id.img_menu);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);
        user_id = userInfo.get(SessionManager.USER_ID);
        agentId = userInfo.get(SessionManager.SELLER_ID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        chatListAdapter = new ChatListAdapter(getActivity(), chatLists, agentId);
        recyclerChatList.setLayoutManager(linearLayoutManager);
        recyclerChatList.setAdapter(chatListAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        waitingListAdapter = new WaitingListAdapter(getActivity(), waitingLists);
        recyclerWaitingList.setLayoutManager(mLinearLayoutManager);
        recyclerWaitingList.setAdapter(waitingListAdapter);
        waitingListAdapter.setOnItemClickListener(onItemClickListener);

        chatViewModel = new ViewModelProvider(getActivity()).get(ChatViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        if (!isLoading && !isLastPage) {
                            currentPage += 1;
                            isLoading = true;
                            loadNextPage();
                        }
                    }
                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadFirstPage();
                getWaitingList();
            }
        };
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
        if (chatListAdapter.getItemCount() <= 0) {
            chatListProgressBar.setVisibility(View.VISIBLE);
        }
        getWaitingList();
        loadFirstPage();
        return view;
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            WaitingList list = waitingLists.get(position);
            startChat(list.getUserId().toString());
            Toast.makeText(getActivity(), "You Clicked: " + list.getName(), Toast.LENGTH_SHORT).show();
        }
    };


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

    private void isOnline(String status) {
        userViewModel.isOnline("Bearer " + api, user_id, status);
    }

    private void loadNextPage() {
        chatViewModel.getChatList("Bearer " + api, user_id, String.valueOf(currentPage)).observe(getActivity(), new Observer<ChatListPaging>() {
            @Override
            public void onChanged(ChatListPaging chatListPaging) {
                if (chatListPaging != null) {
                    chatListAdapter.removeLoadingFooter();
                    isLoading = false;

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

    private void getWaitingList() {
        chatViewModel.getWaitingList("Bearer " + api).observe(getActivity(), new Observer<List<WaitingList>>() {
            @Override
            public void onChanged(List<WaitingList> lists) {
                waitingLists.clear();
                chatListProgressBar.setVisibility(View.INVISIBLE);
                if (lists != null) {
                    waitingLists.addAll(lists);
                    waitingListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadFirstPage() {
        chatViewModel.getChatList("Bearer " + api, user_id, "1").observe(getActivity(), new Observer<ChatListPaging>() {
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

    public void startChat(String id) {
        chatViewModel.startChat("Bearer " + api, agentId, id, user_id).observe(getActivity(), new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject object) {
                if (object != null) {
                    getWaitingList();
                    loadFirstPage();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.MESSAGE_NOTIFICATION);
        filter.addAction(Config.PUSH_NOTIFICATION);
        filter.addAction(Config.UPDATE_NOTIFICATION);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver, filter);
        loadFirstPage();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}