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

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.EventsResponse;
import com.beyondbinary.app.api.InteractionsResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedEventsFragment extends Fragment {

    private static final String TAG = "RecommendedEvents";

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

        loadRecommendedEvents();

        return view;
    }

    private void loadRecommendedEvents() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("beyondbinary_prefs", android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        ApiService apiService = RetrofitClient.getApiService();

        // Fetch all events first
        apiService.getAllEvents().enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body().getEvents();

                    if (userId != -1) {
                        // Fetch interaction history
                        apiService.getUserInteractions(userId).enqueue(new Callback<InteractionsResponse>() {
                            @Override
                            public void onResponse(Call<InteractionsResponse> call2,
                                                   Response<InteractionsResponse> response2) {
                                if (!isAdded()) return;

                                List<InteractionsResponse.Interaction> interactions =
                                        (response2.isSuccessful() && response2.body() != null)
                                                ? response2.body().getInteractions()
                                                : new ArrayList<>();

                                rankWithAI(allEvents, interactions, userId);
                            }

                            @Override
                            public void onFailure(Call<InteractionsResponse> call2, Throwable t) {
                                if (!isAdded()) return;
                                // Fall back to AI ranking without history
                                rankWithAI(allEvents, new ArrayList<>(), userId);
                            }
                        });
                    } else {
                        showEvents(allEvents);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load recommended events", Toast.LENGTH_SHORT).show();
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

    private void rankWithAI(List<Event> allEvents, List<InteractionsResponse.Interaction> interactions, int userId) {
        AppDatabaseHelper db = AppDatabaseHelper.getInstance(requireContext());
        User user = db.getUserById(userId);

        String bio = (user != null && user.getBio() != null) ? user.getBio() : "";
        Log.i(TAG, "rankWithAI: userId=" + userId + ", bio='" + bio + "', interactions=" + interactions.size());

        // Build prompt
        StringBuilder sb = new StringBuilder();
        if (!bio.isEmpty()) {
            sb.append("User preferences: ").append(bio).append("\n\n");
        }

        if (!interactions.isEmpty()) {
            sb.append("Past interactions:\n");
            for (InteractionsResponse.Interaction interaction : interactions) {
                sb.append("- ").append(interaction.getInteractionType())
                  .append(": ").append(interaction.getTitle())
                  .append(" (").append(interaction.getEventType()).append(")\n");
            }
            sb.append("\n");
        }

        if (bio.isEmpty() && interactions.isEmpty()) {
            Log.w(TAG, "rankWithAI: No bio and no interactions — showing default order");
            showEvents(allEvents);
            return;
        }

        sb.append("Recommend and rank these events. Prioritize events similar to past engagement, ");
        sb.append("but also suggest new types based on their profile.\n");
        sb.append("Return ONLY a comma-separated list of event IDs (most recommended first), nothing else.\n\n");
        sb.append("Events:\n");

        for (Event event : allEvents) {
            sb.append("ID:").append(event.getId())
              .append(" | ").append(event.getTitle())
              .append(" | ").append(event.getEventType())
              .append(" | ").append(event.getDescription())
              .append("\n");
        }

        String prompt = sb.toString();
        Log.i(TAG, "=== GEMINI RECOMMENDATION REQUEST ===");
        Log.d(TAG, "Full prompt:\n" + prompt);

        String apiKey = BuildConfig.GEMINI_API_KEY;
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (!isAdded()) return;
                try {
                    String text = result.getText();
                    Log.i(TAG, "=== GEMINI RAW RESPONSE ===");
                    Log.i(TAG, "Response text: " + text);
                    if (text != null) {
                        List<Event> ranked = parseRankedEvents(text.trim(), allEvents);
                        Log.i(TAG, "AI recommendation successful, matched " + ranked.size() + "/" + allEvents.size() + " events by ID");
                        requireActivity().runOnUiThread(() -> {
                            showEvents(ranked);
                            Toast.makeText(getContext(), "Recommended by AI", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Log.w(TAG, "AI returned null text, using default order");
                        requireActivity().runOnUiThread(() -> showEvents(allEvents));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing AI response", e);
                    requireActivity().runOnUiThread(() -> showEvents(allEvents));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "=== GEMINI CALL FAILED ===");
                Log.e(TAG, "AI recommendation failed: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
                requireActivity().runOnUiThread(() -> showEvents(allEvents));
            }
        }, Executors.newSingleThreadExecutor());
    }

    private List<Event> parseRankedEvents(String response, List<Event> original) {
        Map<Integer, Event> eventMap = new HashMap<>();
        for (Event event : original) {
            eventMap.put(event.getId(), event);
        }

        List<Event> ranked = new ArrayList<>();
        String[] ids = response.split(",");
        for (String idStr : ids) {
            try {
                int id = Integer.parseInt(idStr.trim());
                Event event = eventMap.remove(id);
                if (event != null) {
                    ranked.add(event);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        for (Event event : original) {
            if (eventMap.containsKey(event.getId())) {
                ranked.add(event);
            }
        }

        return ranked;
    }

    private void showEvents(List<Event> eventList) {
        events.clear();
        events.addAll(eventList);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }
}
