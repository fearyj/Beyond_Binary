package com.beyondbinary.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.beyondbinary.app.api.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.beyondbinary.app.api.EventsResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int SEARCH_RADIUS_KM = 50; // Search radius in kilometers

    private GoogleMap mMap;
    private EventDatabase eventDatabase;
    private Geocoder geocoder;
    private ExecutorService executorService;
    private Map<Marker, Event> markerEventMap;

    // Target event to focus on when opening map
    private int targetEventId = -1;
    private Marker targetMarker = null;

    // UI elements
    private EditText searchLocationInput;
    private Button searchButton;
    private Button showAllButton;

    // Search state
    private LatLng searchedLocation;
    private boolean isFiltered = false;

    // Emoji mapping for specific event types
    private final Map<String, String> eventTypeEmojis = new HashMap<String, String>() {{
        // Sports
        put("Soccer", "⚽");
        put("Basketball", "🏀");
        put("Tennis", "🎾");
        put("Ping Pong", "🏓");
        put("Volleyball", "🏐");
        put("Baseball", "⚾");
        put("Football", "🏈");
        put("Badminton", "🏸");
        put("Swimming", "🏊");
        put("Cycling", "🚴");
        put("Running", "🏃");
        put("Yoga", "🧘");
        put("Gym", "🏋️");
        put("Boxing", "🥊");
        put("Martial Arts", "🥋");
        put("Skateboarding", "🛹");
        put("Surfing", "🏄");
        put("Golf", "⛳");

        // Outdoor Activities
        put("Hiking", "🥾");
        put("Camping", "⛺");
        put("Rock Climbing", "🧗");
        put("Fishing", "🎣");
        put("Skiing", "⛷️");
        put("Snowboarding", "🏂");
        put("Beach", "🏖️");
        put("Picnic", "🧺");
        put("Bird Watching", "🦜");

        // Social & Entertainment
        put("Party", "🎉");
        put("Coffee", "☕");
        put("Movie", "🎬");
        put("Concert", "🎵");
        put("Karaoke", "🎤");
        put("Dancing", "💃");
        put("Board Games", "🎲");
        put("Video Games", "🎮");
        put("Trivia Night", "🧠");
        put("Meetup", "👥");

        // Food & Dining
        put("Dinner", "🍽️");
        put("Lunch", "🍱");
        put("Breakfast", "🍳");
        put("BBQ", "🍖");
        put("Pizza", "🍕");
        put("Sushi", "🍣");
        put("Dessert", "🍰");
        put("Wine Tasting", "🍷");
        put("Beer Tasting", "🍺");
        put("Cooking Class", "👨‍🍳");

        // Arts & Culture
        put("Painting", "🎨");
        put("Photography", "📷");
        put("Museum", "🏛️");
        put("Theater", "🎭");
        put("Music", "🎵");
        put("Crafts", "✂️");
        put("Pottery", "🏺");
        put("Drawing", "✏️");
        put("Dance Class", "💃");

        // Learning & Reading
        put("Book Club", "📚");
        put("Study Group", "📖");
        put("Language Exchange", "🗣️");
        put("Workshop", "🔧");
        put("Lecture", "🎓");
        put("Writing", "✍️");

        // Nature & Animals
        put("Dog Walking", "🐕");
        put("Pet Meetup", "🐾");
        put("Gardening", "🌱");
        put("Park Visit", "🌳");

        // Tech & Professional
        put("Coding", "💻");
        put("Networking", "🤝");
        put("Startup", "🚀");

        // Default fallback
        put("Other", "📍");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Check if we should focus on a specific event
        targetEventId = getIntent().getIntExtra("EVENT_ID", -1);

        // Initialize database and geocoder
        eventDatabase = EventDatabase.getInstance(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        executorService = Executors.newSingleThreadExecutor();
        markerEventMap = new HashMap<>();

        // Note: Sample data now loaded from backend API, not local database
        // SampleDataHelper.populateSampleEvents(this); // Commented out - using backend API

        // Initialize UI elements
        searchLocationInput = findViewById(R.id.search_location);
        searchButton = findViewById(R.id.search_button);
        showAllButton = findViewById(R.id.show_all_button);

        // Set up search button click listener
        searchButton.setOnClickListener(v -> searchLocation());

        // Set up show all button click listener
        showAllButton.setOnClickListener(v -> showAllEvents());

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up bottom navigation
        setupBottomNavigation();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Apply custom map style
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            );
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't find map style. Error: " + e.getMessage());
        }

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Set custom info window adapter
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set up info window click listener to view event details
        mMap.setOnInfoWindowClickListener(marker -> {
            Event event = markerEventMap.get(marker);
            if (event != null) {
                Intent intent = new Intent(MapsActivity.this, EventDetailActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                startActivity(intent);
            }
        });

        // Set default camera position to Singapore
        LatLng defaultLocation = new LatLng(1.3521, 103.8198); // Singapore
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        // Set up FAB for current location
        setupCurrentLocationButton();

        // Load and display events on map
        loadEventsAndDisplayOnMap();
    }

    /**
     * Main function to load events from backend API and display them on the map with emoji markers
     */
    private void loadEventsAndDisplayOnMap() {
        // Show loading message
        Toast.makeText(this, "Loading events from server...", Toast.LENGTH_SHORT).show();

        // Fetch events from backend API
        ApiService apiService = RetrofitClient.getApiService();
        Call<EventsResponse> call = apiService.getAllEvents();

        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call,
                                   @NonNull Response<EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventsResponse eventsResponse = response.body();
                    List<Event> events = eventsResponse.getEvents();

                    Log.d(TAG, "Loaded " + events.size() + " events from API");
                    Toast.makeText(MapsActivity.this,
                            "Loaded " + events.size() + " events",
                            Toast.LENGTH_SHORT).show();

                    // Process each event and add to map
                    for (Event event : events) {
                        processAndAddEventMarker(event);
                    }
                } else {
                    Log.e(TAG, "API response unsuccessful: " + response.code());
                    Toast.makeText(MapsActivity.this,
                            "Failed to load events: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading events from API: " + t.getMessage());
                Toast.makeText(MapsActivity.this,
                        "Error connecting to server. Make sure backend is running.",
                        Toast.LENGTH_LONG).show();

                // Optionally: Fall back to local database
                // loadEventsFromLocalDatabase();
            }
        });
    }

    /**
     * Process an event and add its marker to the map
     */
    private void processAndAddEventMarker(Event event) {
        // Check if event already has coordinates
        if (event.getLatitude() != null && event.getLongitude() != null) {
            addEmojiMarkerToMap(event);
        } else {
            // Geocode the address to get coordinates
            geocodeEventLocation(event);
        }
    }

    /**
     * Geocode an event's address to get latitude and longitude
     */
    private void geocodeEventLocation(Event event) {
        executorService.execute(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocationName(event.getLocation(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();

                    // Update event with coordinates
                    event.setLatitude(latitude);
                    event.setLongitude(longitude);

                    // Save coordinates to database for future use
                    eventDatabase.eventDao().update(event);

                    // Add marker on main thread
                    runOnUiThread(() -> addEmojiMarkerToMap(event));
                } else {
                    Log.w(TAG, "Geocoding failed for location: " + event.getLocation());
                }

            } catch (IOException e) {
                Log.e(TAG, "Geocoding error: " + e.getMessage());
            }
        });
    }

    /**
     * Add an emoji marker to the map for the given event
     */
    private void addEmojiMarkerToMap(Event event) {
        if (event.getLatitude() == null || event.getLongitude() == null) {
            return;
        }

        LatLng position = new LatLng(event.getLatitude(), event.getLongitude());

        // Get emoji for event type
        String emoji = eventTypeEmojis.getOrDefault(event.getEventType(), "📍");

        // Create custom bitmap with emoji
        BitmapDescriptor icon = createEmojiMarkerIcon(emoji);

        // Add marker to map
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(event.getTitle())
                .icon(icon));

        // Store event reference for info window
        if (marker != null) {
            markerEventMap.put(marker, event);

            // Check if this is the target event we should focus on
            if (targetEventId != -1 && event.getId() == targetEventId) {
                targetMarker = marker;
                // Zoom to this marker and show info window
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f));
                marker.showInfoWindow();
            }
        }
    }

    /**
     * Create a custom marker icon with emoji
     */
    private BitmapDescriptor createEmojiMarkerIcon(String emoji) {
        int size = 120;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw white circle background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 5, backgroundPaint);

        // Draw border
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.parseColor("#4285F4")); // Google blue
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6);
        borderPaint.setAntiAlias(true);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 5, borderPaint);

        // Draw emoji
        Paint textPaint = new Paint();
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setAntiAlias(true);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        float textOffset = (textHeight / 2) - fontMetrics.bottom;

        canvas.drawText(emoji, size / 2f, (size / 2f) + textOffset, textPaint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Custom Info Window Adapter to display event details when marker is clicked
     */
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            return null; // Use default frame
        }

        @Override
        public View getInfoContents(@NonNull Marker marker) {
            View view = LayoutInflater.from(MapsActivity.this)
                    .inflate(R.layout.custom_info_window, null);

            Event event = markerEventMap.get(marker);
            if (event == null) return view;

            // Get the emoji for this event type
            String eventType = event.getEventType();
            String emoji = eventTypeEmojis.getOrDefault(eventType, "📍");

            TextView emojiView = view.findViewById(R.id.info_emoji);
            TextView titleView = view.findViewById(R.id.info_title);
            TextView locationView = view.findViewById(R.id.info_location);
            TextView timeView = view.findViewById(R.id.info_time);
            TextView participantsView = view.findViewById(R.id.info_participants);
            TextView descriptionView = view.findViewById(R.id.info_description);

            emojiView.setText(emoji);
            titleView.setText(event.getTitle());
            locationView.setText(event.getLocation());
            timeView.setText(event.getTime());
            participantsView.setText(event.getCurrentParticipants() + "/" +
                    event.getMaxParticipants() + " participants");
            descriptionView.setText(event.getDescription());

            return view;
        }
    }

    /**
     * Search for a location and filter events near it
     */
    private void searchLocation() {
        String locationQuery = searchLocationInput.getText().toString().trim();

        if (locationQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading message
        Toast.makeText(this, "Searching for: " + locationQuery, Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                // Geocode the search query
                List<Address> addresses = geocoder.getFromLocationName(locationQuery, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    searchedLocation = new LatLng(address.getLatitude(), address.getLongitude());

                    runOnUiThread(() -> {
                        // Move camera to searched location
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 13));

                        // Filter and show only nearby events
                        filterEventsByLocation(searchedLocation);

                        // Show the "Show All Events" button
                        showAllButton.setVisibility(View.VISIBLE);
                        isFiltered = true;

                        Toast.makeText(this, "Found events near " + address.getLocality(),
                                Toast.LENGTH_SHORT).show();
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Location not found. Try a different search.",
                                Toast.LENGTH_LONG).show();
                    });
                }

            } catch (IOException e) {
                Log.e(TAG, "Geocoding error: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Search failed. Please check your internet connection.",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Filter events to show only those within the search radius using API
     */
    private void filterEventsByLocation(LatLng searchLocation) {
        // Clear existing markers
        mMap.clear();
        markerEventMap.clear();

        // Fetch nearby events from API
        ApiService apiService = RetrofitClient.getApiService();
        Call<EventsResponse> call = apiService.getNearbyEvents(
                searchLocation.latitude,
                searchLocation.longitude,
                SEARCH_RADIUS_KM
        );

        call.enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsResponse> call,
                                   @NonNull Response<EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventsResponse eventsResponse = response.body();
                    List<Event> events = eventsResponse.getEvents();

                    Toast.makeText(MapsActivity.this,
                            "Showing " + events.size() + " events within " + SEARCH_RADIUS_KM + " km",
                            Toast.LENGTH_SHORT).show();

                    // Add filtered events to map
                    for (Event event : events) {
                        addEmojiMarkerToMap(event);
                    }
                } else {
                    Log.e(TAG, "Failed to filter events: " + response.code());
                    Toast.makeText(MapsActivity.this,
                            "Failed to filter events",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error filtering events: " + t.getMessage());
                Toast.makeText(MapsActivity.this,
                        "Error filtering events",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show all events (reset filter)
     */
    private void showAllEvents() {
        isFiltered = false;
        searchedLocation = null;
        showAllButton.setVisibility(View.GONE);
        searchLocationInput.setText("");

        // Clear map and reload all events
        mMap.clear();
        markerEventMap.clear();
        loadEventsAndDisplayOnMap();

        // Reset camera to default position (Singapore)
        LatLng defaultLocation = new LatLng(1.3521, 103.8198);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        Toast.makeText(this, "Showing all events", Toast.LENGTH_SHORT).show();
    }

    /**
     * Setup current location button (FAB)
     */
    private void setupCurrentLocationButton() {
        findViewById(R.id.fab_current_location).setOnClickListener(v -> {
            // Move camera back to Singapore center
            LatLng defaultLocation = new LatLng(1.3521, 103.8198);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
            Toast.makeText(this, "Centered on Singapore", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Setup bottom navigation bar
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set Map as selected by default
        bottomNav.setSelectedItemId(R.id.nav_map);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Navigate to Home (MainActivity with video feed)
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_chatbot) {
                // Navigate to AI Chatbot
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("OPEN_CHATBOT", true);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_add_event) {
                // Navigate to Add Event Activity
                Intent intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_map) {
                // Already on Map screen
                Toast.makeText(this, "Map View", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.nav_profile) {
                // Navigate to Profile Activity
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    /**
     * Calculate distance between two locations in kilometers
     */
    private float calculateDistance(LatLng loc1, LatLng loc2) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(
                loc1.latitude, loc1.longitude,
                loc2.latitude, loc2.longitude,
                results
        );
        return results[0] / 1000; // Convert meters to kilometers
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload events when returning from AddEventActivity
        if (mMap != null && !isFiltered) {
            mMap.clear();
            markerEventMap.clear();
            loadEventsAndDisplayOnMap();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
