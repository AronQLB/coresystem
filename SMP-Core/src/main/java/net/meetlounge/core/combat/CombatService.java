package net.meetlounge.core.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CombatService {

    private final Map<UUID, Long> combat = new HashMap<>();

    public void tag(UUID uuid, long millis) {
        combat.put(uuid, System.currentTimeMillis() + millis);
    }

    public boolean inCombat(UUID uuid) {
        long until = combat.getOrDefault(uuid, 0L);

        if (System.currentTimeMillis() > until) {
            combat.remove(uuid);
            return false;
        }

        return true;
    }

    public void clear(UUID uuid) {
        combat.remove(uuid);
    }
}