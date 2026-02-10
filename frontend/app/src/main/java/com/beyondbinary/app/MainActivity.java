package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondbinary.app.chatbot.ChatbotFragment;
import com.beyondbinary.app.onboarding.OnboardingFragment;
import com.beyondbinary.app.registration.RegistrationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private boolean onboardingDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        boolean openChatbot = getIntent().getBooleanExtra("OPEN_CHATBOT", false);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            onboardingDone = prefs.getBoolean(OnboardingFragment.KEY_ONBOARDING_COMPLETED, false);

            if (userId == -1) {
                // No user registered — show registration
                bottomNav.setVisibility(android.view.View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new RegistrationFragment())
                        .commit();
            } else if (!onboardingDone) {
                // User registered but hasn't completed onboarding
                bottomNav.setVisibility(android.view.View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new OnboardingFragment())
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

        if (onboardingDone && openChatbot) {
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

    @Override
    protected void onResume() {
        super.onResume();
        boolean openChatbot = getIntent().getBooleanExtra("OPEN_CHATBOT", false);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null && onboardingDone && !openChatbot) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}
