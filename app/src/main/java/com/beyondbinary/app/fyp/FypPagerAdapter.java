package com.beyondbinary.app.fyp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FypPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 2;

    public FypPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PersonalizedFragment();
        } else {
            return new RecommendedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
