package com.beyondbinary.app.fyp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.beyondbinary.app.BuildConfig;
import com.beyondbinary.app.R;
import com.beyondbinary.app.agents.EventRankingAgent;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.Event;
import com.beyondbinary.app.data.models.User;

import java.util.List;

public class PersonalizedFragment extends Fragment {

    private static final String TAG = "PersonalizedFragment";
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

        loadEvents();
    }

    private void loadEvents() {
        AppDatabaseHelper db = AppDatabaseHelper.getInstance(requireContext());
        User user = db.getUser();

        if (user == null) {
            Log.w(TAG, "No user found in database");
            return;
        }

        List<String> interests = user.getInterestTagsAsList();
        List<Event> events = db.getEventsByCategories(interests);

        Log.d(TAG, "Found " + events.size() + " events for interests: " + interests);

        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey != null && !apiKey.isEmpty()) {
            EventRankingAgent agent = new EventRankingAgent(apiKey);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            agent.rankEvents(user, events, rankedEvents ->
                    mainHandler.post(() -> adapter.setEvents(rankedEvents)));
        } else {
            Log.w(TAG, "No Gemini API key configured, using default order");
            adapter.setEvents(events);
        }
    }
}
