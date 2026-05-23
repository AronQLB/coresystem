package net.meetlounge.core.clan;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClanInviteService {

    private final Map<UUID, Integer> invites = new HashMap<>();

    public void invite(UUID targetUuid, int clanId) {
        invites.put(targetUuid, clanId);
    }

    public boolean hasInvite(UUID targetUuid) {
        return invites.containsKey(targetUuid);
    }

    public Integer getInvite(UUID targetUuid) {
        return invites.get(targetUuid);
    }

    public void clear(UUID targetUuid) {
        invites.remove(targetUuid);
    }
}