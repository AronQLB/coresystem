package net.meetlounge.core.player;

import java.util.UUID;

public final class PlayerData {

    private final UUID uuid;
    private String name;
    private long firstJoin;
    private long lastJoin;
    private long playtime;
    private double coins;
    private String rank;
    private int level;
    private long xp;

    public PlayerData(UUID uuid, String name, long firstJoin, long lastJoin, long playtime, double coins, String rank, int level, long xp) {
        this.uuid = uuid;
        this.name = name;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.playtime = playtime;
        this.coins = coins;
        this.rank = rank;
        this.level = level;
        this.xp = xp;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public long firstJoin() {
        return firstJoin;
    }

    public long lastJoin() {
        return lastJoin;
    }

    public long playtime() {
        return playtime;
    }

    public double coins() {
        return coins;
    }

    public String rank() {
        return rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public void addPlaytime(long millis) {
        this.playtime += millis;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public long xp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = Math.max(0, xp);
    }

    public void addXp(long amount) {
        this.xp += amount;
    }

    public void removeXp(long amount) {
        this.xp = Math.max(0, this.xp - amount);
    }
}