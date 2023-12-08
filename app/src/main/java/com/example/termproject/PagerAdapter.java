package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {
    private int mPageCount = 2;

    // PagerAdapter 생성자
    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    // 해당 position에 맞는 Fragment를 생성하는 메서드
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                // 오늘의 뉴스 Fragment 생성
                TodayFragment frag1 = new TodayFragment();
                return frag1;
            case 1:
                // 어제의 뉴스 Fragment 생성
                YesterdayFragment frag2 = new YesterdayFragment();
                return frag2;
            default:
                return null;
        }
    }

    // Fragment 개수 반환
    @Override
    public int getItemCount() {
        return mPageCount;
    }
}
