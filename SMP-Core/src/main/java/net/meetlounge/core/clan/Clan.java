package net.meetlounge.core.clan;

import java.util.UUID;

public final class Clan {

    private final int id;
    private String name;
    private String tag;
    private UUID ownerUuid;
    private String ownerName;
    private int kills;
    private int deaths;
    private double bank;
    private long createdAt;

    public Clan(int id, String name, String tag, UUID ownerUuid, String ownerName,
                int kills, int deaths, double bank, long createdAt) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.kills = kills;
        this.deaths = deaths;
        this.bank = bank;
        this.createdAt = createdAt;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String tag() {
        return tag;
    }

    public UUID ownerUuid() {
        return ownerUuid;
    }

    public String ownerName() {
        return ownerName;
    }

    public int kills() {
        return kills;
    }

    public int deaths() {
        return deaths;
    }

    public double bank() {
        return bank;
    }

    public long createdAt() {
        return createdAt;
    }

    public double kd() {
        if (deaths <= 0) {
            return kills;
        }

        return (double) kills / deaths;
    }
}