package com.beyondbinary.app.chatbot;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.Event;
import com.beyondbinary.app.EventDetailActivity;
import com.beyondbinary.app.MapsActivity;
import com.beyondbinary.app.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatbotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int VIEW_TYPE_EVENTS = 3;
    private static final int VIEW_TYPE_SUGGESTIONS = 4;

    private static final Map<String, String> EVENT_EMOJIS = new HashMap<>();
    static {
        EVENT_EMOJIS.put("Yoga", "🧘‍♀️");
        EVENT_EMOJIS.put("Yoga Class", "🧘‍♀️");
        EVENT_EMOJIS.put("Meditation", "🧘");
        EVENT_EMOJIS.put("Meditation Session", "🧘");
        EVENT_EMOJIS.put("Basketball", "🏀");
        EVENT_EMOJIS.put("Basketball Game", "🏀");
        EVENT_EMOJIS.put("Soccer", "⚽");
        EVENT_EMOJIS.put("Soccer Match", "⚽");
        EVENT_EMOJIS.put("Tennis", "🎾");
        EVENT_EMOJIS.put("Tennis Tournament", "🎾");
        EVENT_EMOJIS.put("Hiking", "🥾");
        EVENT_EMOJIS.put("Hiking Trip", "🥾");
        EVENT_EMOJIS.put("Running", "🏃");
        EVENT_EMOJIS.put("Cycling", "🚴");
        EVENT_EMOJIS.put("Swimming", "🏊");
        EVENT_EMOJIS.put("Coffee", "☕");
        EVENT_EMOJIS.put("Coffee Meetup", "☕");
        EVENT_EMOJIS.put("Cooking", "🍳");
        EVENT_EMOJIS.put("Cooking Class", "🍳");
        EVENT_EMOJIS.put("Music", "🎵");
        EVENT_EMOJIS.put("Concert", "🎵");
        EVENT_EMOJIS.put("Art", "🎨");
        EVENT_EMOJIS.put("Art Class", "🎨");
        EVENT_EMOJIS.put("Book Club", "📚");
        EVENT_EMOJIS.put("Reading", "📚");
        EVENT_EMOJIS.put("Gaming", "🎮");
        EVENT_EMOJIS.put("Photography", "📷");
        EVENT_EMOJIS.put("Dance", "💃");
        EVENT_EMOJIS.put("Dance Class", "💃");
        EVENT_EMOJIS.put("Picnic", "🧺");
        EVENT_EMOJIS.put("Park Picnic", "🧺");
        EVENT_EMOJIS.put("Beach", "🏖️");
        EVENT_EMOJIS.put("Beach Volleyball", "🏐");
        EVENT_EMOJIS.put("Volunteer", "🤝");
        EVENT_EMOJIS.put("Volunteering", "🤝");
        EVENT_EMOJIS.put("Workshop", "🔧");
        EVENT_EMOJIS.put("Fitness", "💪");
        EVENT_EMOJIS.put("Gym", "💪");
        EVENT_EMOJIS.put("Brunch", "🥂");
        EVENT_EMOJIS.put("Dinner", "🍽️");
        EVENT_EMOJIS.put("Party", "🎉");
    }

    private List<Message> messageList;
    private OnSuggestionClickListener suggestionClickListener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String eventType, int maxParticipants, String descriptionHint, String userContext);
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.suggestionClickListener = listener;
    }

    public ChatbotAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isSentByUser()) {
            return VIEW_TYPE_USER;
        } else if (message.getMessageType() == Message.TYPE_EVENTS) {
            return VIEW_TYPE_EVENTS;
        } else if (message.getMessageType() == Message.TYPE_SUGGESTIONS) {
            return VIEW_TYPE_SUGGESTIONS;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new TextMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_EVENTS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_event, parent, false);
            return new EventMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_SUGGESTIONS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_suggestions, parent, false);
            return new SuggestionMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message, parent, false);
            return new TextMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof TextMessageViewHolder) {
            ((TextMessageViewHolder) holder).bind(message);
        } else if (holder instanceof EventMessageViewHolder) {
            ((EventMessageViewHolder) holder).bind(message);
        } else if (holder instanceof SuggestionMessageViewHolder) {
            ((SuggestionMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private static String getEmojiForEvent(String eventType) {
        if (eventType == null) return "🎉";
        // Try exact match first
        String emoji = EVENT_EMOJIS.get(eventType);
        if (emoji != null) return emoji;
        // Try case-insensitive partial match
        String lower = eventType.toLowerCase();
        for (Map.Entry<String, String> entry : EVENT_EMOJIS.entrySet()) {
            if (lower.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return "🎉";
    }

    // ViewHolder for text messages
    static class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        TextMessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.user_message_text);
            if (messageText == null) {
                messageText = view.findViewById(R.id.bot_message_text);
            }
        }

        void bind(Message message) {
            messageText.setText(message.getText());
        }
    }

    // ViewHolder for event cards
    static class EventMessageViewHolder extends RecyclerView.ViewHolder {
        TextView introText;
        LinearLayout eventsContainer;

        EventMessageViewHolder(View view) {
            super(view);
            introText = view.findViewById(R.id.event_intro_text);
            eventsContainer = view.findViewById(R.id.events_container);
        }

        void bind(Message message) {
            introText.setText(message.getText());
            eventsContainer.removeAllViews();

            List<Event> events = message.getEvents();
            if (events != null) {
                for (Event event : events) {
                    View eventCard = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_chat_event_card, eventsContainer, false);

                    TextView emoji = eventCard.findViewById(R.id.chat_event_emoji);
                    TextView title = eventCard.findViewById(R.id.chat_event_title);
                    TextView type = eventCard.findViewById(R.id.chat_event_type);
                    TextView location = eventCard.findViewById(R.id.chat_event_location);
                    TextView time = eventCard.findViewById(R.id.chat_event_time);
                    TextView participants = eventCard.findViewById(R.id.chat_event_participants);
                    View btnDetails = eventCard.findViewById(R.id.btn_event_details);
                    View btnMap = eventCard.findViewById(R.id.btn_view_map);

                    emoji.setText(getEmojiForEvent(event.getEventType()));
                    title.setText(event.getTitle());
                    type.setText(event.getEventType());
                    location.setText("📍 " + event.getLocation());
                    time.setText("🕐 " + event.getTime());
                    participants.setText("👥 " + event.getCurrentParticipants() + "/" + event.getMaxParticipants());

                    // Event Details button → open event detail
                    btnDetails.setOnClickListener(v -> {
                        Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                        intent.putExtra("EVENT_ID", event.getId());
                        itemView.getContext().startActivity(intent);
                    });

                    // View on Map button → open maps
                    btnMap.setOnClickListener(v -> {
                        Intent intent = new Intent(itemView.getContext(), MapsActivity.class);
                        itemView.getContext().startActivity(intent);
                    });

                    // Whole card click → event detail
                    eventCard.setOnClickListener(v -> {
                        Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                        intent.putExtra("EVENT_ID", event.getId());
                        itemView.getContext().startActivity(intent);
                    });

                    eventsContainer.addView(eventCard);
                }
            }
        }
    }

    // ViewHolder for event suggestions
    class SuggestionMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout suggestionsContainer;

        SuggestionMessageViewHolder(View view) {
            super(view);
            suggestionsContainer = view.findViewById(R.id.suggestions_container);
        }

        void bind(Message message) {
            suggestionsContainer.removeAllViews();

            List<String> suggestions = message.getEventTypeSuggestions();
            if (suggestions != null) {
                for (String eventType : suggestions) {
                    View suggestionCard = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_chat_suggestion_card, suggestionsContainer, false);

                    TextView title = suggestionCard.findViewById(R.id.suggestion_title);
                    title.setText(eventType);

                    // Click listener to create event with this suggestion
                    suggestionCard.setOnClickListener(v -> {
                        if (suggestionClickListener != null) {
                            suggestionClickListener.onSuggestionClick(
                                    eventType,
                                    message.getSuggestedMaxParticipants(),
                                    message.getDescriptionHint(),
                                    message.getUserContext()
                            );
                        }
                    });

                    suggestionsContainer.addView(suggestionCard);
                }
            }
        }
    }
}
