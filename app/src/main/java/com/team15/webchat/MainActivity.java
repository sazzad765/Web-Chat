package com.team15.webchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.team15.webchat.FCM.Config;
import com.team15.webchat.Fragment.ActiveListFragment;
import com.team15.webchat.Fragment.ChatListFragment;
import com.team15.webchat.Fragment.HomeFragment;
import com.team15.webchat.Fragment.ProfileFragment;
import com.team15.webchat.Heads.HeadViewService;
import com.team15.webchat.Session.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    private final static int ID_HOME = 1;
    private final static int ID_CHAT = 2;
    private final static int ID_ACTIVE_USER = 3;
    private final static int ID_ACCOUNT = 4;
    private String userType = null;
    SessionManager sessionManager;
    private ImageView imgMenu;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LightTheme);
        setContentView(R.layout.activity_main);
        imgMenu = findViewById(R.id.img_menu);

        sessionManager =  new SessionManager(this);
        HashMap<String,String> userInfo=sessionManager.get_user();
        userType = userInfo.get(SessionManager.USER_TYPE);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        //head start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
        //end
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });

        if (userType.equals("user")){
            loadFragment(new HomeFragment());
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_home_24));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_CHAT, R.drawable.ic_baseline_people_24));
        }else {
            loadFragment(new ChatListFragment());
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_CHAT, R.drawable.ic_baseline_chat_24));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACTIVE_USER, R.drawable.ic_baseline_people_24));
        }

        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_baseline_account_circle_24));

        bottomNavigation.setCount(ID_CHAT, "115");
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment;
                switch (item.getId()) {
                    case ID_HOME:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        break;
                    case ID_CHAT:
                        fragment = new ChatListFragment();
                        loadFragment(fragment);
                        break;
                    case ID_ACTIVE_USER:
                        fragment = new ActiveListFragment();
                        loadFragment(fragment);
                        break;
                    case ID_ACCOUNT:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        break;
                    default:
                        break;
                }

            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
        if (userType.equals("user")){
            bottomNavigation.show(ID_HOME, true);
        }else {
            bottomNavigation.show(ID_CHAT, true);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received

                }
            }
        };

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initializeView() {

        startService(new Intent(MainActivity.this, HeadViewService.class));
        finish();

    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

//        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        initializeView();
    }

    private void menuClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.top_menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        sessionManager.logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            if (Settings.canDrawOverlays(this)) {
//                initializeView();
            } else {
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
