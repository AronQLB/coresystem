package net.meetlounge.core.report;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ReportsGui {

    public static final String TITLE = TextUtil.color("&cOffene Reports");

    private final Core plugin;

    public ReportsGui(Core plugin) {
        this.plugin = plugin;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        for (ReportData report : plugin.reports().openReports()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(TextUtil.color("&cReport #" + report.id()));
            meta.setLore(List.of(
                    TextUtil.color("&7Reporter: &f" + report.reporterName()),
                    TextUtil.color("&7Gegen: &f" + report.targetName()),
                    TextUtil.color("&7Grund: &c" + report.reason()),
                    TextUtil.color("&7Zeit: &f" + TimeUtil.formatDateTime(report.createdAt())),
                    "",
                    TextUtil.color("&aLinksklick: Teleportieren"),
                    TextUtil.color("&cRechtsklick: Schließen")
            ));

            item.setItemMeta(meta);
            inventory.addItem(item);
        }

        return inventory;
    }
}