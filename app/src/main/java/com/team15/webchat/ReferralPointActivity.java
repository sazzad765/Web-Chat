package com.team15.webchat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.team15.webchat.Adapters.PagerAdapter2;
import com.team15.webchat.Fragment.RefPointHistoryFragment;
import com.team15.webchat.Fragment.RefPointTotalFragment;


public class ReferralPointActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String[] tabTitles = {
            "Referral Point", "Referral History"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_point);
        viewPager = findViewById(R.id.pager_follow);

        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab_layout_follow);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabTitles.length; i++) {
            TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabOne.setText(tabTitles[i]);
            tabOne.setTextColor(Color.parseColor("#180000"));
            tabLayout.getTabAt(i).setCustomView(tabOne);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter2 adapter = new PagerAdapter2(getSupportFragmentManager());
        adapter.addFrag(new RefPointTotalFragment());
        adapter.addFrag(new RefPointHistoryFragment());

        viewPager.setAdapter(adapter);
    }

    public void imgBack(View view) {
        finish();
    }
}