package com.beyondbinary.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EventListFragment(); // Personalised
            case 1:
                return new RecommendedEventsFragment(); // Recommended
            default:
                return new EventListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs
    }
}
