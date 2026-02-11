package com.beyondbinary.app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.AttendedGalleriesResponse;
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
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    // Dynamic gallery data fetched from server
    private final List<AttendedGalleriesResponse.EventGallery> galleries = new ArrayList<>();
    private final List<String> gridThumbnails = new ArrayList<>();
    private EventPhotoGridAdapter gridAdapter;

    // Edit profile photo picker
    private ActivityResultLauncher<String> editPhotoPickerLauncher;
    private ImageView editDialogProfilePic;
    private String pendingProfilePicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register photo picker before setContentView
        editPhotoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String path = copyPhotoToInternalStorage(uri);
                        if (path != null) {
                            pendingProfilePicPath = path;
                            if (editDialogProfilePic != null) {
                                editDialogProfilePic.setPadding(0, 0, 0, 0);
                                Glide.with(this)
                                        .load(new File(path))
                                        .transform(new CircleCrop())
                                        .into(editDialogProfilePic);
                            }
                        }
                    }
                }
        );

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

        // Edit Profile button
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> showEditProfileDialog());

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

        gridAdapter = new EventPhotoGridAdapter(gridThumbnails, position -> {
            if (position < galleries.size()) {
                AttendedGalleriesResponse.EventGallery gallery = galleries.get(position);
                Intent intent = new Intent(ProfileActivity.this, EventPostDetailActivity.class);
                intent.putStringArrayListExtra("IMAGE_URLS", new ArrayList<>(gallery.getImageUrls()));
                intent.putExtra("EVENT_TITLE", gallery.getTitle());
                intent.putExtra("EVENT_TYPE", gallery.getEventType());
                startActivity(intent);
            }
        });

        photoGrid.setAdapter(gridAdapter);
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
            tagView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tag_text_size));
            tagView.setTextColor(0x80000000); // black with 50% alpha
            tagView.setBackgroundResource(R.drawable.bg_trait_tag);
            int hPad = (int) getResources().getDimension(R.dimen.tag_padding_horizontal);
            int vPad = (int) getResources().getDimension(R.dimen.tag_padding_vertical);
            tagView.setPadding(hPad, vPad, hPad, vPad);

            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) getResources().getDimension(R.dimen.tag_margin);
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

        // Load interaction stats
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

        // Load attended event galleries for the photo grid
        loadAttendedGalleries(userId);
    }

    private void loadAttendedGalleries(int userId) {
        ApiService apiService = RetrofitClient.getApiService();
        String baseUrl = com.beyondbinary.app.BuildConfig.API_BASE_URL.replace("/api/", "");

        apiService.getAttendedGalleries(userId).enqueue(new retrofit2.Callback<AttendedGalleriesResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<AttendedGalleriesResponse> call,
                                   @NonNull retrofit2.Response<AttendedGalleriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    galleries.clear();
                    gridThumbnails.clear();

                    List<AttendedGalleriesResponse.EventGallery> fetched = response.body().getGalleries();
                    if (fetched != null) {
                        for (AttendedGalleriesResponse.EventGallery gallery : fetched) {
                            // Only show events that have at least one photo
                            if (gallery.getImageUrls() != null && !gallery.getImageUrls().isEmpty()) {
                                galleries.add(gallery);
                                gridThumbnails.add(baseUrl + gallery.getImageUrls().get(0));
                            }
                        }
                    }

                    if (gridAdapter != null) {
                        gridAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<AttendedGalleriesResponse> call, @NonNull Throwable t) {
                // Keep grid empty
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

    private void showEditProfileDialog() {
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return;

        AppDatabaseHelper dbHelper = AppDatabaseHelper.getInstance(this);
        User user = dbHelper.getUserById(userId);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        dialog.setContentView(sheetView);

        editDialogProfilePic = sheetView.findViewById(R.id.edit_profile_picture);
        TextInputEditText editUsername = sheetView.findViewById(R.id.edit_username);
        TextInputEditText editCaption = sheetView.findViewById(R.id.edit_caption);
        TextInputEditText editDob = sheetView.findViewById(R.id.edit_dob);
        TextInputEditText editAddress = sheetView.findViewById(R.id.edit_address);

        pendingProfilePicPath = null;

        // Pre-fill current values
        if (user != null) {
            if (user.getUsername() != null) editUsername.setText(user.getUsername());
            if (user.getCaption() != null) editCaption.setText(user.getCaption());
            if (user.getDob() != null) editDob.setText(user.getDob());
            if (user.getAddress() != null) editAddress.setText(user.getAddress());

            String picPath = user.getProfilePicturePath();
            if (picPath != null && !picPath.isEmpty() && new File(picPath).exists()) {
                editDialogProfilePic.setPadding(0, 0, 0, 0);
                Glide.with(this)
                        .load(new File(picPath))
                        .transform(new CircleCrop())
                        .into(editDialogProfilePic);
            }
        }

        // Photo picker
        sheetView.findViewById(R.id.btn_change_photo).setOnClickListener(v ->
                editPhotoPickerLauncher.launch("image/*"));
        editDialogProfilePic.setOnClickListener(v ->
                editPhotoPickerLauncher.launch("image/*"));

        // Date picker for DOB
        editDob.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -18);
            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                editDob.setText(sdf.format(selected.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Save button
        sheetView.findViewById(R.id.btn_save_profile).setOnClickListener(v -> {
            String username = editUsername.getText() != null ? editUsername.getText().toString().trim() : "";
            if (username.isEmpty()) {
                editUsername.setError("Username is required");
                return;
            }

            String caption = editCaption.getText() != null ? editCaption.getText().toString().trim() : "";
            String dob = editDob.getText() != null ? editDob.getText().toString().trim() : "";
            String address = editAddress.getText() != null ? editAddress.getText().toString().trim() : "";

            saveProfileChanges(userId, username, caption, dob, address, dialog);
        });

        dialog.show();
    }

    private void saveProfileChanges(int userId, String username, String caption, String dob, String address, BottomSheetDialog dialog) {
        AppDatabaseHelper dbHelper = AppDatabaseHelper.getInstance(this);
        User user = dbHelper.getUserById(userId);
        if (user == null) user = new User();

        user.setId(userId);
        user.setUsername(username);
        user.setCaption(caption);
        user.setDob(dob);
        user.setAddress(address);
        if (pendingProfilePicPath != null) {
            user.setProfilePicturePath(pendingProfilePicPath);
        }
        dbHelper.insertUser(user);

        // Update backend
        ApiService apiService = RetrofitClient.getApiService();
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("caption", caption);
        body.put("dob", dob);
        body.put("address", address);

        User finalUser = user;
        apiService.updateUser(userId, body).enqueue(new retrofit2.Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<UserResponse> call,
                                   @NonNull retrofit2.Response<UserResponse> response) {
                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                displayUserData(finalUser);
                dialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<UserResponse> call, @NonNull Throwable t) {
                // Still update locally
                displayUserData(finalUser);
                dialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Saved locally", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String copyPhotoToInternalStorage(Uri uri) {
        try {
            File dir = new File(getFilesDir(), "profile_photos");
            if (!dir.exists()) dir.mkdirs();
            File destFile = new File(dir, "profile_" + System.currentTimeMillis() + ".jpg");
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show();
            return null;
        }
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
