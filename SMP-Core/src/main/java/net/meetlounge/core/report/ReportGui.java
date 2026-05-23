package net.meetlounge.core.report;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public final class ReportGui {

    public static final String TITLE = TextUtil.color("&cReport Grund auswählen");

    private final Core plugin;

    public ReportGui(Core plugin) {
        this.plugin = plugin;
    }

    public void open(Player reporter, Player target) {
        plugin.reports().session().setTarget(reporter.getUniqueId(), target.getUniqueId());

        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);

        int slot = 10;

        for (ReportReason reason : ReportReason.values()) {
            ItemStack item = new ItemStack(reason.icon());
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(TextUtil.color("&c" + reason.displayName()));
            meta.setLore(List.of(
                    TextUtil.color("&7Spieler: &f" + target.getName()),
                    TextUtil.color("&7Klicke zum Reporten")
            ));

            item.setItemMeta(meta);
            inventory.setItem(slot, item);

            slot++;
            if (slot == 17) {
                break;
            }
        }

        reporter.openInventory(inventory);
    }
}