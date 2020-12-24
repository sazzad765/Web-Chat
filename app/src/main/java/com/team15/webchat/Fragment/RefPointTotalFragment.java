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

import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.UserReferralPointListAdapter;
import com.team15.webchat.Adapters.UserReferralPointTotalAdapter;
import com.team15.webchat.Model.RefPointTotal;
import com.team15.webchat.Model.ReferralPointList;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RefPointTotalFragment extends Fragment {
    RecyclerView recyclerReferralPointTotal;
    UserReferralPointTotalAdapter userReferralPointTotalAdapter;
    private AppViewModel appViewModel;
    List<RefPointTotal.RefPoint> points = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_ref_point_total, container, false);
        recyclerReferralPointTotal = view.findViewById(R.id.recyclerReferralPointTotal);

        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        userReferralPointTotalAdapter = new UserReferralPointTotalAdapter(getActivity(), points);
        recyclerReferralPointTotal.setLayoutManager(linearLayoutManager);
        recyclerReferralPointTotal.setAdapter(userReferralPointTotalAdapter);

        recyclerReferralPointTotal.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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
        appViewModel.getReferralPointTotal("Bearer " + api, userId, "1").observe(this, new Observer<RefPointTotal.RefPointPaging>() {
            @Override
            public void onChanged(RefPointTotal.RefPointPaging paging) {
                if (paging != null) {
                    points.clear();
                    TOTAL_PAGES = paging.getLastPage();
                    points.addAll(paging.getData());
                    userReferralPointTotalAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) userReferralPointTotalAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadNextPage() {
        appViewModel.getReferralPointTotal("Bearer " + api, userId, String.valueOf(currentPage)).observe(this, new Observer<RefPointTotal.RefPointPaging>() {
            @Override
            public void onChanged(RefPointTotal.RefPointPaging paging) {
                if (paging != null) {
                    userReferralPointTotalAdapter.removeLoadingFooter();
                    isLoading = false;
                    points.addAll(paging.getData());
                    userReferralPointTotalAdapter.notifyDataSetChanged();
                    if (currentPage < TOTAL_PAGES) userReferralPointTotalAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }
}