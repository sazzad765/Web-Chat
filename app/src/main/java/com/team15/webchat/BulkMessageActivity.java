package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.team15.webchat.Adapters.ActiveListAdapter;
import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.UserListAdapter;
import com.team15.webchat.App.Config;
import com.team15.webchat.Model.ActiveUser;
import com.team15.webchat.Model.ActiveUserList;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulkMessageActivity extends AppCompatActivity {
    ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    private RecyclerView recyclerUserList;
    private ProgressBar activeListProgressBar;
    private UserListAdapter activeListAdapter;
    private String api;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    List<ActiveUserList> results = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_message);

        recyclerUserList = findViewById(R.id.recyclerUserList);
        activeListProgressBar = findViewById(R.id.activeListProgressBar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        activeListAdapter = new UserListAdapter(this,results);
        recyclerUserList.setLayoutManager(linearLayoutManager);
        recyclerUserList.setAdapter(activeListAdapter);

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        api = userInfo.get(SessionManager.API_KEY);

        recyclerUserList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
    }

    private void loadNextPage() {
        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(this, new Observer<ActiveUser>() {
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
        chatViewModel.activeUser("Bearer " + api, Config.APP_ID, String.valueOf(currentPage)).observe(this, new Observer<ActiveUser>() {
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

    public void imgBack(View view) {
        finish();
    }
}