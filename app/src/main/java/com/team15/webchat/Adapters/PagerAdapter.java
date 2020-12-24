package com.team15.webchat.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class PagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }


    //
//    public PagerAdapter(FragmentManager manager) {
//        super(manager);
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return mFragmentList.get(position);
//    }
//
//    @Override
//    public int getCount() {
//        return mFragmentList.size();
//    }
//
    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

}