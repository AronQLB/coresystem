package net.meetlounge.core.warp;

import net.meetlounge.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class WarpsGuiListener implements Listener {

    private final Core plugin;

    public WarpsGuiListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!event.getView().getTitle().equals(WarpsGui.TITLE)) {
            return;
        }

        event.setCancelled(true);

        for (WarpType type : WarpType.values()) {
            if (event.getSlot() != type.slot()) {
                continue;
            }

            if (!plugin.warps().teleport(player, type.id())) {
                player.sendMessage(plugin.messages().raw("&cDieser Warp wurde noch nicht gesetzt."));
                return;
            }

            player.closeInventory();
            player.sendMessage(plugin.messages().raw(Core.prefix + "&7Du wurdest zu &a" + type.displayName() + " &7teleportiert."));
            return;
        }
    }
}
