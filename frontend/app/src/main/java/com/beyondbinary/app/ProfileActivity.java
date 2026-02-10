package com.beyondbinary.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.InteractionsResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UserResponse;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI elements
    private ImageView profilePicture;
    private TextView usernameText;
    private TextView bioText;
    private TextView eventsCountText;
    private TextView hostedCountText;
    private TextView friendsCountText;
    private RecyclerView photoGrid;
    private FlexboxLayout tagsContainer;
    private FrameLayout personTabContent;
    private TextView achievementUsername;
    private androidx.core.widget.NestedScrollView scrollView;

    // Tab views
    private FrameLayout tabGrid;
    private FrameLayout tabPerson;
    private ImageView tabGridIcon;
    private ImageView tabPersonIcon;

    // Hardcoded grid thumbnails
    private final List<String> gridThumbnails = Arrays.asList(
            "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=400&h=400&fit=crop",
            "https://images.unsplash.com/photo-1523301343968-6a6ebf63c672?w=400&h=400&fit=crop"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profilePicture = findViewById(R.id.profile_picture);
        usernameText = findViewById(R.id.username);
        bioText = findViewById(R.id.bio);
        eventsCountText = findViewById(R.id.events_count);
        hostedCountText = findViewById(R.id.hosted_count);
        friendsCountText = findViewById(R.id.friends_count);
        photoGrid = findViewById(R.id.photo_grid);
        tagsContainer = findViewById(R.id.tags_container);
        personTabContent = findViewById(R.id.person_tab_content);
        achievementUsername = findViewById(R.id.achievement_username);
        scrollView = findViewById(R.id.scroll_view);

        // Tab views
        tabGrid = findViewById(R.id.tab_grid);
        tabPerson = findViewById(R.id.tab_person);
        tabGridIcon = findViewById(R.id.tab_grid_icon);
        tabPersonIcon = findViewById(R.id.tab_person_icon);

        // Sign out button
        findViewById(R.id.btn_sign_out).setOnClickListener(v -> signOut());

        // Shopping bag FAB
        findViewById(R.id.fab_shopping).setOnClickListener(v -> showShopDialog());

        // Setup tabs
        setupTabs();

        // Setup photo grid
        setupPhotoGrid();

        // Load user data
        loadUserProfile();
        loadUserStats();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupPhotoGrid() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        photoGrid.setLayoutManager(gridLayoutManager);

        EventPhotoGridAdapter adapter = new EventPhotoGridAdapter(gridThumbnails, position -> {
            if (position == 0) {
                Intent intent = new Intent(ProfileActivity.this, EventPostDetailActivity.class);
                startActivity(intent);
            }
        });

        photoGrid.setAdapter(adapter);
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
                    // Preserve local-only fields before overwriting
                    User existingUser = dbHelper.getUserById(userData.getId());
                    String existingPicPath = (existingUser != null) ? existingUser.getProfilePicturePath() : null;

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
                    user.setProfilePicturePath(existingPicPath);
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
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                displayName = email.substring(0, email.indexOf("@"));
            } else {
                displayName = "User";
            }
        }
        usernameText.setText(displayName);
        achievementUsername.setText(displayName);

        String caption = user.getCaption();
        if (caption != null && !caption.isEmpty()) {
            bioText.setText(caption);
        } else if (user.getBio() != null && !user.getBio().isEmpty()) {
            bioText.setText(user.getBio());
        } else {
            bioText.setText("");
        }

        // Load profile picture
        String picPath = user.getProfilePicturePath();
        android.util.Log.d(TAG, "Profile pic path: " + picPath);
        if (picPath != null && !picPath.isEmpty()) {
            File picFile = new File(picPath);
            android.util.Log.d(TAG, "Profile pic file exists: " + picFile.exists());
            if (picFile.exists()) {
                profilePicture.setPadding(0, 0, 0, 0);
                Glide.with(this)
                        .load(picFile)
                        .transform(new CircleCrop())
                        .into(profilePicture);
            }
        }

        // Populate interest tags
        populateTags(user);
    }

    private void populateTags(User user) {
        tagsContainer.removeAllViews();
        List<String> tags = user.getInterestTagsAsList();
        if (tags.isEmpty()) return;

        for (String tag : tags) {
            String trimmed = tag.trim();
            if (trimmed.isEmpty()) continue;

            TextView tagView = new TextView(this);
            tagView.setText(trimmed);
            tagView.setTextSize(11);
            tagView.setTextColor(0x80000000); // black with 50% alpha
            tagView.setBackgroundResource(R.drawable.bg_trait_tag);
            int hPad = (int) (12 * getResources().getDisplayMetrics().density);
            int vPad = (int) (6 * getResources().getDisplayMetrics().density);
            tagView.setPadding(hPad, vPad, hPad, vPad);

            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) (3 * getResources().getDisplayMetrics().density);
            lp.setMargins(0, margin, margin, margin);
            tagView.setLayoutParams(lp);

            tagsContainer.addView(tagView);
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
        tabGrid.setOnClickListener(v -> selectGridTab());
        tabPerson.setOnClickListener(v -> selectPersonTab());
    }

    private void selectGridTab() {
        // Active: grid dark, person white
        tabGrid.setBackgroundColor(0xFF343149);
        tabPerson.setBackgroundColor(0xFFFFFFFF);
        tabGridIcon.setImageResource(R.drawable.ic_grid_active);
        tabPersonIcon.setImageResource(R.drawable.ic_person_outline);

        photoGrid.setVisibility(View.VISIBLE);
        personTabContent.setVisibility(View.GONE);
    }

    private void selectPersonTab() {
        // Active: person dark, grid white
        tabGrid.setBackgroundColor(0xFFFFFFFF);
        tabPerson.setBackgroundColor(0xFF343149);
        tabGridIcon.setImageResource(R.drawable.ic_grid);
        tabPersonIcon.setImageResource(R.drawable.ic_person_active);

        photoGrid.setVisibility(View.GONE);
        personTabContent.setVisibility(View.VISIBLE);

        // Scroll to top so the full achievement view is visible
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
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

    private void showShopDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_shop, null);
        dialog.setContentView(sheetView);

        // Hardcoded shop items matching Figma design
        List<ShopItem> shopItems = new ArrayList<>();
        shopItems.add(new ShopItem(R.drawable.img_shop_fountain, "Fountain", 60));
        shopItems.add(new ShopItem(R.drawable.img_shop_garden, "Garden", 60));
        shopItems.add(new ShopItem(R.drawable.img_shop_bench, "Bench", 30));
        shopItems.add(new ShopItem(R.drawable.img_shop_signboard, "Signboard", 20));

        RecyclerView list = sheetView.findViewById(R.id.shop_items_grid);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new ShopItemAdapter(shopItems));

        // Close button
        sheetView.findViewById(R.id.btn_close_shop).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
