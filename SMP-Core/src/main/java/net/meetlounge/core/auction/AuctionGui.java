package net.meetlounge.core.auction;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class AuctionGui {

    public static final String TITLE_PREFIX = TextUtil.color("&8Auktionshaus");
    public static final String SELL_TITLE = TextUtil.color("&8Auktion einstellen");

    private final Core plugin;

    public AuctionGui(Core plugin) {
        this.plugin = plugin;
    }

    public Inventory create(int page) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE_PREFIX + TextUtil.color(" &7#" + (page + 1)));
        fillFrame(inventory);

        List<AuctionListing> listings = plugin.auctions().page(page);

        for (int slot = 0; slot < listings.size() && slot < AuctionService.PAGE_SIZE; slot++) {
            inventory.setItem(slot, displayItem(listings.get(slot)));
        }

        inventory.setItem(45, button(Material.ARROW, "&aVorherige Seite", List.of("&7Klicke zum Blättern"), pageAction(page - 1)));
        inventory.setItem(49, button(Material.EMERALD, "&aItem hineinstellen", List.of("&7Lege ein Item hinein", "&7und setze danach den Preis"), "sell"));
        inventory.setItem(53, button(Material.ARROW, "&aNächste Seite", List.of("&7Klicke zum Blättern"), pageAction(page + 1)));

        return inventory;
    }

    public Inventory createSell() {
        Inventory inventory = Bukkit.createInventory(null, 27, SELL_TITLE);
        fillFrame(inventory);
        inventory.setItem(13, null);
        inventory.setItem(22, button(Material.BARRIER, "&cZurück", List.of("&7Zurück zum Auktionshaus"), "back"));
        return inventory;
    }

    public boolean isMainTitle(String title) {
        return title.startsWith(TITLE_PREFIX);
    }

    public int pageFromTitle(String title) {
        int index = title.lastIndexOf("#");

        if (index == -1) {
            return 0;
        }

        try {
            return Math.max(0, Integer.parseInt(title.substring(index + 1)) - 1);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    public NamespacedKey actionKey() {
        return new NamespacedKey(plugin, "auction_action");
    }

    public NamespacedKey idKey() {
        return new NamespacedKey(plugin, "auction_id");
    }

    private ItemStack displayItem(AuctionListing listing) {
        ItemStack item = listing.item().clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = List.of(
                TextUtil.color("&7Verkäufer: &f" + listing.sellerName()),
                TextUtil.color("&7Preis: &a" + listing.price() + " Coins")
        );

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(idKey(), PersistentDataType.INTEGER, listing.id());
        item.setItemMeta(meta);

        return item;
    }

    private void fillFrame(Inventory inventory) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);

        for (int slot = 45; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, filler);
        }
    }

    private ItemStack button(Material material, String name, List<String> lore, String action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(TextUtil.color(name));
        meta.setLore(lore.stream().map(TextUtil::color).toList());
        meta.getPersistentDataContainer().set(actionKey(), PersistentDataType.STRING, action);
        item.setItemMeta(meta);

        return item;
    }

    private String pageAction(int page) {
        return "page:" + Math.max(0, page);
    }
}
