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
import com.beyondbinary.app.api.DeleteEventResponse;
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
    private View attendanceButtonsContainer;
    private Button attendedButton;
    private Button notAttendedButton;
    private Button leaveEventButton;

    private int eventId;
    private Event event;
    private boolean userHasJoined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get event ID from intent
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        userHasJoined = getIntent().getBooleanExtra("USER_HAS_JOINED", false);

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
        attendanceButtonsContainer = findViewById(R.id.attendance_buttons_container);
        attendedButton = findViewById(R.id.btn_attended);
        notAttendedButton = findViewById(R.id.btn_not_attended);
        leaveEventButton = findViewById(R.id.btn_leave_event);

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
        attendedButton.setOnClickListener(v -> markAttendance(true));
        notAttendedButton.setOnClickListener(v -> markAttendance(false));
        leaveEventButton.setOnClickListener(v -> leaveEvent());

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

        // Check if user is the creator
        android.content.SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        boolean isCreator = (userId != -1 && event.getCreatorUserId() != null && event.getCreatorUserId() == userId);

        // Show appropriate buttons based on join status
        if (userHasJoined) {
            // User has joined - show leave/cancel button
            joinButton.setVisibility(View.GONE);
            leaveEventButton.setVisibility(View.VISIBLE);

            // Only show attendance buttons if opened from My Events (with USER_HAS_JOINED extra)
            boolean openedFromMyEvents = getIntent().getBooleanExtra("USER_HAS_JOINED", false);
            if (openedFromMyEvents) {
                attendanceButtonsContainer.setVisibility(View.VISIBLE);
            } else {
                attendanceButtonsContainer.setVisibility(View.GONE);
            }

            // Change button text if user is creator
            if (isCreator) {
                leaveEventButton.setText("Cancel Event");
            } else {
                leaveEventButton.setText("Leave Event");
            }
        } else {
            // User hasn't joined - show join button
            joinButton.setVisibility(View.VISIBLE);
            attendanceButtonsContainer.setVisibility(View.GONE);
            leaveEventButton.setVisibility(View.GONE);

            // Check if event is full
            if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
                joinButton.setEnabled(false);
                joinButton.setText("Event Full");
            }
        }
    }

    private void joinEvent() {
        if (event.getCurrentParticipants() < event.getMaxParticipants()) {
            event.setCurrentParticipants(event.getCurrentParticipants() + 1);
            participantsText.setText("👥 " + event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " participants");

            Toast.makeText(this, "Joined event successfully!", Toast.LENGTH_SHORT).show();

            // Update UI to show leave button and attendance options
            userHasJoined = true;
            displayEventDetails();

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

    private void markAttendance(boolean attended) {
        android.content.SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            ApiService apiService = RetrofitClient.getApiService();
            Map<String, Object> body = new HashMap<>();
            body.put("user_id", userId);
            body.put("event_id", eventId);
            body.put("interaction_type", attended ? "attended" : "not_attended");

            apiService.createInteraction(body).enqueue(new Callback<CreateInteractionResponse>() {
                @Override
                public void onResponse(Call<CreateInteractionResponse> call, Response<CreateInteractionResponse> response) {
                    String message = attended ? "Marked as Attended ✓" : "Marked as Not Attended";
                    Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Update button states
                    if (attended) {
                        attendedButton.setEnabled(false);
                        notAttendedButton.setEnabled(true);
                        attendedButton.setAlpha(0.5f);
                        notAttendedButton.setAlpha(1.0f);
                    } else {
                        attendedButton.setEnabled(true);
                        notAttendedButton.setEnabled(false);
                        attendedButton.setAlpha(1.0f);
                        notAttendedButton.setAlpha(0.5f);
                    }
                }
                @Override
                public void onFailure(Call<CreateInteractionResponse> call, Throwable t) {
                    Toast.makeText(EventDetailActivity.this, "Failed to update attendance", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void leaveEvent() {
        android.content.SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        boolean isCreator = (userId != -1 && event.getCreatorUserId() != null && event.getCreatorUserId() == userId);

        ApiService apiService = RetrofitClient.getApiService();

        if (isCreator) {
            // Creator is cancelling the event - delete it
            apiService.deleteEvent(eventId).enqueue(new Callback<DeleteEventResponse>() {
                @Override
                public void onResponse(Call<DeleteEventResponse> call, Response<DeleteEventResponse> response) {
                    Toast.makeText(EventDetailActivity.this, "Event cancelled successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Signal that event was removed
                    // Navigate back to My Events
                    Intent intent = new Intent(EventDetailActivity.this, MainActivity.class);
                    intent.putExtra("OPEN_MY_EVENTS", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onFailure(Call<DeleteEventResponse> call, Throwable t) {
                    Toast.makeText(EventDetailActivity.this, "Failed to cancel event", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User is leaving the event
            if (event.getCurrentParticipants() > 0) {
                event.setCurrentParticipants(event.getCurrentParticipants() - 1);

                // Update backend
                apiService.updateEvent(eventId, event).enqueue(new Callback<UpdateEventResponse>() {
                    @Override
                    public void onResponse(Call<UpdateEventResponse> call, Response<UpdateEventResponse> response) {}
                    @Override
                    public void onFailure(Call<UpdateEventResponse> call, Throwable t) {}
                });

                // Track "left" interaction - wait for completion before finishing
                if (userId != -1) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("user_id", userId);
                    body.put("event_id", eventId);
                    body.put("interaction_type", "left");

                    apiService.createInteraction(body).enqueue(new Callback<CreateInteractionResponse>() {
                        @Override
                        public void onResponse(Call<CreateInteractionResponse> call, Response<CreateInteractionResponse> response) {
                            Toast.makeText(EventDetailActivity.this, "Left event successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK); // Signal that event was removed
                            // Navigate back to My Events
                            Intent intent = new Intent(EventDetailActivity.this, MainActivity.class);
                            intent.putExtra("OPEN_MY_EVENTS", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onFailure(Call<CreateInteractionResponse> call, Throwable t) {
                            Toast.makeText(EventDetailActivity.this, "Failed to leave event", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Left event successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    // Navigate back to My Events
                    Intent intent = new Intent(EventDetailActivity.this, MainActivity.class);
                    intent.putExtra("OPEN_MY_EVENTS", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }
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
