package com.beyondbinary.app.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.R;
import com.beyondbinary.app.data.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.EventViewHolder> {

    private List<Event> events = new ArrayList<>();

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.category.setText(event.getCategory());
        holder.description.setText(event.getDescription());
        holder.location.setText(event.getLocation());
        holder.mediaUrl = event.getMediaUrl();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onViewAttachedToWindow(@NonNull EventViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.mediaUrl != null) {
            ExoPlayerManager.getInstance(holder.playerView.getContext())
                    .attach(holder.playerView, holder.mediaUrl);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onViewDetachedFromWindow(@NonNull EventViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ExoPlayerManager.getInstance(holder.playerView.getContext())
                .detach(holder.playerView);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        final PlayerView playerView;
        final TextView title;
        final TextView category;
        final TextView description;
        final TextView location;
        String mediaUrl;

        @OptIn(markerClass = UnstableApi.class)
        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.player_view);
            title = itemView.findViewById(R.id.event_title);
            category = itemView.findViewById(R.id.event_category);
            description = itemView.findViewById(R.id.event_description);
            location = itemView.findViewById(R.id.event_location);
        }
    }
}
