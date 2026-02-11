package com.beyondbinary.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EventPostDetailActivity extends AppCompatActivity {

    private List<String> imageUrls;
    private String eventTitle;
    private String eventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_post_detail);

        // Receive image URLs from intent (passed by ProfileActivity)
        ArrayList<String> intentUrls = getIntent().getStringArrayListExtra("IMAGE_URLS");
        eventTitle = getIntent().getStringExtra("EVENT_TITLE");
        eventType = getIntent().getStringExtra("EVENT_TYPE");

        if (intentUrls != null && !intentUrls.isEmpty()) {
            // Build full URLs from server-relative paths
            String baseUrl = com.beyondbinary.app.BuildConfig.API_BASE_URL.replace("/api/", "");
            imageUrls = new ArrayList<>();
            for (String url : intentUrls) {
                if (url.startsWith("http")) {
                    imageUrls.add(url);
                } else {
                    imageUrls.add(baseUrl + url);
                }
            }
        } else {
            // Fallback: empty list — no photos to display
            imageUrls = new ArrayList<>();
        }

        setupBackButton();
        setupHeader();
        setupCarousel();
        setupCaption();
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupCarousel() {
        ViewPager2 carousel = findViewById(R.id.image_carousel);
        TextView slideCounter = findViewById(R.id.slide_counter);
        LinearLayout dotContainer = findViewById(R.id.dot_indicators);

        EventPostImageAdapter adapter = new EventPostImageAdapter(imageUrls);
        carousel.setAdapter(adapter);

        // Create dot indicators
        View[] dots = new View[imageUrls.size()];
        for (int i = 0; i < imageUrls.size(); i++) {
            dots[i] = new View(this);
            int size = (int) getResources().getDimension(R.dimen.dot_indicator_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            int margin = (int) getResources().getDimension(R.dimen.dot_indicator_margin);
            params.setMargins(margin, 0, margin, 0);
            dots[i].setLayoutParams(params);
            dots[i].setBackgroundResource(i == 0 ? R.drawable.dot_indicator_active : R.drawable.dot_indicator_inactive);
            dotContainer.addView(dots[i]);
        }

        slideCounter.setText("1/" + imageUrls.size());

        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                slideCounter.setText((position + 1) + "/" + imageUrls.size());
                for (int i = 0; i < dots.length; i++) {
                    dots[i].setBackgroundResource(
                            i == position ? R.drawable.dot_indicator_active : R.drawable.dot_indicator_inactive
                    );
                }
            }
        });
    }

    private void setupHeader() {
        TextView postUsername = findViewById(R.id.post_username);
        ImageView headerProfilePic = findViewById(R.id.header_profile_pic);

        // Load the current user's name and profile picture
        SharedPreferences prefs = getSharedPreferences("beyondbinary_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            AppDatabaseHelper dbHelper = AppDatabaseHelper.getInstance(this);
            User user = dbHelper.getUserById(userId);
            if (user != null) {
                String name = user.getUsername();
                if (name == null || name.isEmpty()) {
                    String email = user.getEmail();
                    if (email != null && email.contains("@")) {
                        name = email.substring(0, email.indexOf("@"));
                    }
                }
                if (name != null) {
                    postUsername.setText(name);
                }

                String picPath = user.getProfilePicturePath();
                if (picPath != null && !picPath.isEmpty() && new File(picPath).exists()) {
                    Glide.with(this)
                            .load(new File(picPath))
                            .transform(new CircleCrop())
                            .into(headerProfilePic);
                }
            }
        }
    }

    private void setupCaption() {
        TextView captionText = findViewById(R.id.caption_text);

        String title = (eventTitle != null) ? eventTitle : "Event";
        String type = (eventType != null) ? eventType : "";
        String hashtag = type.isEmpty() ? "" : " #" + type.toLowerCase().replace(" ", "");

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // Bold event title
        String titlePart = title + " ";
        ssb.append(titlePart);
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                0, titlePart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(0xFF000000),
                0, titlePart.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Caption text
        String captionPart = imageUrls.size() + " photos from attendees ";
        int captionStart = ssb.length();
        ssb.append(captionPart);
        ssb.setSpan(new ForegroundColorSpan(0xFF1E2939),
                captionStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Blue hashtags
        if (!hashtag.isEmpty()) {
            String hashtags = hashtag + " #buddeee";
            int hashStart = ssb.length();
            ssb.append(hashtags);
            ssb.setSpan(new ForegroundColorSpan(0xFF3897F0),
                    hashStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        captionText.setText(ssb);
    }
}
