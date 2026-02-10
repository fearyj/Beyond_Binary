package com.beyondbinary.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.messaging.ChatActivity;
import com.beyondbinary.app.messaging.MessageConversation;
import com.beyondbinary.app.messaging.MessagesAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private List<MessageConversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Hardcoded conversations
        conversations = new ArrayList<>();
        conversations.add(new MessageConversation("Alicia Thor", "Alicia sent an invitation.", "18:00", "👩"));
        conversations.add(new MessageConversation("Celeste Floyd", "See you on Sunday!", "14:33", "😊"));
        conversations.add(new MessageConversation("Kamari Ponce", "You: Are you free on this Sunday?", "13:21", "🧑"));
        conversations.add(new MessageConversation("Martin Blankenship", "Great, I'll bring the snacks!", "11:58", "👨"));
        conversations.add(new MessageConversation("Paige Salinas", "You: Thinking of checking out that new...", "11:00", "👩‍🦰"));
        conversations.add(new MessageConversation("Vincenzo Roberts", "Haha, worth it for sure!!", "10:58", "😎"));
        conversations.add(new MessageConversation("Marceline Avila", "Perfect! Let's do it.", "09:46", "👱‍♀️"));

        adapter = new MessagesAdapter(conversations, conversation -> {
            // Open chat screen
            Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
            intent.putExtra("CONTACT_NAME", conversation.getName());
            intent.putExtra("PROFILE_EMOJI", conversation.getProfileEmoji());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Setup back button
        android.view.View backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> goBackToHome());
        }

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void goBackToHome() {
        Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBackToHome();
        super.onBackPressed();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home); // No messages tab yet, default to home

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_chatbot) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("OPEN_CHATBOT", true);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_my_events) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("OPEN_MY_EVENTS", true);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_map) {
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }
}
