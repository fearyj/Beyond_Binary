package com.beyondbinary.app.chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.AddEventActivity;
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
    private View sendButton;
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

        // Set suggestion click listener
        adapter.setOnSuggestionClickListener((eventType, maxParticipants, descriptionHint, userContext) -> {
            String title = generateEventTitle(eventType, userContext);
            createEvent(eventType, title, maxParticipants, descriptionHint);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Add welcome message
        addTextMessage("Hi there! I'm Buddeee AI, your Buddeee assistant. I can help you discover events, create new events, or chat about anything! Ask me about events and I'll search our database for you!", false);

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
        String systemPrompt = "You are Buddeee AI, a friendly AI assistant for Buddeee, a community app that helps people discover and join local events.\n\n" +
                "IMPORTANT: Analyze the user's message and determine their intent.\n\n" +
                "If the user wants to CREATE/HOST/ORGANIZE an event WITH a description (e.g., 'looking forward to a relaxing event for small group'):\n" +
                "- Suggest 3 event types that match their description\n" +
                "- Format: SUGGEST_EVENTS: type1 | type2 | type3 | max_participants | description_hint\n" +
                "- Examples:\n" +
                "  * 'relaxing event for small group' → SUGGEST_EVENTS: Yoga Class | Coffee Meetup | Meditation Session | 8 | A relaxing activity for a small, intimate group\n" +
                "  * 'outdoor adventure for large group' → SUGGEST_EVENTS: Hiking Trip | Beach Volleyball | Park Picnic | 20 | An exciting outdoor activity for many people\n" +
                "  * 'competitive sports' → SUGGEST_EVENTS: Soccer Match | Basketball Game | Tennis Tournament | 12 | A competitive sports event\n\n" +
                "If the user is SEARCHING for existing events:\n" +
                "- Respond with: SEARCH_EVENTS: <search keywords>\n" +
                "- Translate descriptive terms to actual event types\n" +
                "- Examples: 'find yoga classes' → SEARCH_EVENTS: yoga meditation wellness\n\n" +
                "If the user is having a normal conversation:\n" +
                "- Provide a friendly, helpful response (2-3 sentences max)\n" +
                "- Mention they can search for events OR create their own events\n\n" +
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
                        // Check AI response type
                        if (botResponse.startsWith("SEARCH_EVENTS:")) {
                            String searchQuery = botResponse.substring("SEARCH_EVENTS:".length()).trim();
                            searchEvents(searchQuery, userMessage);
                        } else if (botResponse.startsWith("SUGGEST_EVENTS:")) {
                            String suggestions = botResponse.substring("SUGGEST_EVENTS:".length()).trim();
                            showEventSuggestions(suggestions, userMessage);
                        } else if (botResponse.trim().equals("CREATE_EVENT")) {
                            // User wants to create an event (no description)
                            createEvent("", "", 10, "");
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

    private void showEventSuggestions(String suggestionsData, String userMessage) {
        // Parse: type1 | type2 | type3 | max_participants | description_hint
        String[] parts = suggestionsData.split("\\|");
        if (parts.length < 3) {
            addTextMessage("Sorry, I had trouble generating suggestions. Please try again!", false);
            return;
        }

        List<String> eventTypes = new ArrayList<>();
        for (int i = 0; i < Math.min(3, parts.length); i++) {
            eventTypes.add(parts[i].trim());
        }

        int maxParticipants = parts.length > 3 ? parseIntOrDefault(parts[3].trim(), 10) : 10;
        String descriptionHint = parts.length > 4 ? parts[4].trim() : "";

        // Show suggestions as clickable cards
        addTextMessage("Here are 3 event ideas for you! Tap one to start creating:", false);
        addSuggestionMessage(eventTypes, maxParticipants, descriptionHint, userMessage);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void addSuggestionMessage(List<String> eventTypes, int maxParticipants, String descriptionHint, String userContext) {
        messageList.add(new Message(eventTypes, maxParticipants, descriptionHint, userContext));
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private String generateEventTitle(String eventType, String userContext) {
        if (userContext == null || userContext.isEmpty()) {
            return eventType;
        }

        String lowerContext = userContext.toLowerCase();
        StringBuilder titleBuilder = new StringBuilder();

        // Extract descriptive terms
        if (lowerContext.contains("relaxing") || lowerContext.contains("relax")) {
            titleBuilder.append("Relaxing ");
        } else if (lowerContext.contains("intense") || lowerContext.contains("high intensity")) {
            titleBuilder.append("Intense ");
        } else if (lowerContext.contains("fun") || lowerContext.contains("exciting")) {
            titleBuilder.append("Fun ");
        } else if (lowerContext.contains("competitive")) {
            titleBuilder.append("Competitive ");
        } else if (lowerContext.contains("casual")) {
            titleBuilder.append("Casual ");
        }

        // Add event type
        titleBuilder.append(eventType);

        // Add group size context
        if (lowerContext.contains("small group")) {
            titleBuilder.append(" - Small Group");
        } else if (lowerContext.contains("large group") || lowerContext.contains("big group")) {
            titleBuilder.append(" - Large Group");
        } else if (lowerContext.contains("intimate")) {
            titleBuilder.append(" - Intimate");
        }

        return titleBuilder.toString();
    }

    private void createEvent(String eventType, String title, int maxParticipants, String description) {
        // Show confirmation message
        if (eventType.isEmpty()) {
            addTextMessage("Great! Opening the event creation form... 📝", false);
        } else {
            addTextMessage("Perfect! Let me help you create a " + eventType + " event. 📝", false);
        }

        // Navigate to AddEventActivity with pre-filled data
        Intent intent = new Intent(getActivity(), AddEventActivity.class);
        intent.putExtra("EVENT_TYPE", eventType);
        intent.putExtra("EVENT_TITLE", title);
        intent.putExtra("MAX_PARTICIPANTS", maxParticipants);
        intent.putExtra("EVENT_DESCRIPTION", description);
        startActivity(intent);
    }
}
