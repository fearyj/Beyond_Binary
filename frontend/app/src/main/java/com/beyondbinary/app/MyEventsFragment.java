package com.beyondbinary.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UserEventsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventsFragment extends Fragment implements MyEventsAdapter.OnEventClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;
    private MyEventsAdapter adapter;
    private final List<UserEventsResponse.UserEvent> eventsList = new ArrayList<>();
    private ActivityResultLauncher<Intent> eventDetailLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register activity result launcher
        eventDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Event was left or cancelled, refresh the list
                        loadMyEvents();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_my_events);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyText = view.findViewById(R.id.text_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyEventsAdapter(eventsList, this);
        recyclerView.setAdapter(adapter);

        loadMyEvents();
    }

    private void loadMyEvents() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("beyondbinary_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            showEmpty();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserEvents(userId).enqueue(new Callback<UserEventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserEventsResponse> call,
                                   @NonNull Response<UserEventsResponse> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<UserEventsResponse.UserEvent> events = response.body().getEvents();
                    if (events != null && !events.isEmpty()) {
                        // Clear the list completely before adding new events
                        eventsList.clear();

                        // Use a Set to track event IDs and prevent duplicates
                        java.util.Set<Integer> seenIds = new java.util.HashSet<>();
                        for (UserEventsResponse.UserEvent event : events) {
                            if (event != null && !seenIds.contains(event.getId())) {
                                seenIds.add(event.getId());
                                eventsList.add(event);
                            }
                        }

                        // Check if we have any events after deduplication
                        if (!eventsList.isEmpty()) {
                            adapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showEmpty();
                        }
                    } else {
                        eventsList.clear();
                        showEmpty();
                    }
                } else {
                    eventsList.clear();
                    showEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserEventsResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                eventsList.clear();
                showEmpty();
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEventClick(UserEventsResponse.UserEvent event) {
        Intent intent = new Intent(getContext(), EventDetailActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("USER_HAS_JOINED", true); // User is viewing from My Events
        eventDetailLauncher.launch(intent); // Use launcher to get result
    }
}
