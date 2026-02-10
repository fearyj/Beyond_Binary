package com.beyondbinary.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.CreateInteractionResponse;
import com.beyondbinary.app.api.DeleteEventResponse;
import com.beyondbinary.app.api.EventResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UpdateEventResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView typeText;
    private TextView emojiText;
    private TextView locationText;
    private TextView dateText;
    private TextView timeText;
    private TextView descriptionText;
    private TextView participantsText;
    private View joinButton;
    private View inviteButton;
    private View viewOnMapButton;
    private View attendanceButtonsContainer;
    private View attendedButton;
    private View notAttendedButton;
    private View leaveEventButton;
    private View joinInviteRow;
    private ImageView shareButton;
    private ImageView backButton;

    private int eventId;
    private Event event;
    private boolean userHasJoined = false;

    // Emoji mapping for event types
    private static final java.util.Map<String, String> EVENT_EMOJIS = new HashMap<>();
    static {
        EVENT_EMOJIS.put("soccer", "\u26BD");
        EVENT_EMOJIS.put("basketball", "\uD83C\uDFC0");
        EVENT_EMOJIS.put("tennis", "\uD83C\uDFBE");
        EVENT_EMOJIS.put("ping pong", "\uD83C\uDFD3");
        EVENT_EMOJIS.put("volleyball", "\uD83C\uDFD0");
        EVENT_EMOJIS.put("running", "\uD83C\uDFC3");
        EVENT_EMOJIS.put("yoga", "\uD83E\uDDD8");
        EVENT_EMOJIS.put("gym", "\uD83C\uDFCB\uFE0F");
        EVENT_EMOJIS.put("hiking", "\u26F0\uFE0F");
        EVENT_EMOJIS.put("cycling", "\uD83D\uDEB4");
        EVENT_EMOJIS.put("coffee", "\u2615");
        EVENT_EMOJIS.put("dinner", "\uD83C\uDF7D\uFE0F");
        EVENT_EMOJIS.put("lunch", "\uD83C\uDF5C");
        EVENT_EMOJIS.put("bbq", "\uD83C\uDF56");
        EVENT_EMOJIS.put("movie", "\uD83C\uDFAC");
        EVENT_EMOJIS.put("book club", "\uD83D\uDCDA");
        EVENT_EMOJIS.put("board games", "\uD83C\uDFB2");
        EVENT_EMOJIS.put("party", "\uD83C\uDF89");
        EVENT_EMOJIS.put("concert", "\uD83C\uDFB5");
        EVENT_EMOJIS.put("beach", "\uD83C\uDFD6\uFE0F");
        EVENT_EMOJIS.put("painting", "\uD83C\uDFA8");
        EVENT_EMOJIS.put("photography", "\uD83D\uDCF7");
        EVENT_EMOJIS.put("museum", "\uD83C\uDFDB\uFE0F");
        EVENT_EMOJIS.put("language exchange", "\uD83D\uDDE3\uFE0F");
        EVENT_EMOJIS.put("coding", "\uD83D\uDCBB");
        EVENT_EMOJIS.put("picnic", "\uD83E\uDDFA");
        EVENT_EMOJIS.put("swimming", "\uD83C\uDFCA");
        EVENT_EMOJIS.put("badminton", "\uD83C\uDFF8");
        EVENT_EMOJIS.put("fishing", "\uD83C\uDFA3");
        EVENT_EMOJIS.put("karaoke", "\uD83C\uDFA4");
        EVENT_EMOJIS.put("bowling", "\uD83C\uDFB3");
        EVENT_EMOJIS.put("study", "\uD83D\uDCDD");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get event ID from intent
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        userHasJoined = getIntent().getBooleanExtra("USER_HAS_JOINED", false);

        // Initialize views
        backButton = findViewById(R.id.btn_back);
        shareButton = findViewById(R.id.btn_share);
        emojiText = findViewById(R.id.event_detail_emoji);
        titleText = findViewById(R.id.event_detail_title);
        typeText = findViewById(R.id.event_detail_type);
        locationText = findViewById(R.id.event_detail_location);
        dateText = findViewById(R.id.event_detail_date);
        timeText = findViewById(R.id.event_detail_time);
        descriptionText = findViewById(R.id.event_detail_description);
        participantsText = findViewById(R.id.event_detail_participants);
        joinButton = findViewById(R.id.btn_join_event);
        inviteButton = findViewById(R.id.btn_invite);
        viewOnMapButton = findViewById(R.id.btn_view_on_map);
        joinInviteRow = findViewById(R.id.join_invite_row);
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
        backButton.setOnClickListener(v -> finish());
        shareButton.setOnClickListener(v -> shareEvent());
        joinButton.setOnClickListener(v -> joinEvent());
        inviteButton.setOnClickListener(v -> shareEvent());
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
        descriptionText.setText(event.getDescription());

        // Set emoji based on event type
        String emoji = getEmojiForEvent(event.getEventType());
        emojiText.setText(emoji);

        // Parse and display date and time separately
        String timeString = event.getTime();
        if (timeString != null && timeString.contains(" • ")) {
            String[] parts = timeString.split(" • ", 2);
            dateText.setText(parts[0]);
            timeText.setText(parts.length > 1 ? parts[1] : "");
        } else {
            dateText.setText(timeString != null ? timeString : "");
            timeText.setText("");
        }

        // Location
        locationText.setText(event.getLocation());

        // Participants
        participantsText.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " Pax");

        // Check if user is the creator
        android.content.SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        boolean isCreator = (userId != -1 && event.getCreatorUserId() != null && event.getCreatorUserId() == userId);

        // Show appropriate buttons based on join status
        if (userHasJoined) {
            joinInviteRow.setVisibility(View.GONE);
            leaveEventButton.setVisibility(View.VISIBLE);

            boolean openedFromMyEvents = getIntent().getBooleanExtra("USER_HAS_JOINED", false);
            if (openedFromMyEvents) {
                attendanceButtonsContainer.setVisibility(View.VISIBLE);
            } else {
                attendanceButtonsContainer.setVisibility(View.GONE);
            }

            if (isCreator) {
                ((TextView) leaveEventButton).setText("Cancel Event");
            } else {
                ((TextView) leaveEventButton).setText("Leave Event");
            }
        } else {
            joinInviteRow.setVisibility(View.VISIBLE);
            attendanceButtonsContainer.setVisibility(View.GONE);
            leaveEventButton.setVisibility(View.GONE);

            if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
                joinButton.setEnabled(false);
                joinButton.setAlpha(0.5f);
                ((TextView) joinButton).setText("Full");
            }
        }
    }

    private String getEmojiForEvent(String eventType) {
        if (eventType == null) return "\uD83C\uDF1F";
        String lower = eventType.toLowerCase().trim();

        // Exact match
        if (EVENT_EMOJIS.containsKey(lower)) {
            return EVENT_EMOJIS.get(lower);
        }

        // Partial match
        for (java.util.Map.Entry<String, String> entry : EVENT_EMOJIS.entrySet()) {
            if (lower.contains(entry.getKey()) || entry.getKey().contains(lower)) {
                return entry.getValue();
            }
        }

        return "\uD83C\uDF1F"; // Default star emoji
    }

    private void joinEvent() {
        if (event.getCurrentParticipants() < event.getMaxParticipants()) {
            event.setCurrentParticipants(event.getCurrentParticipants() + 1);
            participantsText.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " Pax");

            Toast.makeText(this, "Joined event successfully!", Toast.LENGTH_SHORT).show();

            userHasJoined = true;
            displayEventDetails();

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateEvent(eventId, event).enqueue(new Callback<UpdateEventResponse>() {
                @Override
                public void onResponse(Call<UpdateEventResponse> call, Response<UpdateEventResponse> response) {}
                @Override
                public void onFailure(Call<UpdateEventResponse> call, Throwable t) {}
            });

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

    private void shareEvent() {
        if (event == null) {
            Toast.makeText(this, "Event details not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ShareEventActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        intent.putExtra("EVENT", event);
        startActivity(intent);
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
                    String message = attended ? "Marked as Attended" : "Marked as Not Attended";
                    Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();

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
            apiService.deleteEvent(eventId).enqueue(new Callback<DeleteEventResponse>() {
                @Override
                public void onResponse(Call<DeleteEventResponse> call, Response<DeleteEventResponse> response) {
                    Toast.makeText(EventDetailActivity.this, "Event cancelled successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
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
            if (event.getCurrentParticipants() > 0) {
                event.setCurrentParticipants(event.getCurrentParticipants() - 1);

                apiService.updateEvent(eventId, event).enqueue(new Callback<UpdateEventResponse>() {
                    @Override
                    public void onResponse(Call<UpdateEventResponse> call, Response<UpdateEventResponse> response) {}
                    @Override
                    public void onFailure(Call<UpdateEventResponse> call, Throwable t) {}
                });

                if (userId != -1) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("user_id", userId);
                    body.put("event_id", eventId);
                    body.put("interaction_type", "left");

                    apiService.createInteraction(body).enqueue(new Callback<CreateInteractionResponse>() {
                        @Override
                        public void onResponse(Call<CreateInteractionResponse> call, Response<CreateInteractionResponse> response) {
                            Toast.makeText(EventDetailActivity.this, "Left event successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
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
                finish();
                return true;

            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
