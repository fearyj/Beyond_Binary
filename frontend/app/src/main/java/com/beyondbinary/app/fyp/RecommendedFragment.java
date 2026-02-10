package com.beyondbinary.app.fyp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.R;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.Event;
import com.beyondbinary.app.data.providers.HealthDataProvider;

import java.util.Arrays;
import java.util.List;

public class RecommendedFragment extends Fragment {

    private static final String TAG = "RecommendedFragment";
    private EventCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.feed_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        adapter = new EventCardAdapter();
        recyclerView.setAdapter(adapter);

        loadRecommendations();
    }

    private void loadRecommendations() {
        HealthDataProvider.HealthData health = HealthDataProvider.getHealthData(requireContext());
        Log.d(TAG, "Health data: steps=" + health.steps + ", heartRate=" + health.heartRate);

        AppDatabaseHelper db = AppDatabaseHelper.getInstance(requireContext());
        List<String> categories;

        if (health.steps < 4000) {
            // Low activity — suggest Walking and Outdoor events
            categories = Arrays.asList("Walking", "Outdoor");
            Log.d(TAG, "Low steps (" + health.steps + "), recommending Walking/Outdoor");
        } else if (health.heartRate > 85) {
            // High heart rate — suggest calming activities
            categories = Arrays.asList("Meditation", "Yoga");
            Log.d(TAG, "High HR (" + health.heartRate + "), recommending Meditation/Yoga");
        } else {
            // Default — show all events
            List<Event> allEvents = db.getAllEvents();
            Log.d(TAG, "Normal health, showing all " + allEvents.size() + " events");
            adapter.setEvents(allEvents);
            return;
        }

        List<Event> events = db.getEventsByCategories(categories);
        Log.d(TAG, "Showing " + events.size() + " recommended events for categories: " + categories);
        adapter.setEvents(events);
    }
}
