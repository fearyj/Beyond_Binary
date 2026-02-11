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
import com.beyondbinary.app.Event;
import com.beyondbinary.app.R;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.ChatbotRequest;
import com.beyondbinary.app.api.ChatbotResponse;
import com.beyondbinary.app.api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

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
    private List<ChatbotRequest.ConversationEntry> conversationHistory;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();

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
                conversationHistory.add(new ChatbotRequest.ConversationEntry("user", messageText));
                messageInput.setText("");

                // Get bot response
                getChatbotResponse(messageText);
            }
        });
    }

    private void getChatbotResponse(String userMessage) {
        ChatbotRequest request = new ChatbotRequest(userMessage, null, conversationHistory);

        apiService.sendChatMessage(request).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatbotResponse> call, @NonNull Response<ChatbotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatbotResponse chatResponse = response.body();
                    conversationHistory.add(new ChatbotRequest.ConversationEntry("assistant", chatResponse.getMessage()));

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> handleChatbotResponse(chatResponse));
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> addTextMessage("Sorry, I had trouble processing that. Please try again.", false));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatbotResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Chatbot API error: " + t.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addTextMessage("Sorry, I couldn't connect to the server. Make sure the backend is running.", false));
                }
            }
        });
    }

    private void handleChatbotResponse(ChatbotResponse response) {
        String type = response.getType();

        if ("events".equals(type) && response.getEvents() != null && !response.getEvents().isEmpty()) {
            List<Event> events = response.getEvents();
            List<Event> topEvents = events.subList(0, Math.min(3, events.size()));
            addEventMessage(topEvents, response.getMessage());
        } else if ("suggestions".equals(type) && response.getSuggestions() != null && !response.getSuggestions().isEmpty()) {
            addTextMessage(response.getMessage(), false);
            List<String> eventTypes = new ArrayList<>();
            int maxParticipants = 10;
            String descriptionHint = "";

            for (ChatbotResponse.Suggestion suggestion : response.getSuggestions()) {
                eventTypes.add(suggestion.getEventType());
                maxParticipants = suggestion.getMaxParticipants();
                descriptionHint = suggestion.getDescriptionHint();
            }

            // Use the last message from conversationHistory as user context
            String userContext = "";
            for (int i = conversationHistory.size() - 1; i >= 0; i--) {
                if ("user".equals(conversationHistory.get(i).getRole())) {
                    userContext = conversationHistory.get(i).getContent();
                    break;
                }
            }
            addSuggestionMessage(eventTypes, maxParticipants, descriptionHint, userContext);
        } else {
            addTextMessage(response.getMessage(), false);
        }
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
            addTextMessage("Great! Opening the event creation form...", false);
        } else {
            addTextMessage("Perfect! Let me help you create a " + eventType + " event.", false);
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
