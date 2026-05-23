package net.meetlounge.core.bloodmoon;

import net.meetlounge.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public final class BloodMoonService {

    private final Core plugin;
    private final Random random = new Random();
    private final Map<UUID, Boolean> activeWorlds = new HashMap<>();
    private final Map<UUID, Boolean> rolledWorlds = new HashMap<>();
    private final Set<UUID> forcedWorlds = new HashSet<>();

    public BloodMoonService(Core plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!enabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskTimer(plugin, this::tickWorlds, 20L, 200L);
    }

    public boolean enabled() {
        return plugin.configs().config().get().getBoolean("bloodmoon.enabled", true);
    }

    public boolean isActive(World world) {
        return world != null && activeWorlds.getOrDefault(world.getUID(), false);
    }

    public boolean isNight(World world) {
        if (world == null || world.getEnvironment() != World.Environment.NORMAL) {
            return false;
        }

        long time = world.getTime();
        return time >= 13000L && time <= 23000L;
    }

    public int boostedMobLevel(World world, int level) {
        if (!isActive(world)) {
            return level;
        }

        return level + plugin.configs().config().get().getInt("bloodmoon.mob-level-bonus", 5);
    }

    public double damageMultiplier(World world) {
        if (!isActive(world)) {
            return 1.0;
        }

        return plugin.configs().config().get().getDouble("bloodmoon.damage-multiplier", 1.35);
    }

    public double xpMultiplier(World world) {
        if (!isActive(world)) {
            return 1.0;
        }

        return plugin.configs().config().get().getDouble("bloodmoon.xp-multiplier", 1.8);
    }

    public double coinMultiplier(World world) {
        if (!isActive(world)) {
            return 1.0;
        }

        return plugin.configs().config().get().getDouble("bloodmoon.coin-multiplier", 2.0);
    }

    public boolean rollBoss(World world) {
        return isActive(world) && random.nextDouble() < plugin.configs().config().get().getDouble("bloodmoon.boss-chance", 0.05);
    }

    public boolean rollRareDrop(World world) {
        return isActive(world) && random.nextDouble() < plugin.configs().config().get().getDouble("bloodmoon.rare-drop-chance", 0.08);
    }

    public void forceStart(World world) {
        if (world == null) {
            return;
        }

        forcedWorlds.add(world.getUID());
        setActive(world, true);
        rolledWorlds.put(world.getUID(), true);
    }

    public void forceStop(World world) {
        if (world == null) {
            return;
        }

        forcedWorlds.remove(world.getUID());
        setActive(world, false);
        rolledWorlds.remove(world.getUID());
    }

    private void tickWorlds() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() != World.Environment.NORMAL) {
                continue;
            }

            if (forcedWorlds.contains(world.getUID())) {
                pulse(world);
                continue;
            }

            if (!isNight(world)) {
                if (isActive(world)) {
                    setActive(world, false);
                }

                rolledWorlds.remove(world.getUID());
                continue;
            }

            if (rolledWorlds.containsKey(world.getUID())) {
                pulse(world);
                continue;
            }

            rolledWorlds.put(world.getUID(), true);

            double chance = plugin.configs().config().get().getDouble("bloodmoon.night-chance", 0.25);
            if (random.nextDouble() <= chance) {
                setActive(world, true);
            }
        }
    }

    private void setActive(World world, boolean active) {
        boolean wasActive = isActive(world);
        activeWorlds.put(world.getUID(), active);

        if (active == wasActive) {
            return;
        }

        if (active) {
            world.setStorm(true);
            world.setThundering(true);
            world.setWeatherDuration(20 * 60 * 8);
            world.setThunderDuration(20 * 60 * 8);

            Bukkit.broadcastMessage(plugin.messages().raw(Core.prefix + "&4&lBLOODMOON &8| &cDer Mond färbt sich blutrot. Monster sind stärker!"));
            playForWorld(world, Sound.ENTITY_WITHER_SPAWN, 0.7F, 0.65F);
            return;
        }

        Bukkit.broadcastMessage(plugin.messages().raw(Core.prefix + "&4&lBLOODMOON &8| &7Die Nacht verliert ihre Macht."));
        playForWorld(world, Sound.BLOCK_BEACON_DEACTIVATE, 0.6F, 0.75F);
    }

    private void pulse(World world) {
        if (!isActive(world)) {
            return;
        }

        playForWorld(world, Sound.AMBIENT_CAVE, 0.25F, 0.55F);
    }

    private void playForWorld(World world, Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(world)) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }
}
