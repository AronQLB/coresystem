package net.meetlounge.core.clan;

import net.meetlounge.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class ClanKillDeathListener implements Listener {

    private final Core plugin;

    public ClanKillDeathListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        plugin.clans().addDeath(victim);

        if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            plugin.clans().addKill(killer);
        }
    }
}