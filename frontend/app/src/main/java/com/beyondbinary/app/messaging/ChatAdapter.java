package com.beyondbinary.app.messaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.EventDetailActivity;
import com.beyondbinary.app.MapsActivity;
import com.beyondbinary.app.R;
import com.beyondbinary.app.utils.EventCategoryHelper;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_EVENT_INVITE = 3;

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.isEventInvite()) {
            return VIEW_TYPE_EVENT_INVITE;
        }
        return message.isSentByMe() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EVENT_INVITE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_card, parent, false);
            return new EventInviteViewHolder(view);
        } else if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof EventInviteViewHolder) {
            ((EventInviteViewHolder) holder).bind(message);
        } else if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getText());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getText());
        }
    }

    static class EventInviteViewHolder extends RecyclerView.ViewHolder {
        private TextView eventEmoji;
        private TextView eventTitle;
        private TextView eventType;
        private TextView eventLocation;
        private TextView eventTime;
        private TextView eventParticipants;
        private TextView btnEventDetails;
        private TextView btnViewMap;

        public EventInviteViewHolder(@NonNull View itemView) {
            super(itemView);
            eventEmoji = itemView.findViewById(R.id.chat_event_emoji);
            eventTitle = itemView.findViewById(R.id.chat_event_title);
            eventType = itemView.findViewById(R.id.chat_event_type);
            eventLocation = itemView.findViewById(R.id.chat_event_location);
            eventTime = itemView.findViewById(R.id.chat_event_time);
            eventParticipants = itemView.findViewById(R.id.chat_event_participants);
            btnEventDetails = itemView.findViewById(R.id.btn_event_details);
            btnViewMap = itemView.findViewById(R.id.btn_view_map);
        }

        public void bind(ChatMessage message) {
            eventTitle.setText(message.getEventTitle());
            eventType.setText(message.getEventType());
            eventLocation.setText(message.getEventLocation());
            eventTime.setText(message.getEventTime());

            String emoji = EventCategoryHelper.getEmojiForEventType(message.getEventType());
            eventEmoji.setText(emoji);

            String participantsText = message.getCurrentParticipants() + "/" + message.getMaxParticipants() + " joined";
            eventParticipants.setText(participantsText);

            Context context = itemView.getContext();
            int eventId = message.getEventId();

            btnEventDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                context.startActivity(intent);
            });

            btnViewMap.setOnClickListener(v -> {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                context.startActivity(intent);
            });
        }
    }
}
