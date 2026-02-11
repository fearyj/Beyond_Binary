package com.beyondbinary.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

public class EventPhotoGridAdapter extends RecyclerView.Adapter<EventPhotoGridAdapter.GridViewHolder> {

    public interface OnPostClickListener {
        void onPostClick(int position);
    }

    private final List<String> thumbnailUrls;
    private final OnPostClickListener listener;

    public EventPhotoGridAdapter(List<String> thumbnailUrls, OnPostClickListener listener) {
        this.thumbnailUrls = thumbnailUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_photo_grid, parent, false);

        // Make grid items square
        int size = parent.getMeasuredWidth() / 3;
        view.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .load(thumbnailUrls.get(position))
                .transform(new CenterCrop())
                .placeholder(android.R.color.darker_gray)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbnailUrls.size();
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
        }
    }
}
