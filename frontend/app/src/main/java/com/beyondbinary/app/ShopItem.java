package com.beyondbinary.app;

public class ShopItem {
    private final int imageResId;
    private final String name;
    private final int price;

    public ShopItem(int imageResId, String name, int price) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
    }

    public int getImageResId() { return imageResId; }
    public String getName() { return name; }
    public int getPrice() { return price; }
}
