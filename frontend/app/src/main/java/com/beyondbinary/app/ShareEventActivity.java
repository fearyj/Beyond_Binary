package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.SendInviteResponse;
import com.beyondbinary.app.messaging.ChatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        event = (Event) getIntent().getSerializableExtra("EVENT");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle("Share Event");

        recyclerView = findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShareUserAdapter(usersList, this);
        recyclerView.setAdapter(adapter);

        sendButton = findViewById(R.id.btn_send);
        sendButton.setOnClickListener(v -> sendToSelectedUsers());

        loadUsers();
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        sendButton.setEnabled(selectedCount > 0);
        if (selectedCount > 0) {
            sendButton.setText("Send to " + selectedCount + " " + (selectedCount == 1 ? "person" : "people"));
        } else {
            sendButton.setText("Send");
        }
    }

    private void loadUsers() {
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int currentUserId = prefs.getInt("user_id", -1);

        // Demo users — in production, fetch from API
        String[] emojis = {"👨", "👩", "🧑", "👦", "👧", "🧔", "👱", "🧑‍💼", "👩‍💻", "🧑‍🎨"};
        String[] names = {"Alex Chen", "Jamie Smith", "Taylor Brown", "Jordan Lee", "Casey Wong",
                         "Morgan Davis", "Riley Johnson", "Avery Martinez", "Quinn Garcia", "Skyler Kim"};
        String[] bios = {"Love outdoor activities", "Foodie & coffee enthusiast",
                        "Sports fan", "Music lover", "Bookworm",
                        "Tech enthusiast", "Art & photography", "Fitness addict",
                        "Movie buff", "Adventure seeker"};

        for (int i = 0; i < names.length; i++) {
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

        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int currentUserId = prefs.getInt("user_id", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
            return;
        }

        sendButton.setEnabled(false);
        sendButton.setText("Sending...");

        ShareUser firstUser = selectedUsers.get(0);
        int totalToSend = selectedUsers.size();
        final int[] sentCount = {0};

        for (ShareUser user : selectedUsers) {
            ApiService apiService = RetrofitClient.getApiService();
            Map<String, Object> body = new HashMap<>();
            body.put("sender_id", currentUserId);
            body.put("receiver_id", user.getId());
            body.put("event_id", eventId);

            apiService.sendEventInvite(body).enqueue(new Callback<SendInviteResponse>() {
                @Override
                public void onResponse(Call<SendInviteResponse> call, Response<SendInviteResponse> response) {
                    sentCount[0]++;
                    if (sentCount[0] == totalToSend) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(ShareEventActivity.this,
                                    "Failed to send invite", Toast.LENGTH_SHORT).show();
                            sendButton.setEnabled(true);
                            onSelectionChanged(selectedUsers.size());
                            return;
                        }

                        Intent intent = new Intent(ShareEventActivity.this, ChatActivity.class);
                        intent.putExtra("CONTACT_NAME", firstUser.getName());
                        intent.putExtra("PROFILE_EMOJI", firstUser.getProfileEmoji());
                        intent.putExtra("receiver_id", firstUser.getId());
                        intent.putExtra("shared_event_id", eventId);
                        if (event != null) {
                            intent.putExtra("shared_event_title", event.getTitle());
                            intent.putExtra("shared_event_time", event.getTime());
                            intent.putExtra("shared_event_location", event.getLocation());
                            intent.putExtra("shared_event_type", event.getEventType());
                            intent.putExtra("shared_event_current", event.getCurrentParticipants());
                            intent.putExtra("shared_event_max", event.getMaxParticipants());
                        }
                        startActivity(intent);

                        Toast.makeText(ShareEventActivity.this,
                                "Invited " + totalToSend + " " + (totalToSend == 1 ? "person" : "people"),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<SendInviteResponse> call, Throwable t) {
                    sentCount[0]++;
                    if (sentCount[0] == totalToSend) {
                        Toast.makeText(ShareEventActivity.this,
                                "Some invites may have failed", Toast.LENGTH_SHORT).show();
                        sendButton.setEnabled(true);
                        onSelectionChanged(selectedUsers.size());
                    }
                }
            });
        }
    }
}
