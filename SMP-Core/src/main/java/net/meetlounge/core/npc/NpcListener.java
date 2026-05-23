package net.meetlounge.core.npc;

import net.meetlounge.core.Core;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class NpcListener implements Listener {

    private final Core plugin;

    public NpcListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {

        Entity entity = event.getRightClicked();

        if (!plugin.npcs().isNpc(entity)) {
            return;
        }

        Player player = event.getPlayer();

        plugin.npcs().execute(player, entity.getUniqueId());

        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();

        if (!plugin.npcs().isNpc(entity)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        plugin.npcs().execute(player, entity.getUniqueId());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof LivingEntity entity) || !plugin.npcs().isNpc(entity)) {
            return;
        }

        event.setCancelled(true);
    }
}
