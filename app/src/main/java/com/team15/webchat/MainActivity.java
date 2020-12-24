package com.team15.webchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.team15.webchat.Adapters.PagerAdapter;
import com.team15.webchat.App.Config;
import com.team15.webchat.Fragment.ActiveListFragment;
import com.team15.webchat.Fragment.ChatFragment;
import com.team15.webchat.Fragment.ChatListFragment;
import com.team15.webchat.Fragment.HomeFragment;
import com.team15.webchat.Fragment.ProfileFragment;
import com.team15.webchat.Fragment.ProfileUserFragment;
import com.team15.webchat.Fragment.SellerPurchaseFragment;
import com.team15.webchat.Fragment.TransferFragment;
import com.team15.webchat.Model.DeviceReg;
import com.team15.webchat.Session.SessionManager;
import com.team15.webchat.ViewModel.AppViewModel;
import com.team15.webchat.ViewModel.UserViewModel;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ImageView imgMenu;
    private String userType;
    private String api_key, user_id;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;
    private AppViewModel appViewModel;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private int[] tabIcons;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgMenu = findViewById(R.id.img_menu);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout);

        String token = FirebaseInstanceId.getInstance().getToken();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> userInfo = sessionManager.get_user();
        userType = userInfo.get(SessionManager.USER_TYPE);
        user_id = userInfo.get(SessionManager.USER_ID);
        api_key = userInfo.get(SessionManager.API_KEY);
        if (!token.equals("")) {
            DeviceReg deviceReg = new DeviceReg("Bearer " + api_key, token, user_id);
            updateDeviceId(deviceReg);
        }

        setupViewPager();
//        tabLayout.setupWithViewPager(viewPager);


        if (userType.equals("user")) {
            tabIcons = new int[]{
                    R.drawable.ic_baseline_home_24,
                    R.drawable.ic_baseline_chat_24,
                    R.drawable.ic_baseline_account_circle_24
            };

        } else {
            tabIcons = new int[]{
                    R.drawable.ic_baseline_chat_24,
                    R.drawable.ic_baseline_people_24,
                    R.drawable.ic_baseline_compare_arrows_24,
                    R.drawable.ic_baseline_shopping_cart_24,
                    R.drawable.ic_baseline_account_circle_24
            };
        }
        setupTabIcons();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getBadgeCount();
            }
        };
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
    }

    private void setupTabIcons() {
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setIcon(tabIcons[position]);
                int tabIconColor = ContextCompat.getColor(getApplication(), R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
        });
        tabLayoutMediator.attach();
    }

    private void setupViewPager() {
        final PagerAdapter adapter = new PagerAdapter(this);
        if (userType.equals("user")) {
            adapter.addFrag(new HomeFragment());
            adapter.addFrag(new ChatFragment());
            adapter.addFrag(new ProfileUserFragment());
        } else {
            adapter.addFrag(new ChatListFragment());
            adapter.addFrag(new ActiveListFragment());
            adapter.addFrag(new TransferFragment());
            adapter.addFrag(new SellerPurchaseFragment());
            adapter.addFrag(new ProfileFragment());
        }

        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
//                adapter.getItem(position).onResume();
            }
        });
    }

    private void getBadgeCount(){
        appViewModel.getBadgeCount("Bearer " + api_key,user_id).observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject object) {
                if (object!=null){
                    if (!userType.equals("user")) {
                        int chatCount = Integer.parseInt(object.get("unseen").toString());
                        BadgeDrawable badgeDrawable = tabLayout.getTabAt(0).getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_background));
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setNumber(chatCount);

                        int activeCount = Integer.parseInt(object.get("active").toString());
                        BadgeDrawable badgeActive = tabLayout.getTabAt(1).getOrCreateBadge();
                        badgeActive.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_background));
                        badgeActive.setVisible(true);
                        badgeActive.setNumber(activeCount);

                        int transferCount = Integer.parseInt(object.get("transfer").toString());
                        BadgeDrawable badgeTransfer = tabLayout.getTabAt(2).getOrCreateBadge();
                        badgeTransfer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_background));
                        badgeTransfer.setVisible(true);
                        badgeTransfer.setNumber(transferCount);

                        int pendingCount = Integer.parseInt(object.get("purchase").toString());
                        BadgeDrawable badgePending = tabLayout.getTabAt(3).getOrCreateBadge();
                        badgePending.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_background));
                        badgePending.setVisible(true);
                        badgePending.setNumber(pendingCount);

                    }
                }
            }
        });
    }

    private void updateDeviceId(DeviceReg deviceReg) {
        userViewModel.updateDeviceId(deviceReg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.MESSAGE_NOTIFICATION);
        filter.addAction(Config.PUSH_NOTIFICATION);
        filter.addAction(Config.UPDATE_NOTIFICATION);

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, filter);
        getBadgeCount();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOnline("0");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isOnline("1");
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOnline("0");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        viewPager.setCurrentItem(0);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void menuClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.top_menu);
        if (!userType.equals("user")) {
            popup.getMenu().getItem(0).setVisible(true);
        }else {
            popup.getMenu().getItem(0).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bulk_message:
                        startActivity(new Intent(MainActivity.this, BulkMessageActivity.class));
                    break;
                    case R.id.logout:
                        isOnline("0");
                        sessionManager.logout();
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(MainActivity.this, LoginActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        break;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    default:
                        break;

                }
                return false;
            }
        });

        popup.show();
    }

    private void isOnline(String status) {
        userViewModel.isOnline("Bearer " + api_key, user_id, status);
    }

}
