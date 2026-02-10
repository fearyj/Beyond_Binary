package com.beyondbinary.app;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.api.UserEventsResponse;

import java.util.List;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder> {

    private final List<UserEventsResponse.UserEvent> events;
    private final OnEventClickListener listener;

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
        private final TextView titleText;
        private final TextView roleBadge;
        private final TextView eventType;
        private final TextView locationText;
        private final TextView timeText;
        private final TextView participantsText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.event_title);
            roleBadge = itemView.findViewById(R.id.role_badge);
            eventType = itemView.findViewById(R.id.event_type);
            locationText = itemView.findViewById(R.id.event_location);
            timeText = itemView.findViewById(R.id.event_time);
            participantsText = itemView.findViewById(R.id.event_participants);
        }

        void bind(UserEventsResponse.UserEvent event) {
            titleText.setText(event.getTitle());

            // Role badge
            if (event.isHost()) {
                roleBadge.setText("Host");
                GradientDrawable hostBg = new GradientDrawable();
                hostBg.setColor(0xFF4CAF50); // Green
                hostBg.setCornerRadius(12f);
                roleBadge.setBackground(hostBg);
            } else {
                roleBadge.setText("Participant");
                GradientDrawable participantBg = new GradientDrawable();
                participantBg.setColor(0xFF2196F3); // Blue
                participantBg.setCornerRadius(12f);
                roleBadge.setBackground(participantBg);
            }

            eventType.setText(event.getEventType());
            locationText.setText(event.getLocation());

            String time = event.getTime();
            timeText.setText(time != null ? time : "");

            participantsText.setText(event.getCurrentParticipants() + "/" +
                    event.getMaxParticipants() + " participants");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }
    }
}
