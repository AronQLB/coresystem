package net.meetlounge.core.staff;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.meetlounge.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Method;
import java.util.Iterator;

public final class VanishListener implements Listener {

    private final Core plugin;

    public VanishListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.vanish().applyVisibility();
            plugin.tablist().refreshAll();
        }, 2L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.vanish().isVanished(event.getPlayer())) {
            event.setQuitMessage(null);
            plugin.vanish().removeOnQuit(event.getPlayer());

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.vanish().applyVisibility();
                plugin.tablist().refreshAll();
            });
        }
    }

    @EventHandler
    public void onServerList(PaperServerListPingEvent event) {
        event.setNumPlayers(Math.max(0,
                event.getNumPlayers() - plugin.vanish().vanishedCount()
        ));
    }
}
