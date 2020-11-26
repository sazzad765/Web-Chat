package com.team15.webchat.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.team15.webchat.Adapters.ProductListAdapter;
import com.team15.webchat.Adapters.SliderAdapter;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.Model.ApiResponse;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.ProductList;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerProduct;
    private TextView txtPoint,txtPurchase;
    private NestedScrollView nestedScrollView;
    private SliderView imageSliderSmall;
    private SliderAdapter adapter;
    private AppViewModel appViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    List<ProductList.Product> product = new ArrayList<>();


    private String sellerId, userId, api;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    ProductListAdapter productListAdapter;
    private final List<Banner> bannerList = new ArrayList<>();

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);

        appViewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        recyclerProduct = view.findViewById(R.id.recyclerProduct);
        imageSliderSmall = view.findViewById(R.id.imageSliderSmall);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        txtPoint = view.findViewById(R.id.txtPoint);
        txtPurchase = view.findViewById(R.id.txtPurchase);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        userId = userInfo.get(SessionManager.USER_ID);

        adapter = new SliderAdapter(getActivity(), sellerId);
        imageSliderSmall.setSliderAdapter(adapter);
        imageSliderSmall.setIndicatorAnimation(IndicatorAnimationType.WORM);
        imageSliderSmall.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSliderSmall.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        imageSliderSmall.setIndicatorSelectedColor(Color.WHITE);
        imageSliderSmall.setIndicatorUnselectedColor(Color.GRAY);
        imageSliderSmall.setScrollTimeInSec(4);
        imageSliderSmall.setAutoCycle(true);
        imageSliderSmall.startAutoCycle();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        productListAdapter = new ProductListAdapter(getActivity(), product, new ProductListAdapter.ProductListAdapterListener() {
            @Override
            public void requestOnClick(View v, int position) {
                purchaseDialog(product.get(position).getId().toString());
            }
        });
        recyclerProduct.setLayoutManager(linearLayoutManager);
        recyclerProduct.setAdapter(productListAdapter);

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

            }
        };
        slider();
        loadFirstPage();
        setProfile();

        return view;
    }

    private void slider() {
        appViewModel.getBanner().observe(getActivity(), new Observer<List<Banner>>() {
            @Override
            public void onChanged(List<Banner> banners) {
                if (banners != null) {
                    bannerList.addAll(banners);
//                    pagerSliderAdapter.notifyDataSetChanged();
                    adapter.renewItems(banners);
                }
            }
        });
    }

    private void setProfile() {
        userViewModel.getUser("Bearer " + api, userId).observe(getActivity(), new Observer<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(User user) {
                if (user!=null) {
                    txtPoint.setText(user.getPoint().toString());
                    txtPurchase.setText(user.getPurchase().toString());
                }
            }
        });
    }


    private void purchaseDialog(final String productId) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.purchase_request_dialog, null);

        final EditText editGameId = (EditText) dialogView.findViewById(R.id.editGameId);
        Button buttonSubmit = (Button) dialogView.findViewById(R.id.btnReq);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btnCancel);


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gameId = editGameId.getText().toString();
                if (gameId.length() <= 0) {
                    editGameId.requestFocus();
                    editGameId.setError("enter game id");
                    return;
                }
                purchaseRequest(productId, gameId);
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    private void purchaseRequest(String productId, String gameId) {
        appViewModel.sellRequest("Bearer " + api, userId, productId, gameId).observe(getActivity(), new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                if (apiResponse != null) {
                    showDialog(apiResponse.getMessage());
                }
            }
        });
    }

    private void loadFirstPage() {
        appViewModel.getProduct("Bearer " + api, "1").observe(getActivity(), new Observer<ProductList.ProductListPaging>() {
            @Override
            public void onChanged(ProductList.ProductListPaging productList) {
                if (productList != null) {
                    product.clear();
                    TOTAL_PAGES = productList.getLastPage();
                    product.addAll(productList.getData());
                    productListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) productListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }

            }
        });
    }

    private void loadNextPage() {
        appViewModel.getProduct("Bearer " + api, String.valueOf(currentPage)).observe(getActivity(), new Observer<ProductList.ProductListPaging>() {
            @Override
            public void onChanged(ProductList.ProductListPaging productList) {
                if (productList != null) {
                    productListAdapter.removeLoadingFooter();
                    isLoading = false;
                    product.addAll(productList.getData());
                    productListAdapter.notifyDataSetChanged();

                    if (currentPage < TOTAL_PAGES) productListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }

            }
        });
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Purchase")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        count();
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.MESSAGE_NOTIFICATION));
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
//    }
//
//
//    private void isOnline(String status) {
//        userViewModel.isOnline("Bearer " + api, userId, status);
//    }

}