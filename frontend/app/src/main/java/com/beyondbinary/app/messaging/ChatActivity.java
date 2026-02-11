package com.beyondbinary.app.messaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.R;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.MessagesResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.SendMessageResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private EditText messageInput;
    private FloatingActionButton sendButton;
    private TextView contactName;
    private TextView profileEmoji;

    private int currentUserId = -1;
    private int receiverId = -1;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        contactName = findViewById(R.id.chat_contact_name);
        profileEmoji = findViewById(R.id.chat_profile_emoji);

        apiService = RetrofitClient.getApiService();

        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        // Get contact info from intent
        String name = getIntent().getStringExtra("CONTACT_NAME");
        String emoji = getIntent().getStringExtra("PROFILE_EMOJI");
        receiverId = getIntent().getIntExtra("receiver_id", -1);

        contactName.setText(name != null ? name : "Contact");
        profileEmoji.setText(emoji != null ? emoji : "\uD83D\uDC64");

        // Setup back button
        View backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> goBackToMessages());
        }

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Send button click listener
        sendButton.setOnClickListener(v -> sendTextMessage());

        // If coming from ShareEventActivity with a shared event, show the invite card immediately
        addSharedEventInvite();

        // Also load full conversation from API
        loadConversation();
    }

    private void addSharedEventInvite() {
        int sharedEventId = getIntent().getIntExtra("shared_event_id", -1);
        String sharedTitle = getIntent().getStringExtra("shared_event_title");

        if (sharedEventId != -1 && sharedTitle != null) {
            String sharedTime = getIntent().getStringExtra("shared_event_time");
            String sharedLocation = getIntent().getStringExtra("shared_event_location");
            String sharedType = getIntent().getStringExtra("shared_event_type");
            int sharedCurrent = getIntent().getIntExtra("shared_event_current", 0);
            int sharedMax = getIntent().getIntExtra("shared_event_max", 0);

            ChatMessage inviteMsg = new ChatMessage(
                    "invited you to: " + sharedTitle,
                    true,
                    sharedEventId,
                    sharedTitle,
                    sharedTime != null ? sharedTime : "",
                    sharedLocation != null ? sharedLocation : "",
                    sharedType != null ? sharedType : "",
                    sharedCurrent,
                    sharedMax
            );
            messages.add(inviteMsg);
            adapter.notifyItemInserted(messages.size() - 1);
            scrollToBottom();
        }
    }

    private void loadConversation() {
        if (currentUserId == -1 || receiverId == -1) {
            Log.w(TAG, "Cannot load conversation: currentUserId=" + currentUserId + " receiverId=" + receiverId);
            return;
        }

        apiService.getMessages(currentUserId, receiverId).enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<MessagesResponse.Message> apiMessages = response.body().getMessages();

                    if (apiMessages != null && !apiMessages.isEmpty()) {
                        // Replace local messages with the full server conversation
                        messages.clear();

                        for (MessagesResponse.Message msg : apiMessages) {
                            boolean sentByMe = msg.getSenderId() == currentUserId;

                            if ("event_invite".equals(msg.getType()) && msg.getEventId() != null) {
                                messages.add(new ChatMessage(
                                        msg.getText(),
                                        sentByMe,
                                        msg.getEventId(),
                                        msg.getEventTitle(),
                                        msg.getEventTime(),
                                        msg.getEventLocation(),
                                        msg.getEventType(),
                                        msg.getCurrentParticipants(),
                                        msg.getMaxParticipants()
                                ));
                            } else {
                                messages.add(new ChatMessage(msg.getText(), sentByMe));
                            }
                        }

                        adapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                    // If API returns empty but we have a local invite from intent, keep it
                } else {
                    Log.w(TAG, "Failed to load messages: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
                Log.e(TAG, "Error loading messages", t);
            }
        });
    }

    private void sendTextMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        if (currentUserId == -1 || receiverId == -1) {
            Toast.makeText(this, "Unable to send message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to local list immediately for responsiveness
        messages.add(new ChatMessage(messageText, true));
        adapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
        messageInput.setText("");

        // Send to API
        Map<String, Object> body = new HashMap<>();
        body.put("sender_id", currentUserId);
        body.put("receiver_id", receiverId);
        body.put("text", messageText);

        apiService.sendMessage(body).enqueue(new Callback<SendMessageResponse>() {
            @Override
            public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "Failed to send message: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                Log.e(TAG, "Error sending message", t);
            }
        });
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    private void goBackToMessages() {
        Intent intent = new Intent(ChatActivity.this, com.beyondbinary.app.MessagesActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBackToMessages();
        super.onBackPressed();
    }
}
