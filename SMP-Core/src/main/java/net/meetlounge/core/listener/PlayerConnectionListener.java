package net.meetlounge.core.listener;

import net.meetlounge.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerConnectionListener implements Listener {

    private final Core plugin;

    public PlayerConnectionListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.maintenance().canJoin(event.getUniqueId())) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    plugin.messages().get("maintenance-kick")
            );
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.players().load(event.getPlayer());

        if (plugin.configs().config().get().getBoolean("server.join-message-enabled", true)) {
            String message = plugin.messages().get("join-message")
                    .replace("%player%", event.getPlayer().getName());

            event.setJoinMessage(message);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.permissions().remove(event.getPlayer());
        plugin.players().unload(event.getPlayer());
        plugin.cooldowns().clear(event.getPlayer().getUniqueId());

        if (plugin.configs().config().get().getBoolean("server.quit-message-enabled", true)) {
            String message = plugin.messages().get("quit-message")
                    .replace("%player%", event.getPlayer().getName());

            event.setQuitMessage(message);
        }
    }
}
