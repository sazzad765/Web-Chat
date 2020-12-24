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

import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActiveListFragment extends Fragment {
    ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerActiveUser;
    private ProgressBar activeListProgressBar;
    private ActiveListAdapter activeListAdapter;
    private String api;
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
        View view = inflater.inflate(R.layout.fragment_active_list, container, false);
        recyclerActiveUser = view.findViewById(R.id.recyclerActiveUser);
        activeListProgressBar = view.findViewById(R.id.activeListProgressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        activeListAdapter = new ActiveListAdapter(getActivity(),results);
        recyclerActiveUser.setLayoutManager(linearLayoutManager);
        recyclerActiveUser.setAdapter(activeListAdapter);

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);

        recyclerActiveUser.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(getActivity(), new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                if (activeUser != null) {
                    activeListAdapter.removeLoadingFooter();
                    isLoading = false;
                    if (activeUser.getTotal() > 0) {
                        results.addAll(activeUser.getData());
                        activeListAdapter.notifyDataSetChanged();
                    }
                    if (currentPage != TOTAL_PAGES) activeListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadFirstPage() {
        activeListProgressBar.setVisibility(View.VISIBLE);
        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(getActivity(), new Observer<ActiveUser>() {
            @Override
            public void onChanged(ActiveUser activeUser) {
                if (activeUser != null) {
                    results.clear();
                    activeListProgressBar.setVisibility(View.INVISIBLE);
                    TOTAL_PAGES = activeUser.getLastPage();
                    if (activeUser.getTotal() > 0) {
                        results.addAll(activeUser.getData());
                        activeListAdapter.notifyDataSetChanged();
                    }
                    if (currentPage < TOTAL_PAGES) activeListAdapter.addLoadingFooter();
                    else isLastPage = true;
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
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}