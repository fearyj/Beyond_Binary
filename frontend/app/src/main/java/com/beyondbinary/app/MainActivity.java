package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondbinary.app.chatbot.ChatbotFragment;
import com.beyondbinary.app.onboarding.OnboardingFragment;
import com.beyondbinary.app.registration.ProfileSetupFragment;
import com.beyondbinary.app.registration.RegistrationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private boolean onboardingDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        boolean openChatbot = getIntent().getBooleanExtra("OPEN_CHATBOT", false);
        boolean openMyEvents = getIntent().getBooleanExtra("OPEN_MY_EVENTS", false);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            onboardingDone = prefs.getBoolean(OnboardingFragment.KEY_ONBOARDING_COMPLETED, false);
            // Existing users who already onboarded before profile setup was added
            // should be treated as having completed profile setup
            boolean profileSetupDone = prefs.getBoolean(ProfileSetupFragment.KEY_PROFILE_SETUP_COMPLETED, false)
                    || onboardingDone;

            if (userId == -1) {
                // No user registered — show registration
                bottomNav.setVisibility(android.view.View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new RegistrationFragment())
                        .commit();
            } else if (!profileSetupDone) {
                // User registered but hasn't completed profile setup
                bottomNav.setVisibility(android.view.View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileSetupFragment())
                        .commit();
            } else if (!onboardingDone) {
                // User completed profile setup but hasn't completed onboarding
                bottomNav.setVisibility(android.view.View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new OnboardingFragment())
                        .commit();
            } else if (openMyEvents) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MyEventsFragment())
                        .commit();
            } else if (openChatbot) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChatbotFragment())
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        }

        setupBottomNavigation();

        if (onboardingDone && openMyEvents) {
            bottomNav.setSelectedItemId(R.id.nav_my_events);
        } else if (onboardingDone && openChatbot) {
            bottomNav.setSelectedItemId(R.id.nav_chatbot);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (onboardingDone) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_chatbot) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChatbotFragment())
                        .commit();
                return true;
            } else if (itemId == R.id.nav_my_events) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MyEventsFragment())
                        .commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        boolean openChatbot = getIntent().getBooleanExtra("OPEN_CHATBOT", false);
        boolean openMyEvents = getIntent().getBooleanExtra("OPEN_MY_EVENTS", false);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null && onboardingDone && !openChatbot && !openMyEvents) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}
