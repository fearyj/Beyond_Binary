package com.beyondbinary.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.utils.EventCategoryHelper;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    private List<Event> events;
    private OnEventClickListener listener;

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
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView emojiText;
        private TextView titleText;
        private TextView categoryText;
        private TextView dateText;
        private TextView locationText;
        private TextView timeText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiText = itemView.findViewById(R.id.event_emoji);
            titleText = itemView.findViewById(R.id.event_title);
            categoryText = itemView.findViewById(R.id.event_category);
            dateText = itemView.findViewById(R.id.event_date);
            locationText = itemView.findViewById(R.id.event_location);
            timeText = itemView.findViewById(R.id.event_time);
        }

        public void bind(Event event, OnEventClickListener listener) {
            // Set emoji based on event type category
            String emoji = EventCategoryHelper.getEmojiForEventType(event.getEventType());
            emojiText.setText(emoji);

            // Set title
            titleText.setText(event.getTitle());

            // Set category
            String category = EventCategoryHelper.getCategoryForEventType(event.getEventType());
            categoryText.setText(category);

            // Parse and format date from time string (e.g., "Mon, Jan 27, 2025 • 6:00 PM - 8:00 PM")
            String dateFormatted = extractDate(event.getTime());
            dateText.setText(dateFormatted);

            // Set location
            locationText.setText(event.getLocation());

            // Extract just the time portion
            String timeFormatted = extractTime(event.getTime());
            timeText.setText(timeFormatted);

            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }

        private String extractDate(String timeString) {
            if (timeString == null || timeString.isEmpty()) {
                return "Date TBD";
            }

            // Parse "Mon, Jan 27, 2025 • 6:00 PM - 8:00 PM"
            if (timeString.contains("•")) {
                String datePart = timeString.split("•")[0].trim();
                return datePart;
            }

            return timeString;
        }

        private String extractTime(String timeString) {
            if (timeString == null || timeString.isEmpty()) {
                return "Time TBD";
            }

            // Parse "Mon, Jan 27, 2025 • 6:00 PM - 8:00 PM"
            if (timeString.contains("•")) {
                String[] parts = timeString.split("•");
                if (parts.length > 1) {
                    return parts[1].trim();
                }
            }

            return timeString;
        }
    }
}
