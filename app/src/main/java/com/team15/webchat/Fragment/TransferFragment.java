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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.TransferListAdapter;
import com.team15.webchat.App.Config;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransferFragment extends Fragment {
    ChatViewModel chatViewModel;
    private TextView txtNoData;
    private SessionManager sessionManager;
    private RecyclerView recyclerTransferList;
    private ProgressBar activeListProgressBar;
    private TransferListAdapter transferListAdapter;
    private String api, user_id;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    List<ActiveUserList> results = new ArrayList<>();

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        recyclerTransferList = view.findViewById(R.id.recyclerTransferList);
        activeListProgressBar = view.findViewById(R.id.activeListProgressBar);
        txtNoData = view.findViewById(R.id.txtNoData);

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);
        user_id = userInfo.get(SessionManager.USER_ID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        transferListAdapter = new TransferListAdapter(getActivity(), results, new TransferListAdapter.TransferListAdapterListener() {
            @Override
            public void acceptOnClick(View v, final int position) {
                chatViewModel.transferUser("Bearer " + api, results.get(position).getUserId().toString()).observe(getActivity(), new Observer<JsonObject>() {
                    @Override
                    public void onChanged(JsonObject object) {
                        loadFirstPage();
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("receiverId", results.get(position).getUserId().toString());
                        startActivity(intent);
                    }
                });
            }
        });
        recyclerTransferList.setLayoutManager(linearLayoutManager);
        recyclerTransferList.setAdapter(transferListAdapter);
        recyclerTransferList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
                loadFirstPage();
            }
        };
        return view;
    }

    private void loadNextPage() {
        chatViewModel.transferList("Bearer " + api, user_id, String.valueOf(currentPage)).observe(getActivity(), new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                if (activeUser != null) {
                    transferListAdapter.removeLoadingFooter();
                    isLoading = false;

                    results.addAll(activeUser.getData());
                    transferListAdapter.notifyDataSetChanged();

                    if (currentPage != TOTAL_PAGES) transferListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadFirstPage() {
        if (results.size() < 1) {
            txtNoData.setVisibility(View.GONE);
            activeListProgressBar.setVisibility(View.VISIBLE);
        }
        chatViewModel.transferList("Bearer " + api, user_id, String.valueOf(currentPage)).observe(getActivity(), new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                if (activeUser != null) {
                    results.clear();
                    activeListProgressBar.setVisibility(View.INVISIBLE);
                    TOTAL_PAGES = activeUser.getLastPage();
                    results.addAll(activeUser.getData());
                    transferListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) transferListAdapter.addLoadingFooter();
                    else isLastPage = true;

                    if (results.size() < 1) {
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        loadFirstPage();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.MESSAGE_NOTIFICATION);
        filter.addAction(Config.PUSH_NOTIFICATION);
        filter.addAction(Config.UPDATE_NOTIFICATION);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}