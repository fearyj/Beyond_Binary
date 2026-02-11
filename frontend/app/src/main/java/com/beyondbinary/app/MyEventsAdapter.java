package com.beyondbinary.app;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.api.UserEventsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder> {

    private final List<UserEventsResponse.UserEvent> events;
    private final OnEventClickListener listener;

    // Map event types to emojis
    private static final Map<String, String> EVENT_EMOJIS = new HashMap<>();
    static {
        EVENT_EMOJIS.put("Yoga", "🧘‍♀️");
        EVENT_EMOJIS.put("Yoga Class", "🧘‍♀️");
        EVENT_EMOJIS.put("Sports", "⚽");
        EVENT_EMOJIS.put("Basketball", "🏀");
        EVENT_EMOJIS.put("Soccer", "⚽");
        EVENT_EMOJIS.put("Running", "🏃");
        EVENT_EMOJIS.put("Hiking", "🥾");
        EVENT_EMOJIS.put("Cycling", "🚴");
        EVENT_EMOJIS.put("Ping Pong", "🏓");
        EVENT_EMOJIS.put("Social", "🎉");
        EVENT_EMOJIS.put("Coffee", "☕");
        EVENT_EMOJIS.put("Board Games", "🎲");
        EVENT_EMOJIS.put("Movie", "🎬");
        EVENT_EMOJIS.put("Reading", "📚");
        EVENT_EMOJIS.put("Book Club", "📖");
        EVENT_EMOJIS.put("Dining", "🍽️");
        EVENT_EMOJIS.put("Lunch", "🍜");
        EVENT_EMOJIS.put("Dinner", "🍷");
        EVENT_EMOJIS.put("BBQ", "🔥");
        EVENT_EMOJIS.put("Arts", "🎨");
        EVENT_EMOJIS.put("Painting", "🖌️");
        EVENT_EMOJIS.put("Photography", "📷");
        EVENT_EMOJIS.put("Museum", "🏛️");
        EVENT_EMOJIS.put("Outdoor", "🌿");
        EVENT_EMOJIS.put("Beach", "🏖️");
        EVENT_EMOJIS.put("Language Exchange", "🗣️");
        EVENT_EMOJIS.put("Crochet", "🧶");
    }

    // Badge colors
    private static final int COLOR_CATEGORY = 0xFFE6E6F7;  // Light lavender
    private static final int COLOR_HOST = 0xFFF5EC8A;       // Yellow
    private static final int COLOR_GUEST = 0xFF8AF5D2;      // Mint/teal

    public interface OnEventClickListener {
        void onEventClick(UserEventsResponse.UserEvent event);
    }

    public MyEventsAdapter(List<UserEventsResponse.UserEvent> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserEventsResponse.UserEvent event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventIcon;
        private final TextView titleText;
        private final TextView locationText;
        private final TextView timeText;
        private final TextView categoryBadge;
        private final TextView roleBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventIcon = itemView.findViewById(R.id.event_icon);
            titleText = itemView.findViewById(R.id.event_title);
            locationText = itemView.findViewById(R.id.event_location);
            timeText = itemView.findViewById(R.id.event_time);
            categoryBadge = itemView.findViewById(R.id.category_badge);
            roleBadge = itemView.findViewById(R.id.role_badge);
        }

        void bind(UserEventsResponse.UserEvent event) {
            // Emoji icon
            String eventType = event.getEventType() != null ? event.getEventType() : "";
            String emoji = EVENT_EMOJIS.getOrDefault(eventType, "📅");
            eventIcon.setText(emoji);

            // Title, location, time
            titleText.setText(event.getTitle());
            locationText.setText(event.getLocation());
            String time = event.getTime();
            timeText.setText(time != null ? time : "");

            // Category badge (top-right)
            categoryBadge.setText(eventType);
            GradientDrawable categoryBg = new GradientDrawable();
            categoryBg.setColor(COLOR_CATEGORY);
            categoryBg.setCornerRadius(4f);
            categoryBadge.setBackground(categoryBg);

            // Role badge (bottom-right)
            if (event.isHost()) {
                roleBadge.setText("Host");
                GradientDrawable hostBg = new GradientDrawable();
                hostBg.setColor(COLOR_HOST);
                hostBg.setCornerRadius(4f);
                roleBadge.setBackground(hostBg);
            } else {
                roleBadge.setText("Guest");
                GradientDrawable guestBg = new GradientDrawable();
                guestBg.setColor(COLOR_GUEST);
                guestBg.setCornerRadius(4f);
                roleBadge.setBackground(guestBg);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }
    }
}
