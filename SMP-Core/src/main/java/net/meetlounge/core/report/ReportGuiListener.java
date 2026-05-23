package net.meetlounge.core.report;

import net.meetlounge.core.Core;
import net.meetlounge.core.permission.PermissionNode;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public final class ReportGuiListener implements Listener {

    private final Core plugin;

    public ReportGuiListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = event.getView().getTitle();

        if (title.equals(ReportGui.TITLE)) {
            handleReportReasonClick(event, player);
            return;
        }

        if (title.equals(ReportsGui.TITLE)) {
            handleReportsClick(event, player);
        }
    }

    private void handleReportReasonClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        UUID targetUuid = plugin.reports().session().getTarget(player.getUniqueId());

        if (targetUuid == null) {
            player.closeInventory();
            player.sendMessage(plugin.messages().raw("&cReport-Ziel nicht gefunden."));
            return;
        }

        Player target = Bukkit.getPlayer(targetUuid);

        if (target == null) {
            player.closeInventory();
            player.sendMessage(plugin.messages().raw("&cDieser Spieler ist nicht mehr online."));
            return;
        }

        String clickedName = event.getCurrentItem().getItemMeta().getDisplayName();

        for (ReportReason reason : ReportReason.values()) {
            if (clickedName.equals(TextUtil.color("&c" + reason.displayName()))) {
                int reportId = plugin.reports().createReport(player, target, reason);

                player.closeInventory();
                plugin.reports().session().clear(player.getUniqueId());

                if (reportId == -1) {
                    player.sendMessage(plugin.messages().raw("&cReport konnte nicht erstellt werden."));
                    return;
                }

                player.sendMessage(plugin.messages().raw("&aDein Report wurde erstellt. &7ID: &f#" + reportId));
                return;
            }
        }
    }

    private void handleReportsClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        if (!plugin.permissions().has(player, PermissionNode.REPORT_STAFF)) {
            player.closeInventory();
            return;
        }

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String name = event.getCurrentItem().getItemMeta().getDisplayName();

        if (!name.contains("#")) {
            return;
        }

        int id;

        try {
            id = Integer.parseInt(name.split("#")[1].replaceAll("[^0-9]", ""));
        } catch (NumberFormatException exception) {
            return;
        }

        ReportData report = plugin.reports().openReports()
                .stream()
                .filter(data -> data.id() == id)
                .findFirst()
                .orElse(null);

        if (report == null) {
            player.sendMessage(plugin.messages().raw("&cReport nicht gefunden."));
            return;
        }

        if (event.getClick() == ClickType.RIGHT) {
            plugin.reports().closeReport(id);
            player.openInventory(new ReportsGui(plugin).create());
            player.sendMessage(plugin.messages().raw("&aReport #" + id + " wurde geschlossen."));
            return;
        }

        Player target = Bukkit.getPlayer(report.targetUuid());

        if (target == null) {
            player.sendMessage(plugin.messages().raw("&cDer Spieler ist nicht online."));
            return;
        }

        player.teleport(target);
        player.closeInventory();
        player.sendMessage(plugin.messages().raw("&aDu wurdest zu &f" + target.getName() + " &ateleportiert."));
    }
}
