package com.beyondbinary.app.chatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.BuildConfig;
import com.beyondbinary.app.Event;
import com.beyondbinary.app.R;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.EventsResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.TextPart;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {

    private static final String TAG = "ChatbotFragment";
    private RecyclerView recyclerView;
    private ChatbotAdapter adapter;
    private List<Message> messageList;
    private EditText messageInput;
    private Button sendButton;
    private GenerativeModelFutures generativeModel;
    private List<String> conversationHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the Generative Model
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", BuildConfig.GEMINI_API_KEY);
        generativeModel = GenerativeModelFutures.from(gm);

        recyclerView = view.findViewById(R.id.recyclerView_chatbot);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        conversationHistory = new ArrayList<>();
        adapter = new ChatbotAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Add welcome message
        addTextMessage("👋 Hi! I'm your Beyond Binary AI assistant. I can help you discover events, answer questions about activities, or chat about anything! Ask me about events and I'll search our database for you!", false);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Add user message
                addTextMessage(messageText, true);
                conversationHistory.add("User: " + messageText);
                messageInput.setText("");

                // Get bot response
                getChatbotResponse(messageText);
            }
        });
    }

    private void getChatbotResponse(String userMessage) {
        // Build conversation context
        StringBuilder context = new StringBuilder();
        for (String history : conversationHistory) {
            context.append(history).append("\n");
        }

        // System prompt to determine intent and search for events
        String systemPrompt = "You are a helpful AI assistant for Beyond Binary, a community app that helps people discover and join local events.\n\n" +
                "IMPORTANT: Analyze the user's message and determine if they are asking about finding/searching/looking for events or activities.\n\n" +
                "If the user is asking about events (examples: 'find soccer events', 'show me yoga classes', 'looking for coffee meetups', 'what events are near me'):\n" +
                "- Respond ONLY with: SEARCH_EVENTS: <search keywords>\n" +
                "- Extract key event type keywords (e.g., 'soccer', 'yoga', 'coffee', 'hiking')\n" +
                "- Example responses: 'SEARCH_EVENTS: soccer', 'SEARCH_EVENTS: yoga coffee', 'SEARCH_EVENTS: outdoor hiking'\n\n" +
                "If the user is having a normal conversation or asking general questions:\n" +
                "- Provide a friendly, helpful response\n" +
                "- Keep responses concise (2-3 sentences max)\n" +
                "- Be encouraging and mention they can ask about finding events\n\n" +
                "Conversation history:\n" + context + "\n" +
                "User: " + userMessage + "\n\n" +
                "Your response:";

        List<TextPart> parts = new ArrayList<>();
        parts.add(new TextPart(systemPrompt));

        Content content = new Content(parts);
        Executor executor = Executors.newSingleThreadExecutor();

        Futures.addCallback(generativeModel.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText().trim();
                conversationHistory.add("Assistant: " + botResponse);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Check if AI wants to search for events
                        if (botResponse.startsWith("SEARCH_EVENTS:")) {
                            String searchQuery = botResponse.substring("SEARCH_EVENTS:".length()).trim();
                            searchEvents(searchQuery, userMessage);
                        } else {
                            // Normal conversation
                            addTextMessage(botResponse, false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addTextMessage("Sorry, I encountered an error. Please try again.", false));
                }
            }
        }, executor);
    }

    private void searchEvents(String searchQuery, String originalUserMessage) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<EventsResponse> call = apiService.getAllEvents();

        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call, @NonNull Response<EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body().getEvents();
                    List<Event> matchingEvents = filterEvents(allEvents, searchQuery);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (matchingEvents.isEmpty()) {
                                addTextMessage("Sorry, I couldn't find any events matching '" + searchQuery + "'. Try asking about different types of activities!", false);
                            } else {
                                // Limit to 3 events
                                List<Event> topEvents = matchingEvents.subList(0, Math.min(3, matchingEvents.size()));
                                String intro = "I found " + topEvents.size() + " event(s) for you! Tap any card to view details:";
                                addEventMessage(topEvents, intro);
                            }
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> addTextMessage("Sorry, I had trouble searching for events. Please try again.", false));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error searching events: " + t.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addTextMessage("Sorry, I couldn't connect to the event database. Make sure the backend is running.", false));
                }
            }
        });
    }

    private List<Event> filterEvents(List<Event> events, String searchQuery) {
        List<Event> filtered = new ArrayList<>();
        String[] keywords = searchQuery.toLowerCase().split("\\s+");

        for (Event event : events) {
            String eventData = (event.getTitle() + " " + event.getEventType() + " " +
                    event.getDescription() + " " + event.getLocation()).toLowerCase();

            for (String keyword : keywords) {
                if (eventData.contains(keyword)) {
                    filtered.add(event);
                    break;
                }
            }
        }

        return filtered;
    }

    private void addTextMessage(String text, boolean isSentByUser) {
        messageList.add(new Message(text, isSentByUser));
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void addEventMessage(List<Event> events, String introText) {
        messageList.add(new Message(events, introText));
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}
