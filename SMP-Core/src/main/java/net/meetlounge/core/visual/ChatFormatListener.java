package net.meetlounge.core.visual;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatFormatListener implements Listener {

    private final Core plugin;

    public ChatFormatListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.configs().config().get().getBoolean("visuals.chat.enabled", true)) {
            return;
        }

        PlayerData data = plugin.players().get(event.getPlayer().getUniqueId());
        String rankId = data == null ? "player" : plugin.ranks().getRankId(event.getPlayer());

        String format = plugin.configs().config().get().getString(
                "visuals.chat.format",
                "%rank% &8| &7%player% &8» &f%message%"
        );

        format = format
                .replace("%rank%", TextUtil.color(plugin.ranks().prefix(rankId)))
                .replace("%player%", event.getPlayer().getName())
                .replace("%message%", "%2$s");

        event.setFormat(TextUtil.color(format));
    }
}
