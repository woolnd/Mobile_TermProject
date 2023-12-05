package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {
    private int mPageCount = 2;
    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                TodayFragment frag1 = new TodayFragment();
                return frag1;
            case 1:
                YesterdayFragment frag2 = new YesterdayFragment();
                return frag2;
            default:
                return null;
        }

    }

    @Override
    public int getItemCount() {
        return mPageCount;
    }
}