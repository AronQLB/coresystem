package net.meetlounge.core.warp;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class WarpsGui {

    public static final String TITLE = TextUtil.color("&8Warps");

    private final Core plugin;

    public WarpsGui(Core plugin) {
        this.plugin = plugin;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }

        for (WarpType type : WarpType.values()) {
            boolean set = plugin.warps().exists(type.id());

            ItemStack item = new ItemStack(type.icon());
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(TextUtil.color(type.displayName()));
            meta.setLore(List.of(
                    TextUtil.color(set ? "&7Status: &aGesetzt" : "&7Status: &cNicht gesetzt"),
                    "",
                    TextUtil.color("&7Klicke zum Teleportieren")
            ));

            item.setItemMeta(meta);
            inventory.setItem(type.slot(), item);
        }

        return inventory;
    }
}