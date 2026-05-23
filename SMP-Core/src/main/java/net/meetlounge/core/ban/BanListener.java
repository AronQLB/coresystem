package net.meetlounge.core.ban;

import net.meetlounge.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;
import java.util.UUID;

public final class BanListener implements Listener {

    private final Core plugin;

    public BanListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Optional<BanData> banOptional = plugin.bans().getActiveBan(uuid);

        if (banOptional.isEmpty()) {
            return;
        }

        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                plugin.bans().buildKickMessage(banOptional.get())
        );
    }
}