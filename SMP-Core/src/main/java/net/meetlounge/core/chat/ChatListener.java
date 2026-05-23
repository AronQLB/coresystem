package net.meetlounge.core.chat;
import net.meetlounge.core.Core;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatListener implements Listener {

    private final Core plugin;
    private final Map<UUID, Long> slowChat = new ConcurrentHashMap<>();

    public ChatListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        if (plugin.mutes().chatMuted()
                && !plugin.permissions().has(event.getPlayer(), PermissionNode.CHAT_BYPASS)) {

            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.messages().raw(Core.prefix + "&cDer Chat ist aktuell deaktiviert."));
            return;
        }

        var muteOptional = plugin.mutes().getMute(event.getPlayer());

        if (muteOptional.isPresent()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.mutes().muteMessage(muteOptional.get()));
            return;
        }

        int slow = plugin.mutes().slowChatSeconds();

        if (slow > 0
                && !plugin.permissions().has(event.getPlayer(), PermissionNode.CHAT_BYPASS)) {

            UUID uuid = event.getPlayer().getUniqueId();
            long now = System.currentTimeMillis();
            long next = slowChat.getOrDefault(uuid, 0L);

            if (now < next) {
                long remaining = (long) Math.ceil((next - now) / 1000.0);

                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.messages().raw(
                        Core.prefix + "&cDu musst noch &f" + remaining + " Sekunden &cwarten."
                ));
                return;
            }

            slowChat.put(uuid, now + (slow * 1000L));
        }
    }
}
