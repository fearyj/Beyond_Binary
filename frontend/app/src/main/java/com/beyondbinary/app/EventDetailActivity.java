package com.beyondbinary.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.CreateInteractionResponse;
import com.beyondbinary.app.api.EventResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UpdateEventResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView typeText;
    private TextView locationText;
    private TextView timeText;
    private TextView descriptionText;
    private TextView participantsText;
    private Button joinButton;
    private Button viewOnMapButton;

    private int eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get event ID from intent
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        titleText = findViewById(R.id.event_detail_title);
        typeText = findViewById(R.id.event_detail_type);
        locationText = findViewById(R.id.event_detail_location);
        timeText = findViewById(R.id.event_detail_time);
        descriptionText = findViewById(R.id.event_detail_description);
        participantsText = findViewById(R.id.event_detail_participants);
        joinButton = findViewById(R.id.btn_join_event);
        viewOnMapButton = findViewById(R.id.btn_view_on_map);

        // Load event details
        if (eventId != -1) {
            loadEventDetails();
        } else {
            Toast.makeText(this, "Invalid event", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup button listeners
        joinButton.setOnClickListener(v -> joinEvent());
        viewOnMapButton.setOnClickListener(v -> viewOnMap());

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadEventDetails() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<EventResponse> call = apiService.getEventById(eventId);

        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event = response.body().getEvent();
                    displayEventDetails();
                } else {
                    Toast.makeText(EventDetailActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEventDetails() {
        titleText.setText(event.getTitle());
        typeText.setText(event.getEventType());
        locationText.setText("📍 " + event.getLocation());
        timeText.setText("🕐 " + event.getTime());
        descriptionText.setText(event.getDescription());
        participantsText.setText("👥 " + event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " participants");

        // Check if event is full
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            joinButton.setEnabled(false);
            joinButton.setText("Event Full");
        }
    }

    private void joinEvent() {
        if (event.getCurrentParticipants() < event.getMaxParticipants()) {
            event.setCurrentParticipants(event.getCurrentParticipants() + 1);
            participantsText.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " participants");

            if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
                joinButton.setEnabled(false);
                joinButton.setText("Event Full");
            }

            Toast.makeText(this, "Joined event successfully!", Toast.LENGTH_SHORT).show();

            // Update participant count on backend
            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateEvent(eventId, event).enqueue(new Callback<UpdateEventResponse>() {
                @Override
                public void onResponse(Call<UpdateEventResponse> call, Response<UpdateEventResponse> response) {}
                @Override
                public void onFailure(Call<UpdateEventResponse> call, Throwable t) {}
            });

            // Track "joined" interaction
            android.content.SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            if (userId != -1) {
                Map<String, Object> body = new HashMap<>();
                body.put("user_id", userId);
                body.put("event_id", eventId);
                body.put("interaction_type", "joined");

                apiService.createInteraction(body).enqueue(new Callback<CreateInteractionResponse>() {
                    @Override
                    public void onResponse(Call<CreateInteractionResponse> call, Response<CreateInteractionResponse> response) {}
                    @Override
                    public void onFailure(Call<CreateInteractionResponse> call, Throwable t) {}
                });
            }
        }
    }

    private void viewOnMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Don't select any item by default since we're viewing event details
        bottomNav.setSelectedItemId(0);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_chatbot) {
                Toast.makeText(this, "AI Chatbot - Coming Soon!", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.nav_add_event) {
                Intent intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
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
