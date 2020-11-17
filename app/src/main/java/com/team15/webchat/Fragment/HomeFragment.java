package com.team15.webchat.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.team15.webchat.Adapters.PagerSliderAdapter;
import com.team15.webchat.Adapters.SliderAdapter;
import com.team15.webchat.App.Config;
import com.team15.webchat.ChatActivity;
import com.team15.webchat.LoginActivity;
import com.team15.webchat.MainActivity;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    ViewPager2 pagerSlider;
    private SliderView imageSliderSmall;
    private SliderAdapter adapter;
    private AppViewModel appViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private ImageView imgSeller;
    private ImageView imgMenu;
    private TextView txtSellerName, txtCount;
    private Button btnWrite;
    private String sellerId, userId, api;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    CardView sellerCardView;
    PagerSliderAdapter pagerSliderAdapter;
    private final List<Banner> bannerList = new ArrayList<>();
    private Handler sliderHandler = new Handler();

    public HomeFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        appViewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

//        sliderView = view.findViewById(R.id.imageSlider);
        imageSliderSmall = view.findViewById(R.id.imageSliderSmall);
        imgSeller = view.findViewById(R.id.imgSeller);
        txtSellerName = view.findViewById(R.id.txtSellerName);
        btnWrite = view.findViewById(R.id.btnWrite);
        txtCount = view.findViewById(R.id.txtCount);
        imgMenu = view.findViewById(R.id.img_menu);
        sellerCardView = view.findViewById(R.id.sellerCardView);
        pagerSlider = view.findViewById(R.id.pagerSlider);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        sellerId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        userId = userInfo.get(SessionManager.USER_ID);

        pagerSliderAdapter = new PagerSliderAdapter(getActivity(),bannerList,pagerSlider,sellerId);

        pagerSlider.setAdapter(pagerSliderAdapter);
        pagerSlider.setClipToPadding(false);
        pagerSlider.setClipChildren(false);
        pagerSlider.setOffscreenPageLimit(3);
        pagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        pagerSlider.setPageTransformer(compositePageTransformer);
        pagerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable,3000);
            }
        });






        adapter = new SliderAdapter(getActivity(), sellerId);
//        sliderView.setSliderAdapter(adapter);
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
//        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//        sliderView.setIndicatorSelectedColor(Color.WHITE);
//        sliderView.setIndicatorUnselectedColor(Color.GRAY);
//        sliderView.setScrollTimeInSec(3);
//        sliderView.setAutoCycle(true);
//        sliderView.startAutoCycle();

        imageSliderSmall.setSliderAdapter(adapter);
        imageSliderSmall.setIndicatorAnimation(IndicatorAnimationType.WORM);
        imageSliderSmall.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSliderSmall.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        imageSliderSmall.setIndicatorSelectedColor(Color.WHITE);
        imageSliderSmall.setIndicatorUnselectedColor(Color.GRAY);
        imageSliderSmall.setScrollTimeInSec(4);
        imageSliderSmall.setAutoCycle(true);
        imageSliderSmall.startAutoCycle();

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectTab(1);
            }
        });
        sellerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectTab(1);
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                count();
            }
        };
        slider();
        setSellerProfile();
        count();
        return view;
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            pagerSlider.setCurrentItem(pagerSlider.getCurrentItem() + 1,true);

        }
    };

    private void setSellerProfile() {
        appViewModel.getSeller("Bearer " + api, sellerId).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    txtSellerName.setText(user.getName());
                    Glide.with(getActivity()).load(user.getImage()).apply(RequestOptions.centerCropTransform()).into(imgSeller);
                }
            }
        });
    }

    private void slider() {
        appViewModel.getBanner().observe(getActivity(), new Observer<List<Banner>>() {
            @Override
            public void onChanged(List<Banner> banners) {
                if (banners!=null) {
                    bannerList.addAll(banners);
                    pagerSliderAdapter.notifyDataSetChanged();
                    adapter.renewItems(banners);
                }
            }
        });
    }

    private void count() {
        appViewModel.getSeenCount("Bearer " + api, userId, sellerId).observe(getActivity(), new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                if (jsonObject != null) {
                    int count = Integer.parseInt(jsonObject.get("unseen_message").toString());
                    if (count != 0) {
                        txtCount.setText("you got new message");
                    } else {
                        txtCount.setText(null);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        count();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

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
        userViewModel.isOnline("Bearer " + api, userId, status);
    }

}