package net.meetlounge.core.npc;

import net.meetlounge.core.Core;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NpcService {

    private final Core plugin;
    private final NpcRepository repository;

    private final Map<String, UUID> spawned = new HashMap<>();
    private final Map<UUID, NpcData> dataByEntity = new HashMap<>();
    private boolean spawningNpc;

    public NpcService(Core plugin, NpcRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public void load() {
        despawnAll();

        for (NpcData npc : repository.findAll()) {
            spawn(npc);
        }
    }

    public boolean create(String id, NpcType type, NpcColor color, Location location, String command) {
        NpcData npc = new NpcData(id, type, color, location, command);

        if (!repository.save(npc)) {
            return false;
        }

        spawn(npc);
        return true;
    }

    public void delete(String id) {
        String key = id.toLowerCase();

        UUID uuid = spawned.remove(key);

        if (uuid != null) {
            Entity entity = Bukkit.getEntity(uuid);

            if (entity != null) {
                entity.remove();
            }

            dataByEntity.remove(uuid);
        }

        repository.delete(key);
    }

    public void execute(Player player, UUID entityUuid) {
        NpcData npc = dataByEntity.get(entityUuid);

        if (npc == null) {
            return;
        }

        String command = npc.command()
                .replace("%player%", player.getName());

        Bukkit.dispatchCommand(player, command.startsWith("/") ? command.substring(1) : command);
    }

    public boolean isNpc(Entity entity) {
        return dataByEntity.containsKey(entity.getUniqueId())
                || entity.getScoreboardTags().contains("meetlounge_npc");
    }

    public boolean isSpawningNpc() {
        return spawningNpc;
    }

    public void despawnAll() {
        for (UUID uuid : spawned.values()) {
            Entity entity = Bukkit.getEntity(uuid);

            if (entity != null) {
                entity.remove();
            }
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains("meetlounge_npc")
                        || dataByEntity.containsKey(entity.getUniqueId())) {
                    entity.remove();
                }
            }
        }

        spawned.clear();
        dataByEntity.clear();
    }
    private void spawn(NpcData npc) {

        Location location = npc.location().clone().add(0, 1, 0);

        World world = location.getWorld();

        if (world == null) {
            return;
        }

        LivingEntity entity;
        spawningNpc = true;

        try {
            entity = switch (npc.type()) {
                case ALLAY -> world.spawn(location, Allay.class);
                case BLAZE -> world.spawn(location, Blaze.class);
                case ENDERMAN -> world.spawn(location, Enderman.class);
                case VILLAGER -> world.spawn(location, Villager.class);
            };
        } finally {
            spawningNpc = false;
        }

        entity.addScoreboardTag("meetlounge_npc");

        entity.setAI(false);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.setGravity(false);
        entity.setGlowing(true);
        entity.setCustomNameVisible(true);

        entity.setCustomName(
                npc.color().color() + "" + org.bukkit.ChatColor.BOLD + npc.id()
        );

        spawned.put(npc.id().toLowerCase(), entity.getUniqueId());
        dataByEntity.put(entity.getUniqueId(), npc);
        NpcGlowUtil.apply(entity, npc.color());
    }

    public void deleteAllWorldNpcs() {

        for (World world : Bukkit.getWorlds()) {

            for (Entity entity : world.getEntities()) {

                if (entity.getScoreboardTags().contains("meetlounge_npc")) {
                    entity.remove();
                }
            }
        }

        spawned.clear();
        dataByEntity.clear();
    }
}
