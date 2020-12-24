package com.team15.webchat.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.team15.webchat.Adapters.PaginationScrollListener;
import com.team15.webchat.Adapters.SellerPurchaseListAdapter;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.PurchaseList;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.UserPurchaseActivity;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellerPurchaseFragment extends Fragment {
    RecyclerView recyclerPurchaseList;
    SellerPurchaseListAdapter sellerPurchaseListAdapter;
    private AppViewModel appViewModel;
    List<PurchaseList.Purchase> purchase = new ArrayList<>();
    private SessionManager sessionManager;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    private String api;
    //    private String sellerId;
    private String type;
    private String id;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_purchase, container, false);

        recyclerPurchaseList = view.findViewById(R.id.recyclerPurchaseList);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        appViewModel = new ViewModelProvider(getActivity()).get(AppViewModel.class);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
//        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        id = userInfo.get(SessionManager.USER_ID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        sellerPurchaseListAdapter = new SellerPurchaseListAdapter(getActivity(), purchase, new SellerPurchaseListAdapter.SellerPurchaseListAdapterListener() {
            @Override
            public void cancelOnClick(View v, int position) {
                cancelPurchase(purchase.get(position).getSellId().toString());
            }

            @Override
            public void acceptOnClick(View v, int position) {
                acceptPurchase(purchase.get(position).getSellId().toString(), purchase.get(position).getUserId().toString());
            }
        });
        recyclerPurchaseList.setLayoutManager(linearLayoutManager);
        recyclerPurchaseList.setAdapter(sellerPurchaseListAdapter);


        List<String> categories = new ArrayList<String>();
        categories.add("Pending");
        categories.add("Approved");
        categories.add("Cancelled");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();
                loadFirstPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        loadFirstPage();
        return view;
    }

    private void loadFirstPage() {

        isLastPage = false;
        currentPage= 1;

        appViewModel.getSellerSell("Bearer " + api, type, "1").observe(getActivity(), new Observer<PurchaseList.PurchaseListPaging>() {
            @Override
            public void onChanged(PurchaseList.PurchaseListPaging purchaseList) {
                if (purchaseList != null) {
                    purchase.clear();
                    TOTAL_PAGES = purchaseList.getLastPage();
                    purchase.addAll(purchaseList.getData());
                    sellerPurchaseListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES){
//                        sellerPurchaseListAdapter.addLoadingFooter();
//                        isLastPage = false;
                    }
                    else {
                        isLastPage = true;
                    }
                }

            }
        });
    }

    private void loadNextPage() {
        appViewModel.getSellerSell("Bearer " + api, type, String.valueOf(currentPage)).observe(getActivity(), new Observer<PurchaseList.PurchaseListPaging>() {
            @Override
            public void onChanged(PurchaseList.PurchaseListPaging purchaseList) {
                if (purchaseList != null) {
//                    sellerPurchaseListAdapter.removeLoadingFooter();
                    isLoading = false;
                    purchase.addAll(purchaseList.getData());
                    sellerPurchaseListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES){
//                        sellerPurchaseListAdapter.addLoadingFooter();
                    }
                    else {
                        isLastPage = true;
                    }
                }
            }
        });
    }


    private void cancelPurchase(final String sellId) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Purchase")
                .setMessage("Do you really want to cancel")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        appViewModel.cancelPurchase("Bearer " + api, sellId).observe(getActivity(), new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                if (apiResponse != null) {
                                    Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    loadFirstPage();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void acceptPurchase(final String sellId, final String userId) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Purchase")
                .setMessage("Do you really want to approve this purchase")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        appViewModel.acceptPurchase("Bearer " + api, sellId, id).observe(getActivity(), new Observer<ApiResponse>() {
                            @Override
                            public void onChanged(ApiResponse apiResponse) {
                                if (apiResponse != null) {
                                    Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    loadFirstPage();
                                    if (apiResponse.getSuccess() == 1) {
                                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                                        intent.putExtra("receiverId", userId);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}