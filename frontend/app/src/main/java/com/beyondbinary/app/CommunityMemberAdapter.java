package com.beyondbinary.app;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // Show/hide badge pills
        if (member.hasLowSocial()) {
            holder.lowSocialText.setVisibility(View.VISIBLE);
            holder.lowSocialText.setText("\uD83E\uDEAB " + member.getLowSocial());
        } else {
            holder.lowSocialText.setVisibility(View.GONE);
        }

        if (member.hasLowPhysical()) {
            holder.lowPhysicalText.setVisibility(View.VISIBLE);
            holder.lowPhysicalText.setText("\uD83C\uDF42 " + member.getLowPhysical());
        } else {
            holder.lowPhysicalText.setVisibility(View.GONE);
        }

        // Hide divider on last item
        if (position == members.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        // Update invite button state
        if (member.isInvited()) {
            holder.inviteButton.setText("Invited");
            holder.inviteButton.setBackgroundResource(R.drawable.bg_invite_btn_invited);
            holder.inviteButton.setAlpha(0.7f);
        } else {
            holder.inviteButton.setText("Invite");
            holder.inviteButton.setBackgroundResource(R.drawable.bg_invite_btn);
            holder.inviteButton.setAlpha(1.0f);
        }

        holder.inviteButton.setOnClickListener(v -> {
            if (!member.isInvited()) {
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
            MediaPlayer mediaPlayer = MediaPlayer.create(view.getContext(), R.raw.airdrop_sound);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView profileEmoji;
        TextView nameText;
        TextView lowSocialText;
        TextView lowPhysicalText;
        TextView inviteButton;
        View divider;

        ViewHolder(View itemView) {
            super(itemView);
            profileEmoji = itemView.findViewById(R.id.profile_emoji);
            nameText = itemView.findViewById(R.id.member_name);
            lowSocialText = itemView.findViewById(R.id.low_social_text);
            lowPhysicalText = itemView.findViewById(R.id.low_physical_text);
            inviteButton = itemView.findViewById(R.id.invite_button);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
