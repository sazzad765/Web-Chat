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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.SellerPurchaseListAdapter;
import com.team15.webchat.Adapters.UserPurchaseListAdapter;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserPurchaseActivity extends AppCompatActivity {
    RecyclerView recyclerPurchaseList;
    UserPurchaseListAdapter userPurchaseListAdapter;
    private AppViewModel appViewModel;
    List<PurchaseList.Purchase> purchase = new ArrayList<>();
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
        setContentView(R.layout.activity_user_purchase);
        recyclerPurchaseList = findViewById(R.id.recyclerPurchaseList);
        imgBack = findViewById(R.id.imgBack);
        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        sessionManager = new SessionManager(this);
        HashMap<String, String> userInfo = sessionManager.get_user();
        userId = userInfo.get(SessionManager.USER_ID);
        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userPurchaseListAdapter = new UserPurchaseListAdapter(this, purchase, new UserPurchaseListAdapter.UserPurchaseListAdapterListener() {
            @Override
            public void cancelOnClick(View v, int position) {
                cancelPurchase(purchase.get(position).getSellId().toString());
            }
        });
        recyclerPurchaseList.setLayoutManager(linearLayoutManager);
        recyclerPurchaseList.setAdapter(userPurchaseListAdapter);

        recyclerPurchaseList.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
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

    private void cancelPurchase(final String sellId){
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Purchase")
                .setMessage("Do you really want to cancel")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        appViewModel.cancelPurchase("Bearer " + api,sellId).observe(UserPurchaseActivity.this, new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                if (apiResponse!=null){
                                    Toast.makeText(UserPurchaseActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    loadFirstPage();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void loadFirstPage() {
        appViewModel.getUserPurchase("Bearer " + api, userId, "1").observe(this, new Observer<PurchaseList.PurchaseListPaging>() {
            @Override
            public void onChanged(PurchaseList.PurchaseListPaging purchaseList) {
                if (purchaseList != null) {
                    purchase.clear();
                    TOTAL_PAGES = purchaseList.getLastPage();
                    purchase.addAll(purchaseList.getData());
                    userPurchaseListAdapter.notifyDataSetChanged();

                    if (currentPage != TOTAL_PAGES) userPurchaseListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }

    private void loadNextPage() {
        appViewModel.getUserPurchase("Bearer " + api, userId, String.valueOf(currentPage)).observe(this, new Observer<PurchaseList.PurchaseListPaging>() {
            @Override
            public void onChanged(PurchaseList.PurchaseListPaging purchaseList) {
                if (purchaseList != null) {
                    userPurchaseListAdapter.removeLoadingFooter();
                    isLoading = false;
                    purchase.addAll(purchaseList.getData());
                    userPurchaseListAdapter.notifyDataSetChanged();
                    if (currentPage < TOTAL_PAGES) userPurchaseListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
        });
    }
}