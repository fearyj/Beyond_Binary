package com.beyondbinary.app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.utils.EventCategoryHelper;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> events;
    private OnEventClickListener listener;

    // Emoji background drawables cycling: blue, purple, green
    private static final int[] EMOJI_BACKGROUNDS = {
            R.drawable.bg_emoji_home_blue,
            R.drawable.bg_emoji_home_purple,
            R.drawable.bg_emoji_home_green
    };

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventListAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_list, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener, position);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout emojiBox;
        private TextView emojiText;
        private TextView titleText;
        private TextView categoryText;
        private TextView dateText;
        private TextView locationText;
        private TextView participantsText;
        private View btnEventDetails;
        private View btnViewMap;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiBox = itemView.findViewById(R.id.emoji_box);
            emojiText = itemView.findViewById(R.id.event_emoji);
            titleText = itemView.findViewById(R.id.event_title);
            categoryText = itemView.findViewById(R.id.event_category);
            dateText = itemView.findViewById(R.id.event_date);
            locationText = itemView.findViewById(R.id.event_location);
            participantsText = itemView.findViewById(R.id.event_participants);
            btnEventDetails = itemView.findViewById(R.id.btn_event_details);
            btnViewMap = itemView.findViewById(R.id.btn_view_map);
        }

        public void bind(Event event, OnEventClickListener listener, int position) {
            // Set emoji
            String emoji = EventCategoryHelper.getEmojiForEventType(event.getEventType());
            emojiText.setText(emoji);

            // Cycle emoji background color
            int bgIndex = position % EMOJI_BACKGROUNDS.length;
            emojiBox.setBackgroundResource(EMOJI_BACKGROUNDS[bgIndex]);

            // Title
            titleText.setText(event.getTitle());

            // Category
            String category = EventCategoryHelper.getCategoryForEventType(event.getEventType());
            categoryText.setText(category);

            // Combined date + time on one line
            String dateFormatted = extractDate(event.getTime());
            String timeFormatted = extractTime(event.getTime());
            if (!timeFormatted.isEmpty()) {
                dateText.setText(dateFormatted + " | " + timeFormatted);
            } else {
                dateText.setText(dateFormatted);
            }

            // Location
            locationText.setText(event.getLocation());

            // Participants
            participantsText.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " Pax");

            // Event Details button
            btnEventDetails.setOnClickListener(v -> listener.onEventClick(event));

            // View on Map button
            btnViewMap.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                v.getContext().startActivity(intent);
            });

            // Card click
            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }

        private String extractDate(String timeString) {
            if (timeString == null || timeString.isEmpty()) {
                return "Date TBD";
            }
            if (timeString.contains(" \u2022 ")) {
                return timeString.split(" \u2022 ")[0].trim();
            }
            return timeString;
        }

        private String extractTime(String timeString) {
            if (timeString == null || timeString.isEmpty()) {
                return "";
            }
            if (timeString.contains(" \u2022 ")) {
                String[] parts = timeString.split(" \u2022 ");
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }
            return "";
        }
    }
}
