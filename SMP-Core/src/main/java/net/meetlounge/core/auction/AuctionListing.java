package net.meetlounge.core.auction;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class AuctionListing {

    private final int id;
    private final UUID sellerUuid;
    private final String sellerName;
    private final ItemStack item;
    private final double price;
    private final long createdAt;
    private final long expiresAt;
    private final boolean reminderSent;

    public AuctionListing(
            int id,
            UUID sellerUuid,
            String sellerName,
            ItemStack item,
            double price,
            long createdAt,
            long expiresAt,
            boolean reminderSent
    ) {
        this.id = id;
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item;
        this.price = price;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.reminderSent = reminderSent;
    }

    public int id() {
        return id;
    }

    public UUID sellerUuid() {
        return sellerUuid;
    }

    public String sellerName() {
        return sellerName;
    }

    public ItemStack item() {
        return item;
    }

    public double price() {
        return price;
    }

    public long createdAt() {
        return createdAt;
    }

    public long expiresAt() {
        return expiresAt;
    }

    public boolean reminderSent() {
        return reminderSent;
    }
}
