package com.team15.webchat.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.UserReferralPointListAdapter;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RefPointHistoryFragment extends Fragment {
    RecyclerView recyclerReferralPointList;
    UserReferralPointListAdapter userReferralPointListAdapter;
    private AppViewModel appViewModel;
    List<ReferralPointList.ReferralPoint> points = new ArrayList<>();
    private SessionManager sessionManager;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private String api;
    private String sellerId;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ref_point_history, container, false);
        recyclerReferralPointList = view.findViewById(R.id.recyclerReferralPointList);

        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        userReferralPointListAdapter = new UserReferralPointListAdapter(getActivity(), points);
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


        loadFirstPage();



        return view;
    }

    private void loadFirstPage() {
        appViewModel.getReferralPointList("Bearer " + api, userId, "1").observe(getActivity(), new Observer<ReferralPointList.ReferralPointPaging>() {
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
        appViewModel.getReferralPointList("Bearer " + api, userId, String.valueOf(currentPage)).observe(getActivity(), new Observer<ReferralPointList.ReferralPointPaging>() {
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