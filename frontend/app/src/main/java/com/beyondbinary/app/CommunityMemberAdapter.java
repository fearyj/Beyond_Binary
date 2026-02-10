package com.beyondbinary.app;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommunityMemberAdapter extends RecyclerView.Adapter<CommunityMemberAdapter.ViewHolder> {

    private List<CommunityMember> members;
    private OnInviteClickListener listener;

    public interface OnInviteClickListener {
        void onInviteClick(CommunityMember member);
    }

    public CommunityMemberAdapter(List<CommunityMember> members, OnInviteClickListener listener) {
        this.members = members;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityMember member = members.get(position);

        holder.nameText.setText(member.getName());

        // Show/hide status indicators
        if (member.hasLowSocial()) {
            holder.lowSocialText.setVisibility(View.VISIBLE);
            holder.lowSocialText.setText("📱 " + member.getLowSocial());
        } else {
            holder.lowSocialText.setVisibility(View.GONE);
        }

        if (member.hasLowPhysical()) {
            holder.lowPhysicalText.setVisibility(View.VISIBLE);
            holder.lowPhysicalText.setText("🏃 " + member.getLowPhysical());
        } else {
            holder.lowPhysicalText.setVisibility(View.GONE);
        }

        // Update button state
        if (member.isInvited()) {
            holder.inviteButton.setText("Invited");
            holder.inviteButton.setEnabled(false);
            holder.inviteButton.setAlpha(0.5f);
        } else {
            holder.inviteButton.setText("Invite");
            holder.inviteButton.setEnabled(true);
            holder.inviteButton.setAlpha(1.0f);
        }

        holder.inviteButton.setOnClickListener(v -> {
            if (!member.isInvited()) {
                // Play invite sound
                playInviteSound(v);

                member.setInvited(true);
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onInviteClick(member);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    private void playInviteSound(View view) {
        try {
            // Play custom AirDrop sound
            MediaPlayer mediaPlayer = MediaPlayer.create(view.getContext(), R.raw.airdrop_sound);

            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                });
                mediaPlayer.start();
            }
        } catch (Exception e) {
            // Silently fail if sound cannot be played
            e.printStackTrace();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView profileEmoji;
        TextView nameText;
        TextView lowSocialText;
        TextView lowPhysicalText;
        Button inviteButton;

        ViewHolder(View itemView) {
            super(itemView);
            profileEmoji = itemView.findViewById(R.id.profile_emoji);
            nameText = itemView.findViewById(R.id.member_name);
            lowSocialText = itemView.findViewById(R.id.low_social_text);
            lowPhysicalText = itemView.findViewById(R.id.low_physical_text);
            inviteButton = itemView.findViewById(R.id.invite_button);
        }
    }
}
