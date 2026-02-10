package com.beyondbinary.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private View tabPersonalised;
    private View tabRecommended;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_with_tabs, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        tabPersonalised = view.findViewById(R.id.tab_personalised);
        tabRecommended = view.findViewById(R.id.tab_recommended);
        View fabCreateEvent = view.findViewById(R.id.fab_create_event);

        // Setup ViewPager with adapter
        HomePagerAdapter adapter = new HomePagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        // Tab click listeners
        tabPersonalised.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
            updateTabState(0);
        });

        tabRecommended.setOnClickListener(v -> {
            viewPager.setCurrentItem(1);
            updateTabState(1);
        });

        // Sync tab state when swiping
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabState(position);
            }
        });

        // FAB click
        fabCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            startActivity(intent);
        });

        // Message icon click
        View messageIcon = view.findViewById(R.id.message_icon);
        messageIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MessagesActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void updateTabState(int selectedPosition) {
        if (selectedPosition == 0) {
            tabPersonalised.setBackgroundResource(R.drawable.bg_tab_active);
            tabRecommended.setBackgroundResource(R.drawable.bg_tab_inactive);
        } else {
            tabPersonalised.setBackgroundResource(R.drawable.bg_tab_inactive);
            tabRecommended.setBackgroundResource(R.drawable.bg_tab_active);
        }
    }
}
