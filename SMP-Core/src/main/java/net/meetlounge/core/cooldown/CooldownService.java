package net.meetlounge.core.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownService {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public void setCooldown(UUID uuid, String key, long durationMillis) {
        cooldowns
                .computeIfAbsent(uuid, ignored -> new HashMap<>())
                .put(key.toLowerCase(), System.currentTimeMillis() + durationMillis);
    }

    public boolean hasCooldown(UUID uuid, String key) {
        return getRemaining(uuid, key) > 0;
    }

    public long getRemaining(UUID uuid, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns == null) {
            return 0;
        }

        long expiresAt = playerCooldowns.getOrDefault(key.toLowerCase(), 0L);
        long remaining = expiresAt - System.currentTimeMillis();

        if (remaining <= 0) {
            playerCooldowns.remove(key.toLowerCase());
            return 0;
        }

        return remaining;
    }

    public void removeCooldown(UUID uuid, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);

        if (playerCooldowns != null) {
            playerCooldowns.remove(key.toLowerCase());
        }
    }

    public void clear(UUID uuid) {
        cooldowns.remove(uuid);
    }

    public void clearAll() {
        cooldowns.clear();
    }
}