package net.meetlounge.core.grave;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class GraveData {

    private final UUID owner;
    private final Location location;
    private final ItemStack[] contents;
    private final long createdAt;

    public GraveData(
            UUID owner,
            Location location,
            ItemStack[] contents,
            long createdAt
    ) {
        this.owner = owner;
        this.location = location;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public UUID owner() {
        return owner;
    }

    public Location location() {
        return location;
    }

    public ItemStack[] contents() {
        return contents;
    }

    public long createdAt() {
        return createdAt;
    }
}