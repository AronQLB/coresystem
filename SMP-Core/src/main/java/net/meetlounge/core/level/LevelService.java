package net.meetlounge.core.level;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;
import java.util.UUID;

public final class LevelService {

    private final Core plugin;
    private final Random random = new Random();
    private boolean spawningLevelMob;

    public LevelService(Core plugin) {
        this.plugin = plugin;
    }

    public void startZombieSpawner() {
        if (!plugin.configs().config().get().getBoolean("level.zombie-spawner.enabled", true)) {
            return;
        }

        int seconds = plugin.configs().config().get().getInt("level.zombie-spawner.interval-seconds", 20);
        long ticks = Math.max(20L, seconds * 20L);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                spawnZombiesFor(player);
            }
        }, ticks, ticks);
    }

    public long requiredXp(int level) {
        return Math.round(250 + (level * level * 85L));
    }

    public void addXp(Player player, long amount) {
        PlayerData data = plugin.players().data(player.getUniqueId());

        if (data == null) {
            return;
        }

        data.addXp(amount);

        boolean leveledUp = false;

        while (data.xp() >= requiredXp(data.level())) {
            data.setXp(data.xp() - requiredXp(data.level()));
            data.setLevel(data.level() + 1);
            leveledUp = true;
        }

        plugin.players().save(data);
        plugin.tablist().refreshAll();

        if (leveledUp) {
            player.sendTitle(
                    TextUtil.color("&a&lLEVEL UP"),
                    TextUtil.color("&7Du bist jetzt Level &a" + data.level()),
                    10,
                    40,
                    10
            );
        }
    }

    public void removeXp(Player player, long amount) {
        PlayerData data = plugin.players().data(player.getUniqueId());

        if (data == null) {
            return;
        }

        long remainingLoss = amount;

        while (remainingLoss > 0) {
            if (data.xp() >= remainingLoss) {
                data.removeXp(remainingLoss);
                remainingLoss = 0;
            } else {
                remainingLoss -= data.xp();

                if (data.level() > 1) {
                    data.setLevel(data.level() - 1);
                    data.setXp(requiredXp(data.level()) / 2);
                } else {
                    data.setXp(0);
                    remainingLoss = 0;
                }
            }
        }

        plugin.players().save(data);
        plugin.tablist().refreshAll();
    }

    public double mobHealthMultiplier(int playerLevel) {
        return 1.0 + (playerLevel * 0.08);
    }

    public double mobDamageMultiplier(int playerLevel) {
        return 1.0 + (playerLevel * 0.05);
    }

    public int playerLevel(Player player) {
        PlayerData data = plugin.players().data(player.getUniqueId());
        return data == null ? 1 : data.level();
    }

    public void applyMobLevel(Monster monster, int level) {
        int mobLevel = Math.max(1, level);
        double healthMultiplier = mobHealthMultiplier(mobLevel);

        Attribute maxHealth = attribute("MAX_HEALTH", "GENERIC_MAX_HEALTH");

        if (maxHealth != null) {
            var attribute = monster.getAttribute(maxHealth);

            if (attribute != null) {
                double newHealth = attribute.getBaseValue() * healthMultiplier;
                attribute.setBaseValue(newHealth);
                monster.setHealth(newHealth);
            }
        }

        monster.getPersistentDataContainer().set(
                key("mob_level"),
                PersistentDataType.INTEGER,
                mobLevel
        );

        monster.setCustomName(TextUtil.color("&cLv." + mobLevel + " &7" + monster.getType().name()));
        monster.setCustomNameVisible(false);
    }

    public int mobLevel(Monster monster) {
        Integer level = monster.getPersistentDataContainer().get(key("mob_level"), PersistentDataType.INTEGER);
        return level == null ? 1 : Math.max(1, level);
    }

    public UUID mobOwner(Monster monster) {
        String owner = monster.getPersistentDataContainer().get(key("mob_owner"), PersistentDataType.STRING);

        if (owner == null) {
            return null;
        }

        try {
            return UUID.fromString(owner);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void spawnZombiesFor(Player player) {
        if (player.isDead() || !player.isValid()) {
            return;
        }

        if (isInProtectedSpawn(player.getLocation())) {
            return;
        }

        int maxZombies = plugin.configs().config().get().getInt("level.zombie-spawner.max-per-player", 4);

        if (ownedZombies(player.getUniqueId()) >= maxZombies) {
            return;
        }

        Location location = findSpawnLocation(player);

        if (location == null) {
            return;
        }

        Zombie zombie;
        spawningLevelMob = true;

        try {
            zombie = location.getWorld().spawn(location, Zombie.class);
        } finally {
            spawningLevelMob = false;
        }

        zombie.getPersistentDataContainer().set(
                key("mob_owner"),
                PersistentDataType.STRING,
                player.getUniqueId().toString()
        );
        zombie.setTarget(player);
        applyMobLevel(zombie, plugin.bloodMoon().boostedMobLevel(zombie.getWorld(), playerLevel(player)));
    }

    public boolean isSpawningLevelMob() {
        return spawningLevelMob;
    }

    private int ownedZombies(UUID owner) {
        int count = 0;

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Zombie zombie) || !zombie.isValid() || zombie.isDead()) {
                    continue;
                }

                String mobOwner = zombie.getPersistentDataContainer().get(key("mob_owner"), PersistentDataType.STRING);

                if (owner.toString().equals(mobOwner)) {
                    count++;
                }
            }
        }

        return count;
    }

    private Location findSpawnLocation(Player player) {
        World world = player.getWorld();
        int minDistance = plugin.configs().config().get().getInt("level.zombie-spawner.min-distance", 18);
        int maxDistance = plugin.configs().config().get().getInt("level.zombie-spawner.max-distance", 32);

        maxDistance = Math.max(minDistance, maxDistance);

        for (int attempt = 0; attempt < 20; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = minDistance + (random.nextDouble() * (maxDistance - minDistance));
            int x = player.getLocation().getBlockX() + (int) Math.round(Math.cos(angle) * distance);
            int z = player.getLocation().getBlockZ() + (int) Math.round(Math.sin(angle) * distance);
            int y = world.getHighestBlockYAt(x, z) + 1;
            Location location = new Location(world, x + 0.5, y, z + 0.5);

            if (isInProtectedSpawn(location)) {
                continue;
            }

            Material ground = world.getBlockAt(x, y - 1, z).getType();
            Material feet = world.getBlockAt(x, y, z).getType();
            Material head = world.getBlockAt(x, y + 1, z).getType();

            if (ground.isAir() || ground == Material.WATER || ground == Material.LAVA) {
                continue;
            }

            if (!feet.isAir() || !head.isAir()) {
                continue;
            }

            return location;
        }

        return null;
    }

    private boolean isInProtectedSpawn(Location location) {
        Location spawn = plugin.spawns().getSpawn();

        if (spawn == null || spawn.getWorld() == null || location.getWorld() == null || !spawn.getWorld().equals(location.getWorld())) {
            return false;
        }

        int radius = plugin.configs().config().get().getInt("rtp.protected-spawn-radius", 10000);
        return location.distanceSquared(spawn) <= (double) radius * radius;
    }

    private NamespacedKey key(String value) {
        return new NamespacedKey(plugin, value);
    }

    private Attribute attribute(String... names) {
        for (String name : names) {
            try {
                return (Attribute) Attribute.class.getField(name).get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return null;
    }
}
