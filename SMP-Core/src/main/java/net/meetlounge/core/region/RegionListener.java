package net.meetlounge.core.region;

import net.meetlounge.core.Core;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class RegionListener implements Listener {

    private final Core plugin;

    public RegionListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (!plugin.regions().allowed(event.getPlayer(), event.getBlock().getLocation(), RegionFlag.BUILD)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.messages().raw("&cDu darfst hier nicht bauen."));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!plugin.regions().allowed(event.getPlayer(), event.getBlock().getLocation(), RegionFlag.BREAK)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.messages().raw("&cDu darfst hier nicht abbauen."));
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (!plugin.regions().allowed(attacker, victim.getLocation(), RegionFlag.PVP)) {
            event.setCancelled(true);
            attacker.sendMessage(plugin.messages().raw("&cPvP ist hier deaktiviert."));
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Monster
                || event.getEntity() instanceof Animals
                || event.getEntity() instanceof Slime
                || event.getEntity() instanceof Ambient)) {
            return;
        }

        boolean allowed = plugin.regions()
                .regionAt(event.getLocation())
                .map(region -> region.flag(RegionFlag.MOB_SPAWN))
                .orElse(true);

        if (!allowed) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCropTrample(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.PHYSICAL) return;

        Material type = event.getClickedBlock().getType();

        if (type != Material.FARMLAND) {
            return;
        }

        if (!plugin.regions().allowed(event.getPlayer(), event.getClickedBlock().getLocation(), RegionFlag.CROP_TRAMPLE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if (!plugin.regions().allowed(
                player,
                player.getLocation(),
                RegionFlag.FALL_DAMAGE
        )) {
            event.setCancelled(true);
        }
    }
}
