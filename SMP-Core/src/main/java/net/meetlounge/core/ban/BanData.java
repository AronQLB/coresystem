package net.meetlounge.core.ban;

import java.util.UUID;

public final class BanData {

    private final UUID uuid;
    private final String name;
    private final int reasonId;
    private final String reason;
    private final String staff;
    private final long createdAt;
    private final long expiresAt;
    private final boolean active;

    public BanData(UUID uuid, String name, int reasonId, String reason, String staff,
                   long createdAt, long expiresAt, boolean active) {
        this.uuid = uuid;
        this.name = name;
        this.reasonId = reasonId;
        this.reason = reason;
        this.staff = staff;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = active;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public int reasonId() {
        return reasonId;
    }

    public String reason() {
        return reason;
    }

    public String staff() {
        return staff;
    }

    public long createdAt() {
        return createdAt;
    }

    public long expiresAt() {
        return expiresAt;
    }

    public boolean active() {
        return active;
    }

    public boolean permanent() {
        return expiresAt == -1;
    }

    public boolean expired() {
        return !permanent() && System.currentTimeMillis() >= expiresAt;
    }
}