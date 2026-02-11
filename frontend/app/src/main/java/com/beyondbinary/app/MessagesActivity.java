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

        // Hardcoded conversations with avatar URLs
        conversations = new ArrayList<>();
        conversations.add(new MessageConversation("Alicia Thor", "Alicia sent an invitation.", "18:00", "👩",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop"));
        conversations.add(new MessageConversation("Celeste Floyd", "See you on Sunday!", "14:33", "😊",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop"));
        conversations.add(new MessageConversation("Kamari Ponce", "You: Are you free on this Sunday?", "13:21", "🧑",
                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop"));
        conversations.add(new MessageConversation("Martin Blankenship", "Great, I'll bring the snacks!", "11:58", "👨",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop"));
        conversations.add(new MessageConversation("Paige Salinas", "You: Thinking of checking out that new...", "11:00", "👩‍🦰",
                "https://images.unsplash.com/photo-1525134479668-1bee5c7c6845?w=100&h=100&fit=crop"));
        conversations.add(new MessageConversation("Vincenzo Roberts", "Haha, worth it for sure!!", "10:58", "😎",
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=100&h=100&fit=crop"));
        conversations.add(new MessageConversation("Marceline Avila", "See you later!", "09:46", "👱‍♀️",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop"));

        adapter = new MessagesAdapter(conversations, conversation -> {
            // Open chat screen
            Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
            intent.putExtra("CONTACT_NAME", conversation.getName());
            intent.putExtra("PROFILE_EMOJI", conversation.getProfileEmoji());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Setup bottom navigation
        setupBottomNavigation();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MessagesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);

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
