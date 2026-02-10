package com.beyondbinary.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.CreateEventResponse;
import com.beyondbinary.app.api.CreateInteractionResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    // Form inputs
    private TextInputEditText inputTitle;
    private AutoCompleteTextView inputEventType;
    private TextInputEditText inputLocation;
    private TextInputEditText inputDescription;
    private TextInputEditText inputDate;
    private TextInputEditText inputStartTime;
    private TextInputEditText inputEndTime;
    private TextInputEditText inputMaxParticipants;
    private MaterialButton btnCreateEvent;

    // Utils
    private Geocoder geocoder;
    private ExecutorService executorService;

    // Date and time storage
    private Calendar selectedDate = Calendar.getInstance();
    private int startHour = 18, startMinute = 0;  // Default 6:00 PM
    private int endHour = 20, endMinute = 0;      // Default 8:00 PM

    // Event types for dropdown
    private final String[] eventTypes = {
            "Soccer", "Basketball", "Tennis", "Ping Pong", "Volleyball",
            "Running", "Yoga", "Gym", "Hiking", "Cycling",
            "Coffee", "Dinner", "Lunch", "BBQ", "Movie",
            "Book Club", "Board Games", "Party", "Concert",
            "Beach", "Painting", "Photography", "Museum",
            "Language Exchange", "Coding", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize geocoder and executor
        geocoder = new Geocoder(this, Locale.getDefault());
        executorService = Executors.newSingleThreadExecutor();

        // Initialize form inputs
        inputTitle = findViewById(R.id.input_title);
        inputEventType = findViewById(R.id.input_event_type);
        inputLocation = findViewById(R.id.input_location);
        inputDescription = findViewById(R.id.input_description);
        inputDate = findViewById(R.id.input_date);
        inputStartTime = findViewById(R.id.input_start_time);
        inputEndTime = findViewById(R.id.input_end_time);
        inputMaxParticipants = findViewById(R.id.input_max_participants);
        btnCreateEvent = findViewById(R.id.btn_create_event);

        // Setup event type dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                eventTypes
        );
        inputEventType.setAdapter(adapter);

        // Setup date picker
        inputDate.setOnClickListener(v -> showDatePicker());

        // Setup time pickers
        inputStartTime.setOnClickListener(v -> showStartTimePicker());
        inputEndTime.setOnClickListener(v -> showEndTimePicker());

        // Setup create button
        btnCreateEvent.setOnClickListener(v -> createEvent());

        // Setup bottom navigation
        setupBottomNavigation();

        // Pre-fill form from chatbot suggestions if available
        preFillFormFromIntent();
    }

    private void preFillFormFromIntent() {
        Intent intent = getIntent();
        if (intent == null) return;

        // Pre-fill event type
        String eventType = intent.getStringExtra("EVENT_TYPE");
        if (eventType != null && !eventType.isEmpty()) {
            inputEventType.setText(eventType, false); // false = don't filter
            inputEventType.setSelection(eventType.length()); // Move cursor to end
        }

        // Pre-fill title (if provided)
        String title = intent.getStringExtra("EVENT_TITLE");
        if (title != null && !title.isEmpty()) {
            inputTitle.setText(title);
        }

        // Pre-fill max participants
        int maxParticipants = intent.getIntExtra("MAX_PARTICIPANTS", 0);
        if (maxParticipants > 0) {
            inputMaxParticipants.setText(String.valueOf(maxParticipants));
        }

        // Pre-fill description hint
        String description = intent.getStringExtra("EVENT_DESCRIPTION");
        if (description != null && !description.isEmpty()) {
            inputDescription.setText(description);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set Add Event as selected by default
        bottomNav.setSelectedItemId(R.id.nav_add_event);

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

            } else if (itemId == R.id.nav_add_event) {
                // Already on Add Event screen
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Format and display date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
                    inputDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                year, month, day
        );

        // Don't allow selecting past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    startHour = hourOfDay;
                    startMinute = minute;

                    // Format and display time
                    String time = formatTime(hourOfDay, minute);
                    inputStartTime.setText(time);
                },
                startHour, startMinute, false  // false = 12-hour format
        );
        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    endHour = hourOfDay;
                    endMinute = minute;

                    // Format and display time
                    String time = formatTime(hourOfDay, minute);
                    inputEndTime.setText(time);
                },
                endHour, endMinute, false  // false = 12-hour format
        );
        timePickerDialog.show();
    }

    private String formatTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return timeFormat.format(calendar.getTime());
    }

    private void createEvent() {
        // Get form values
        String title = getTextOrEmpty(inputTitle);
        String eventType = getTextOrEmpty(inputEventType);
        String location = getTextOrEmpty(inputLocation);
        String description = getTextOrEmpty(inputDescription);
        String date = getTextOrEmpty(inputDate);
        String startTime = getTextOrEmpty(inputStartTime);
        String endTime = getTextOrEmpty(inputEndTime);
        String maxParticipantsStr = getTextOrEmpty(inputMaxParticipants);

        // Validate required fields
        if (title.isEmpty()) {
            inputTitle.setError("Title is required");
            inputTitle.requestFocus();
            return;
        }

        if (eventType.isEmpty()) {
            inputEventType.setError("Event type is required");
            inputEventType.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            inputLocation.setError("Location is required");
            inputLocation.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            inputDescription.setError("Description is required");
            inputDescription.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            inputDate.setError("Date is required");
            inputDate.requestFocus();
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime.isEmpty()) {
            inputStartTime.setError("Start time is required");
            inputStartTime.requestFocus();
            Toast.makeText(this, "Please select a start time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime.isEmpty()) {
            inputEndTime.setError("End time is required");
            inputEndTime.requestFocus();
            Toast.makeText(this, "Please select an end time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (maxParticipantsStr.isEmpty()) {
            inputMaxParticipants.setError("Max participants is required");
            inputMaxParticipants.requestFocus();
            return;
        }

        // Parse max participants
        int maxParticipants;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid participant number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (maxParticipants < 1) {
            inputMaxParticipants.setError("Must be at least 1");
            inputMaxParticipants.requestFocus();
            return;
        }

        // Validate end time is after start time
        if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_LONG).show();
            return;
        }

        // Combine date and time into formatted string
        String timeString = date + " • " + startTime + " - " + endTime;

        // Disable button while processing
        btnCreateEvent.setEnabled(false);
        btnCreateEvent.setText("Creating...");

        // Current participants defaults to 1
        int currentParticipants = 1;

        // Geocode location to get coordinates
        geocodeAndCreateEvent(title, eventType, location, description, timeString,
                currentParticipants, maxParticipants);
    }

    private void geocodeAndCreateEvent(String title, String eventType, String location,
                                       String description, String time,
                                       int currentParticipants, int maxParticipants) {

        Toast.makeText(this, "Finding location...", Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                // Geocode the location
                List<Address> addresses = geocoder.getFromLocationName(location, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();

                    runOnUiThread(() -> {
                        // Send to backend API
                        postEventToBackend(title, eventType, location, description, time,
                                currentParticipants, maxParticipants, latitude, longitude);
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this,
                                "Location not found. Please try a different location.",
                                Toast.LENGTH_LONG).show();
                        resetCreateButton();
                    });
                }

            } catch (IOException e) {
                Log.e(TAG, "Geocoding error: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Failed to find location. Check your internet connection.",
                            Toast.LENGTH_LONG).show();
                    resetCreateButton();
                });
            }
        });
    }

    private void postEventToBackend(String title, String eventType, String location,
                                    String description, String time,
                                    int currentParticipants, int maxParticipants,
                                    double latitude, double longitude) {

        // Get user ID
        android.content.SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Create Event object using constructor
        Event event = new Event(title, location, description, time,
                currentParticipants, maxParticipants, eventType);

        // Set coordinates and creator
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        if (userId != -1) {
            event.setCreatorUserId(userId);
        }

        // Call API
        ApiService apiService = RetrofitClient.getApiService();
        Call<CreateEventResponse> call = apiService.createEvent(event);

        call.enqueue(new Callback<CreateEventResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateEventResponse> call,
                                   @NonNull Response<CreateEventResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateEventResponse createResponse = response.body();
                    Log.d(TAG, "Event created: " + createResponse.getMessage());

                    // Track "created" interaction
                    if (userId != -1) {
                        Map<String, Object> interactionBody = new HashMap<>();
                        interactionBody.put("user_id", userId);
                        interactionBody.put("event_id", createResponse.getEventId());
                        interactionBody.put("interaction_type", "created");

                        apiService.createInteraction(interactionBody).enqueue(new Callback<CreateInteractionResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<CreateInteractionResponse> c,
                                                   @NonNull Response<CreateInteractionResponse> r) {
                                Log.d(TAG, "Created interaction tracked");
                            }

                            @Override
                            public void onFailure(@NonNull Call<CreateInteractionResponse> c, @NonNull Throwable t) {
                                Log.e(TAG, "Failed to track created interaction", t);
                            }
                        });
                    }

                    Toast.makeText(AddEventActivity.this,
                            "Event created successfully!",
                            Toast.LENGTH_LONG).show();

                    finish();

                } else {
                    Log.e(TAG, "Failed to create event: " + response.code());
                    Toast.makeText(AddEventActivity.this,
                            "Failed to create event: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    resetCreateButton();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateEventResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error creating event: " + t.getMessage());
                Toast.makeText(AddEventActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                resetCreateButton();
            }
        });
    }

    private void resetCreateButton() {
        btnCreateEvent.setEnabled(true);
        btnCreateEvent.setText("Create Event");
    }

    private String getTextOrEmpty(TextInputEditText editText) {
        if (editText.getText() == null) return "";
        return editText.getText().toString().trim();
    }

    private String getTextOrEmpty(AutoCompleteTextView textView) {
        if (textView.getText() == null) return "";
        return textView.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
