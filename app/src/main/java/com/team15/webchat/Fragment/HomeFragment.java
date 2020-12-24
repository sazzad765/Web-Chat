package com.team15.webchat.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.team15.webchat.Adapters.CategoryAdapter;
import com.team15.webchat.Adapters.GridSpacingItemDecoration;
import com.team15.webchat.Adapters.PackageProductListAdapter;
import com.team15.webchat.Adapters.SliderAdapter;
import com.team15.webchat.Model.Banner;
import com.team15.webchat.Model.Category;
import com.team15.webchat.Model.PackageProduct;

import com.team15.webchat.PackageProductActivity;
import com.team15.webchat.R;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils.dpToPx;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerCategory;

    private SliderView imageSliderSmall;
    private SliderAdapter adapter;
    private AppViewModel appViewModel;
    private ChatViewModel chatViewModel;
    private SessionManager sessionManager;
    List<PackageProduct.Product> product = new ArrayList<>();

    private String agentId, userId, api;
    private final List<Banner> bannerList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private  List<Category>categories = new ArrayList<>();

    public HomeFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);
        init(view);
        appViewModel =  new ViewModelProvider(getActivity()).get(AppViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> userInfo = sessionManager.get_user();
        agentId = userInfo.get(SessionManager.SELLER_ID);
        api = userInfo.get(SessionManager.API_KEY);
        userId = userInfo.get(SessionManager.USER_ID);

        adapter = new SliderAdapter(getActivity(), new SliderAdapter.SliderAdapterListener() {
            @Override
            public void SliderOnClick(View v, int position) {
                String url = bannerList.get(position).getSlider();
                String message = "Do you want message to admin for this offer";
                sendMessage(url, agentId, "banner",message);
            }
        });
        imageSliderSmall.setSliderAdapter(adapter);
        imageSliderSmall.setIndicatorAnimation(IndicatorAnimationType.WORM);
        imageSliderSmall.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSliderSmall.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        imageSliderSmall.setIndicatorSelectedColor(Color.WHITE);
        imageSliderSmall.setIndicatorUnselectedColor(Color.GRAY);
        imageSliderSmall.setScrollTimeInSec(4);
        imageSliderSmall.setAutoCycle(true);
        imageSliderSmall.startAutoCycle();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerCategory.setLayoutManager(mLayoutManager);
        recyclerCategory.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(16), true));
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        recyclerCategory.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(onItemClickListener);

        slider();
        setCategory();

        return view;
    }

    private void slider() {
        appViewModel.getBanner().observe(getActivity(), new Observer<List<Banner>>() {
            @Override
            public void onChanged(List<Banner> banners) {
                if (banners != null) {
                    bannerList.addAll(banners);
                    adapter.renewItems(banners);
                }
            }
        });
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            Category list = categories.get(position);
            Intent intent = new Intent(getActivity(), PackageProductActivity.class);
            intent.putExtra("data", list);
            startActivity(intent);
        }
    };

    private void setCategory() {
        appViewModel.getCategory("Bearer " + api).observe(getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> data) {
                if (data != null) {
                    categories.clear();
                    categories.addAll(data);
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void sendMessage(final String message, final String receiverId, final String type, String body) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setTitle("Message")
                .setMessage(body)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chatViewModel.sendMessage("Bearer " + api, userId, receiverId, message, type, "user");
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void init(View view) {
        imageSliderSmall = view.findViewById(R.id.imageSliderSmall);
        recyclerCategory= view.findViewById(R.id.recyclerCategory);
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