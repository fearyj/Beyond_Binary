package com.beyondbinary.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ViewHolder> {

    private final List<ShopItem> items;

    public ShopItemAdapter(List<ShopItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopItem item = items.get(position);
        holder.iconImage.setImageResource(item.getImageResId());
        holder.nameText.setText(item.getName());
        holder.priceText.setText(item.getPrice() + " pts");

        // Hide divider on last item
        holder.divider.setVisibility(
                position == items.size() - 1 ? View.GONE : View.VISIBLE);

        holder.buyBtn.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),
                    "Purchased " + item.getName() + "!",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconImage;
        final TextView nameText;
        final TextView priceText;
        final TextView buyBtn;
        final View divider;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.shop_item_icon);
            nameText = itemView.findViewById(R.id.shop_item_name);
            priceText = itemView.findViewById(R.id.shop_item_price);
            buyBtn = itemView.findViewById(R.id.btn_buy);
            divider = itemView.findViewById(R.id.shop_divider);
        }
    }
}
