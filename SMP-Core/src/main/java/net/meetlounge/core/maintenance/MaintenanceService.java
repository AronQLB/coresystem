package net.meetlounge.core.maintenance;

import net.meetlounge.core.Core;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class MaintenanceService {

    private final Core plugin;
    private boolean enabled;

    public MaintenanceService(Core plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.enabled = plugin.configs().config().get().getBoolean("server.maintenance", false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        plugin.configs().config().get().set("server.maintenance", enabled);
        plugin.configs().config().save();

        if (enabled) {
            kickBlockedPlayers();
        }
    }

    public boolean canJoin(Player player) {
        return !enabled || plugin.permissions().has(player, PermissionNode.MAINTENANCE_BYPASS);
    }

    public boolean canJoin(UUID uuid) {
        if (!enabled) {
            return true;
        }

        return plugin.players().findStored(uuid)
                .map(data -> plugin.permissions().rankHas(data.rank(), PermissionNode.MAINTENANCE_BYPASS))
                .orElse(false);
    }

    public void kickBlockedPlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!canJoin(player)) {
                player.kickPlayer(plugin.messages().get("maintenance-kick"));
            }
        }
    }
}
