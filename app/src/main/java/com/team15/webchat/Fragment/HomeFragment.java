package com.team15.webchat.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.team15.webchat.Adapters.SliderAdapter;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.User;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private SliderView sliderView;
    private SliderAdapter adapter;
    private AppViewModel appViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private ImageView imgSeller;
    private TextView txtSellerName;
    private Button btnWrite;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        appViewModel = ViewModelProviders.of(getActivity()).get(AppViewModel.class);
        sliderView = view.findViewById(R.id.imageSlider);
        imgSeller = view.findViewById(R.id.imgSeller);
        txtSellerName = view.findViewById(R.id.txtSellerName);
        btnWrite = view.findViewById(R.id.btnWrite);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        final String sellerId = userInfo.get(SessionManager.SELLER_ID);
        String api = userInfo.get(SessionManager.API_KEY);

        adapter = new SliderAdapter(getActivity());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();


        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });

        slider();
        setSellerProfile(api, sellerId);
        return view;
    }

    private void setSellerProfile(String api, String sellerId) {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getUser("Bearer " + api, sellerId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                txtSellerName.setText(user.getName());
                Glide.with(getActivity()).load(user.getImage()).apply(RequestOptions.centerCropTransform()).into(imgSeller);
            }
        });
    }

    private void slider() {
        appViewModel.getBanner().observe(getActivity(), new Observer<List<Banner>>() {
            @Override
            public void onChanged(List<Banner> banners) {
                adapter.renewItems(banners);
            }
        });
    }
}