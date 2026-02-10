package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.InteractionsResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UserResponse;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI elements
    private TabLayout tabLayout;
    private TextView usernameText;
    private TextView bioText;
    private TextView eventsCountText;
    private TextView hostedCountText;
    private TextView friendsCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        // Initialize views
        usernameText = findViewById(R.id.username);
        bioText = findViewById(R.id.bio);
        eventsCountText = findViewById(R.id.events_count);
        hostedCountText = findViewById(R.id.hosted_count);
        friendsCountText = findViewById(R.id.friends_count);

        // Setup tabs
        tabLayout = findViewById(R.id.tab_layout);
        setupTabs();

        // Load user data
        loadUserProfile();
        loadUserStats();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return;

        // Load from local DB first for instant display
        AppDatabaseHelper dbHelper = AppDatabaseHelper.getInstance(this);
        User localUser = dbHelper.getUserById(userId);
        if (localUser != null) {
            displayUserData(localUser);
        }

        // Fetch fresh data from backend
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUser(userId).enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<UserResponse> call,
                                   @NonNull retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    UserResponse.UserData userData = response.body().getUser();
                    // Update local DB
                    User user = new User();
                    user.setId(userData.getId());
                    user.setEmail(userData.getEmail());
                    user.setBio(userData.getBio());
                    user.setInterestTags(userData.getInterestTags());
                    user.setUsername(userData.getUsername());
                    user.setDob(userData.getDob());
                    user.setAddress(userData.getAddress());
                    user.setCaption(userData.getCaption());
                    dbHelper.insertUser(user);
                    displayUserData(user);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<UserResponse> call, @NonNull Throwable t) {
                // Local data already displayed
            }
        });
    }

    private void displayUserData(User user) {
        String displayName = user.getUsername();
        if (displayName == null || displayName.isEmpty()) {
            // Fallback to email prefix
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                displayName = email.substring(0, email.indexOf("@"));
            } else {
                displayName = "User";
            }
        }
        usernameText.setText(displayName);

        String caption = user.getCaption();
        if (caption != null && !caption.isEmpty()) {
            bioText.setText(caption);
        } else if (user.getBio() != null && !user.getBio().isEmpty()) {
            bioText.setText(user.getBio());
        } else {
            bioText.setText("");
        }
    }

    private void loadUserStats() {
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return;

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserInteractions(userId).enqueue(new retrofit2.Callback<InteractionsResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<InteractionsResponse> call,
                                   @NonNull retrofit2.Response<InteractionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<InteractionsResponse.Interaction> interactions =
                            response.body().getInteractions();
                    int joined = 0;
                    int created = 0;
                    for (InteractionsResponse.Interaction interaction : interactions) {
                        if ("joined".equals(interaction.getInteractionType())) {
                            joined++;
                        } else if ("created".equals(interaction.getInteractionType())) {
                            created++;
                        }
                    }
                    eventsCountText.setText(String.valueOf(joined));
                    hostedCountText.setText(String.valueOf(created));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<InteractionsResponse> call, @NonNull Throwable t) {
                // Keep defaults
            }
        });
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // Show Events Photos
                    Toast.makeText(ProfileActivity.this,
                            "Events Photos - Coming Soon!",
                            Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    // Show placeholder section
                    Toast.makeText(ProfileActivity.this,
                            "This section is not implemented yet",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Set Profile as selected by default
        bottomNav.setSelectedItemId(R.id.nav_profile);

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
                // Already on Profile screen
                return true;
            }

            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_sign_out) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", -1)
                .putBoolean("onboarding_completed", false)
                .putBoolean("profile_setup_completed", false)
                .apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
