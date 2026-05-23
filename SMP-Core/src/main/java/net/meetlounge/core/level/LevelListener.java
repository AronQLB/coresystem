package net.meetlounge.core.level;

import net.meetlounge.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.UUID;

public final class LevelListener implements Listener {

    private final Core plugin;

    public LevelListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        plugin.levels().removeXp(victim, 180);

        victim.sendMessage(plugin.messages().raw(
                Core.prefix + "&cDu hast &f180 XP &cverloren."
        ));

        if (killer == null || killer.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        plugin.levels().addXp(killer, 350);
        plugin.economy().add(killer.getUniqueId(), 750);

        killer.sendMessage(plugin.messages().raw(
                Core.prefix + "&7Du hast &a350 XP &7und &a750 Coins &7für den Kill erhalten."
        ));

        Bukkit.broadcastMessage(plugin.messages().raw(
                Core.prefix + "&c" + victim.getName()
                        + " &7wurde von &a" + killer.getName()
                        + " &7getötet."
        ));
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) {
            return;
        }

        if (entity instanceof Player) {
            return;
        }

        long xp = Math.round(xpForEntity(entity) * plugin.bloodMoon().xpMultiplier(entity.getWorld()));

        if (xp <= 0) {
            return;
        }

        plugin.levels().addXp(killer, xp);
        long coins = Math.round(coinsForEntity(entity) * plugin.bloodMoon().coinMultiplier(entity.getWorld()));

        if (coins > 0) {
            plugin.economy().add(killer.getUniqueId(), coins);
        }

        killer.sendMessage(plugin.messages().raw(
                Core.prefix + "&7Du hast &a" + xp + " XP &7und &a" + coins + " Coins &7erhalten."
        ));
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (plugin.levels().isSpawningLevelMob()) {
            return;
        }

        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        Player nearest = nearestPlayer(monster);
        int level = nearest == null ? 1 : plugin.levels().playerLevel(nearest);
        level = plugin.bloodMoon().boostedMobLevel(monster.getWorld(), level);

        plugin.levels().applyMobLevel(monster, level);
    }

    @EventHandler
    public void onMobDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Monster monster)) {
            return;
        }

        double multiplier = plugin.levels().mobDamageMultiplier(plugin.levels().mobLevel(monster))
                * plugin.bloodMoon().damageMultiplier(monster.getWorld());

        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        UUID ownerUuid = plugin.levels().mobOwner(monster);

        if (ownerUuid == null) {
            return;
        }

        Player owner = Bukkit.getPlayer(ownerUuid);

        if (owner == null || !owner.isOnline() || !owner.getWorld().equals(monster.getWorld())) {
            monster.remove();
            return;
        }

        if (event.getTarget() == null || event.getTarget().getUniqueId().equals(ownerUuid)) {
            return;
        }

        event.setTarget(owner);
    }

    private long xpForEntity(LivingEntity entity) {
        if (entity instanceof Monster) {
            return switch (entity.getType()) {
                case ZOMBIE, SKELETON, SPIDER, CREEPER -> 35;
                case ENDERMAN -> 70;
                case BLAZE, WITCH, PILLAGER -> 85;
                case WITHER_SKELETON, EVOKER, VINDICATOR -> 120;
                case WARDEN -> 1000;
                default -> 30;
            };
        }

        if (entity instanceof Animals) {
            return 8;
        }

        if (entity instanceof Slime) {
            return 15;
        }

        return 5;
    }

    private long coinsForEntity(LivingEntity entity) {
        if (entity instanceof Monster) {
            return switch (entity.getType()) {
                case ZOMBIE, SKELETON, SPIDER, CREEPER -> 18;
                case ENDERMAN -> 35;
                case BLAZE, WITCH, PILLAGER -> 45;
                case WITHER_SKELETON, EVOKER, VINDICATOR -> 70;
                case WARDEN -> 500;
                default -> 15;
            };
        }

        if (entity instanceof Animals) {
            return 3;
        }

        if (entity instanceof Slime) {
            return 8;
        }

        return 2;
    }

    private Player nearestPlayer(LivingEntity entity) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(entity.getWorld())) {
                continue;
            }

            double distance = player.getLocation().distanceSquared(entity.getLocation());

            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }

        return nearest;
    }
}
