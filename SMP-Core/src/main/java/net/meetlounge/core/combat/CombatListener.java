package net.meetlounge.core.combat;

import net.meetlounge.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class CombatListener implements Listener {

    private final Core plugin;

    public CombatListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        long duration = plugin.configs().config().get().getInt("combat.duration-seconds", 15) * 1000L;

        plugin.combat().tag(victim.getUniqueId(), duration);
        plugin.combat().tag(attacker.getUniqueId(), duration);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.combat().inCombat(event.getPlayer().getUniqueId())) {
            return;
        }

        event.getPlayer().setHealth(0.0);
        plugin.combat().clear(event.getPlayer().getUniqueId());
    }
}