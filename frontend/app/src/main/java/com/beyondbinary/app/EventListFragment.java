package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.agents.EventRankingAgent;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.EventsResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListFragment extends Fragment {

    private static final String TAG = "EventListFragment";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EventListAdapter adapter;
    private List<Event> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        recyclerView = view.findViewById(R.id.events_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventListAdapter(events, event -> {
            Intent intent = new Intent(getActivity(), EventDetailActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadEvents();

        return view;
    }

    private void loadEvents() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService();
        Call<EventsResponse> call = apiService.getAllEvents();

        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body().getEvents();
                    rankWithAI(allEvents);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rankWithAI(List<Event> allEvents) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("beyondbinary_prefs", android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        Log.i(TAG, "rankWithAI: userId from prefs = " + userId);

        AppDatabaseHelper db = AppDatabaseHelper.getInstance(requireContext());
        User user = (userId != -1) ? db.getUserById(userId) : null;
        Log.i(TAG, "rankWithAI: user found = " + (user != null) +
                ", bio = " + (user != null ? ("'" + user.getBio() + "'") : "N/A"));

        if (user != null && user.getBio() != null && !user.getBio().isEmpty()) {
            Log.i(TAG, "rankWithAI: Calling Gemini for AI ranking...");
            String apiKey = BuildConfig.GEMINI_API_KEY;
            EventRankingAgent agent = new EventRankingAgent(apiKey);

            agent.rankEvents(user, allEvents, rankedEvents -> {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        events.clear();
                        events.addAll(rankedEvents);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Ranked by AI based on your preferences", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "rankWithAI: AI-ranked events displayed");
                    });
                }
            });
        } else {
            Log.w(TAG, "rankWithAI: No bio found — showing default order");
            // No user profile — show events in default order
            events.clear();
            events.addAll(allEvents);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }
}
