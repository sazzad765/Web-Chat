package com.team15.webchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.UserPurchaseListAdapter;
import com.team15.webchat.Adapters.UserReferralPointListAdapter;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReferralPointActivity extends AppCompatActivity {
    RecyclerView recyclerReferralPointList;
    UserReferralPointListAdapter userReferralPointListAdapter;
    private AppViewModel appViewModel;
    List<ReferralPointList.ReferralPoint> points = new ArrayList<>();
    private SessionManager sessionManager;
    private ImageView imgBack;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private String api;
    private String sellerId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_point);
        recyclerReferralPointList = findViewById(R.id.recyclerReferralPointList);
        imgBack = findViewById(R.id.imgBack);
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userReferralPointListAdapter = new UserReferralPointListAdapter(this, points);
        recyclerReferralPointList.setLayoutManager(linearLayoutManager);
        recyclerReferralPointList.setAdapter(userReferralPointListAdapter);

        recyclerReferralPointList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadFirstPage();
    }

    private void loadFirstPage() {
        appViewModel.getReferralPointList("Bearer " + api, userId, "1").observe(this, new Observer<ReferralPointList.ReferralPointPaging>() {
            @Override
            public void onChanged(ReferralPointList.ReferralPointPaging paging) {
                if (paging != null) {
                    points.clear();
                    TOTAL_PAGES = paging.getLastPage();
                    points.addAll(paging.getData());
                    userReferralPointListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) userReferralPointListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadNextPage() {
        appViewModel.getReferralPointList("Bearer " + api, userId, String.valueOf(currentPage)).observe(this, new Observer<ReferralPointList.ReferralPointPaging>() {
            @Override
            public void onChanged(ReferralPointList.ReferralPointPaging paging) {
                if (paging != null) {
                    userReferralPointListAdapter.removeLoadingFooter();
                    isLoading = false;
                    points.addAll(paging.getData());
                    userReferralPointListAdapter.notifyDataSetChanged();
                    if (currentPage < TOTAL_PAGES) userReferralPointListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }
}