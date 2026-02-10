package com.beyondbinary.app;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

public class EventPostDetailActivity extends AppCompatActivity {

    private boolean isLiked = false;
    private boolean isBookmarked = false;

    // 5 group-of-people photos from Unsplash (friends hanging out / brunch)
    private final List<String> imageUrls = Arrays.asList(
            "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800&h=800&fit=crop",
            "https://images.unsplash.com/photo-1528605248644-14dd04022da1?w=800&h=800&fit=crop",
            "https://images.unsplash.com/photo-1523301343968-6a6ebf63c672?w=800&h=800&fit=crop",
            "https://images.unsplash.com/photo-1543807535-eceef0bc6599?w=800&h=800&fit=crop",
            "https://images.unsplash.com/photo-1511632765486-a01980e01a18?w=800&h=800&fit=crop"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_post_detail);

        setupBackButton();
        setupCarousel();
        setupActionButtons();
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
            int size = (int) (6 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            int margin = (int) (3 * getResources().getDisplayMetrics().density);
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

    private void setupActionButtons() {
        ImageButton btnLike = findViewById(R.id.btn_like);
        ImageButton btnComment = findViewById(R.id.btn_comment);
        ImageButton btnShare = findViewById(R.id.btn_share);
        ImageButton btnBookmark = findViewById(R.id.btn_bookmark);
        TextView likesCount = findViewById(R.id.likes_count);

        btnLike.setImageResource(R.drawable.ic_heart_outline);
        btnComment.setImageResource(R.drawable.ic_comment);
        btnShare.setImageResource(R.drawable.ic_share);
        btnBookmark.setImageResource(R.drawable.ic_bookmark_outline);

        btnLike.setOnClickListener(v -> {
            isLiked = !isLiked;
            btnLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            likesCount.setText(isLiked ? "2,543 likes" : "2,542 likes");
        });

        btnBookmark.setOnClickListener(v -> {
            isBookmarked = !isBookmarked;
            btnBookmark.setImageResource(isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
        });
    }

    private void setupCaption() {
        TextView captionText = findViewById(R.id.caption_text);

        String username = "sarah.mitchell ";
        String caption = "Sunday brunch with my favorite people! Nothing better than good food and great company! ";
        String hashtags = "#brunch #squad #sundayvibes #friends";

        SpannableStringBuilder ssb = new SpannableStringBuilder();

        // Bold username
        int usernameStart = 0;
        ssb.append(username);
        ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                usernameStart, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(0xFF000000),
                usernameStart, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Caption text
        int captionStart = ssb.length();
        ssb.append(caption);
        ssb.setSpan(new ForegroundColorSpan(0xFF1E2939),
                captionStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Blue hashtags
        int hashStart = ssb.length();
        ssb.append(hashtags);
        ssb.setSpan(new ForegroundColorSpan(0xFF3897F0),
                hashStart, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        captionText.setText(ssb);
    }
}
