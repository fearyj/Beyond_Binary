package com.beyondbinary.app.messaging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.R;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<MessageConversation> conversations;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(MessageConversation conversation);
    }

    public MessagesAdapter(List<MessageConversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_conversation, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageConversation conversation = conversations.get(position);
        holder.bind(conversation, listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView profilePicture;
        private TextView name;
        private TextView preview;
        private TextView time;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            name = itemView.findViewById(R.id.message_name);
            preview = itemView.findViewById(R.id.message_preview);
            time = itemView.findViewById(R.id.message_time);
        }

        public void bind(MessageConversation conversation, OnConversationClickListener listener) {
            profilePicture.setText(conversation.getProfileEmoji());
            name.setText(conversation.getName());
            preview.setText(conversation.getLastMessage());
            time.setText(conversation.getTime());

            itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
        }
    }
}
