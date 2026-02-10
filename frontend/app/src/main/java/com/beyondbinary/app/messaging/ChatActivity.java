package com.beyondbinary.app.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private EditText messageInput;
    private FloatingActionButton sendButton;
    private TextView contactName;
    private TextView profileEmoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        contactName = findViewById(R.id.chat_contact_name);
        profileEmoji = findViewById(R.id.chat_profile_emoji);

        // Get contact info from intent
        String name = getIntent().getStringExtra("CONTACT_NAME");
        String emoji = getIntent().getStringExtra("PROFILE_EMOJI");

        contactName.setText(name != null ? name : "Contact");
        profileEmoji.setText(emoji != null ? emoji : "👤");

        // Setup back button
        View backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> goBackToMessages());
        }

        // Hardcoded messages for demonstration
        messages = new ArrayList<>();
        messages.add(new ChatMessage("Hey! How are you?", false));
        messages.add(new ChatMessage("I'm good! Thanks for asking 😊", true));
        messages.add(new ChatMessage("Want to join the soccer event this weekend?", false));
        messages.add(new ChatMessage("Sounds great! What time?", true));
        messages.add(new ChatMessage("Saturday at 3 PM at Central Park", false));

        adapter = new ChatAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Scroll to bottom
        if (messages.size() > 0) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                messages.add(new ChatMessage(messageText, true));
                adapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
                messageInput.setText("");
            }
        });
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
