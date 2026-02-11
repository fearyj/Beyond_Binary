package com.beyondbinary.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShareUserAdapter extends RecyclerView.Adapter<ShareUserAdapter.ViewHolder> {

    private final List<ShareUser> users;
    private final Set<Integer> selectedUserIds = new HashSet<>();
    private final OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public ShareUserAdapter(List<ShareUser> users, OnSelectionChangedListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public List<ShareUser> getSelectedUsers() {
        List<ShareUser> selected = new ArrayList<>();
        for (ShareUser user : users) {
            if (selectedUserIds.contains(user.getId())) {
                selected.add(user);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_share_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShareUser user = users.get(position);

        // Set user emoji/avatar
        holder.userEmoji.setText(user.getProfileEmoji() != null ? user.getProfileEmoji() : "👤");

        // Set user name
        holder.userName.setText(user.getName());

        // Set bio if available
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            holder.userBio.setText(user.getBio());
            holder.userBio.setVisibility(View.VISIBLE);
        } else {
            holder.userBio.setVisibility(View.GONE);
        }

        // Set checkbox state
        holder.checkbox.setChecked(selectedUserIds.contains(user.getId()));

        // Click listener to toggle selection
        holder.itemView.setOnClickListener(v -> {
            if (selectedUserIds.contains(user.getId())) {
                selectedUserIds.remove(user.getId());
            } else {
                selectedUserIds.add(user.getId());
            }
            holder.checkbox.setChecked(selectedUserIds.contains(user.getId()));

            if (listener != null) {
                listener.onSelectionChanged(selectedUserIds.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userEmoji;
        TextView userName;
        TextView userBio;
        CheckBox checkbox;

        ViewHolder(View itemView) {
            super(itemView);
            userEmoji = itemView.findViewById(R.id.user_emoji);
            userName = itemView.findViewById(R.id.user_name);
            userBio = itemView.findViewById(R.id.user_bio);
            checkbox = itemView.findViewById(R.id.user_checkbox);
        }
    }
}
