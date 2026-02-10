package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.messaging.ChatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ShareEventActivity extends AppCompatActivity implements ShareUserAdapter.OnSelectionChangedListener {

    private RecyclerView recyclerView;
    private ShareUserAdapter adapter;
    private MaterialButton sendButton;
    private List<ShareUser> usersList = new ArrayList<>();
    private int eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_event);

        // Get event details from intent
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        event = (Event) getIntent().getSerializableExtra("EVENT");

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle("Share Event");

        // Setup RecyclerView
        recyclerView = findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShareUserAdapter(usersList, this);
        recyclerView.setAdapter(adapter);

        // Setup send button
        sendButton = findViewById(R.id.btn_send);
        sendButton.setOnClickListener(v -> sendToSelectedUsers());

        // Load users
        loadUsers();
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        // Enable send button only if at least one user is selected
        sendButton.setEnabled(selectedCount > 0);

        // Update button text with count
        if (selectedCount > 0) {
            sendButton.setText("Send to " + selectedCount + " " + (selectedCount == 1 ? "person" : "people"));
        } else {
            sendButton.setText("Send");
        }
    }

    private void loadUsers() {
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int currentUserId = prefs.getInt("user_id", -1);

        // For demo, create sample users
        // In production, you would fetch from API
        String[] emojis = {"👨", "👩", "🧑", "👦", "👧", "🧔", "👱", "🧑‍💼", "👩‍💻", "🧑‍🎨"};
        String[] names = {"Alex Chen", "Jamie Smith", "Taylor Brown", "Jordan Lee", "Casey Wong",
                         "Morgan Davis", "Riley Johnson", "Avery Martinez", "Quinn Garcia", "Skyler Kim"};
        String[] bios = {"Love outdoor activities 🌲", "Foodie & coffee enthusiast ☕",
                        "Sports fan 🏀", "Music lover 🎵", "Bookworm 📚",
                        "Tech enthusiast 💻", "Art & photography 📷", "Fitness addict 💪",
                        "Movie buff 🎬", "Adventure seeker ✈️"};

        for (int i = 0; i < names.length; i++) {
            // Skip current user
            if (i + 1 != currentUserId) {
                usersList.add(new ShareUser(i + 1, names[i], bios[i], emojis[i]));
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void sendToSelectedUsers() {
        List<ShareUser> selectedUsers = adapter.getSelectedUsers();

        if (selectedUsers.isEmpty()) {
            Toast.makeText(this, "Please select at least one person", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create event share message
        String shareMessage = "🎉 Hey! I want to invite you to this event:\n\n" +
                "📅 " + event.getTitle() + "\n" +
                "📍 " + event.getLocation() + "\n" +
                "🕐 " + event.getTime() + "\n" +
                "👥 " + event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " participants\n\n" +
                event.getDescription();

        // Send to all selected users
        // For now, we'll open chat with the first selected user with the pre-filled message
        // In production, you might want to send messages to all users via an API
        ShareUser firstUser = selectedUsers.get(0);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receiver_id", firstUser.getId());
        intent.putExtra("receiver_name", firstUser.getName());
        intent.putExtra("share_message", shareMessage);
        intent.putExtra("shared_event_id", eventId);
        startActivity(intent);

        Toast.makeText(this, "Sent to " + selectedUsers.size() + " " +
                (selectedUsers.size() == 1 ? "person" : "people"), Toast.LENGTH_SHORT).show();
        finish();
    }
}
