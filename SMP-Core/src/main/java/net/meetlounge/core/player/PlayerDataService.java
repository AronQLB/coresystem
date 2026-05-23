package net.meetlounge.core.player;


import net.meetlounge.core.Core;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDataService {

    private final Core plugin;
    private final PlayerDataRepository repository;

    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> sessionStart = new ConcurrentHashMap<>();

    public PlayerDataService(Core plugin, PlayerDataRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public void loadOnlinePlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            load(player);
        }
    }

    public void load(Player player) {
        plugin.scheduler().async(() -> {
            PlayerData data = repository.findByUuid(player.getUniqueId())
                    .orElseGet(() -> create(player));

            data.setName(player.getName());
            data.setLastJoin(System.currentTimeMillis());

            repository.save(data);

            cache.put(player.getUniqueId(), data);
            sessionStart.put(player.getUniqueId(), System.currentTimeMillis());
            plugin.scheduler().sync(() -> {
                if (player.isOnline()) {
                    plugin.permissions().apply(player);
                }
            });
        });
    }

    private PlayerData create(Player player) {
        long now = System.currentTimeMillis();

        return new PlayerData(
                player.getUniqueId(),
                player.getName(),
                now,
                now,
                0L,
                0.0,
                "player",
                1,
                0L
        );
    }

    public void save(PlayerData data) {
        if (!plugin.isEnabled()) {
            saveNow(data);
            return;
        }

        plugin.scheduler().async(() -> saveNow(data));
    }

    public void saveNow(PlayerData data) {
        repository.save(data);
    }

    public void saveAllNow() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = cache.get(player.getUniqueId());

            if (data == null) {
                continue;
            }

            updateSessionTime(player.getUniqueId(), data);
            saveNow(data);
        }
    }

    public void unload(Player player) {
        PlayerData data = cache.get(player.getUniqueId());

        if (data == null) {
            return;
        }

        updateSessionTime(player.getUniqueId(), data);
        save(data);

        cache.remove(player.getUniqueId());
        sessionStart.remove(player.getUniqueId());
    }

    public void saveAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = cache.get(player.getUniqueId());

            if (data == null) {
                continue;
            }

            updateSessionTime(player.getUniqueId(), data);

            if (plugin.isEnabled()) {
                save(data);
            } else {
                saveNow(data);
            }
        }
    }

    public void shutdown() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = cache.get(player.getUniqueId());

            if (data == null) {
                continue;
            }

            updateSessionTime(player.getUniqueId(), data);
            saveNow(data);
        }

        cache.clear();
        sessionStart.clear();
    }

    private void updateSessionTime(UUID uuid, PlayerData data) {
        long joinedAt = sessionStart.getOrDefault(uuid, System.currentTimeMillis());
        data.addPlaytime(System.currentTimeMillis() - joinedAt);
        data.setLastJoin(System.currentTimeMillis());
        sessionStart.put(uuid, System.currentTimeMillis());
    }

    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    public long currentPlaytime(UUID uuid) {
        PlayerData data = cache.get(uuid);

        if (data == null) {
            return 0L;
        }

        long joinedAt = sessionStart.getOrDefault(uuid, System.currentTimeMillis());
        return data.playtime() + Math.max(0L, System.currentTimeMillis() - joinedAt);
    }

    public boolean isLoaded(UUID uuid) {
        return cache.containsKey(uuid);
    }

    public PlayerData data(UUID uuid) {
        return cache.get(uuid);
    }

    public Optional<PlayerData> findStored(UUID uuid) {
        return repository.findByUuid(uuid);
    }
}
