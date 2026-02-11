package com.beyondbinary.app.agents;

import android.util.Log;

import com.beyondbinary.app.Event;
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

public class EventRankingAgent {

    private static final String TAG = "EventRankingAgent";
    private final GenerativeModelFutures model;

    public interface Callback {
        void onResult(List<Event> rankedEvents);
    }

    public EventRankingAgent(String apiKey) {
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey);
        this.model = GenerativeModelFutures.from(gm);
    }

    public void rankEvents(User user, List<Event> events, Callback callback) {
        if (events == null || events.isEmpty()) {
            callback.onResult(events != null ? events : new ArrayList<>());
            return;
        }

        String prompt = buildPrompt(user, events);
        Log.i(TAG, "=== GEMINI RANKING REQUEST ===");
        Log.i(TAG, "User bio: " + user.getBio());
        Log.i(TAG, "Events count: " + events.size());
        Log.d(TAG, "Full prompt:\n" + prompt);

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                try {
                    String text = result.getText();
                    Log.i(TAG, "=== GEMINI RAW RESPONSE ===");
                    Log.i(TAG, "Response text: " + text);
                    if (text != null) {
                        List<Event> ranked = parseRankedEvents(text.trim(), events);
                        Log.i(TAG, "AI ranking successful, ranked " + ranked.size() + "/" + events.size() + " events matched by ID");
                        callback.onResult(ranked);
                    } else {
                        Log.w(TAG, "AI returned null text, using original order");
                        callback.onResult(events);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing AI response", e);
                    callback.onResult(events);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "=== GEMINI CALL FAILED ===");
                Log.e(TAG, "AI ranking failed: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
                callback.onResult(events);
            }
        }, Executors.newSingleThreadExecutor());
    }

    private String buildPrompt(User user, List<Event> events) {
        StringBuilder sb = new StringBuilder();
        sb.append("Given this user's questionnaire answers: ").append(user.getBio()).append("\n\n");
        sb.append("Rank these events by how meaningful and relevant they are to this person.\n");
        sb.append("Return ONLY a comma-separated list of event IDs (most relevant first), nothing else.\n\n");
        sb.append("Events:\n");

        for (Event event : events) {
            sb.append("ID:").append(event.getId())
              .append(" | ").append(event.getTitle())
              .append(" | ").append(event.getEventType())
              .append(" | ").append(event.getDescription())
              .append("\n");
        }

        return sb.toString();
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

        // Append any events not mentioned by the AI
        for (Event event : original) {
            if (eventMap.containsKey(event.getId())) {
                ranked.add(event);
            }
        }

        return ranked;
    }
}
