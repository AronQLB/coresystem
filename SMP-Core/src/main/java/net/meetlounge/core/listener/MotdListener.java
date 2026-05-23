package net.meetlounge.core.listener;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public final class MotdListener implements Listener {

    private final Core plugin;

    public MotdListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        String path = plugin.maintenance().isEnabled()
                ? "motd.maintenance"
                : "motd.normal";

        String line1 = plugin.configs().config().get().getString(path + ".line1", "&a&lSMP-Core");
        String line2 = plugin.configs().config().get().getString(path + ".line2", "&7Survival SMP");

        event.setMotd(TextUtil.color(line1 + "\n" + line2));
    }
}
