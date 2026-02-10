package com.beyondbinary.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        private TextView titleText;
        private TextView locationText;
        private TextView typeText;
        private TextView timeText;
        private TextView participantsText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.event_title);
            locationText = itemView.findViewById(R.id.event_location);
            typeText = itemView.findViewById(R.id.event_type);
            timeText = itemView.findViewById(R.id.event_time);
            participantsText = itemView.findViewById(R.id.event_participants);
        }

        public void bind(Event event, OnEventClickListener listener) {
            titleText.setText(event.getTitle());
            locationText.setText(event.getLocation());
            typeText.setText(event.getEventType());
            timeText.setText(event.getTime());
            participantsText.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants());

            itemView.setOnClickListener(v -> listener.onEventClick(event));
        }
    }
}
