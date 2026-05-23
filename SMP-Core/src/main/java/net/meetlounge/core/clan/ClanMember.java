package net.meetlounge.core.clan;

import java.util.UUID;

public final class ClanMember {

    private final UUID uuid;
    private final String name;
    private final int clanId;
    private final ClanRole role;
    private final long joinedAt;

    public ClanMember(UUID uuid, String name, int clanId, ClanRole role, long joinedAt) {
        this.uuid = uuid;
        this.name = name;
        this.clanId = clanId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public int clanId() {
        return clanId;
    }

    public ClanRole role() {
        return role;
    }

    public long joinedAt() {
        return joinedAt;
    }
}