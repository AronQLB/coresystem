package net.meetlounge.core.auction;

import net.meetlounge.core.Core;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class AuctionListener implements Listener {

    private final Core plugin;

    public AuctionListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMainClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        AuctionGui gui = new AuctionGui(plugin);

        if (!gui.isMainTitle(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        String action = meta.getPersistentDataContainer().get(gui.actionKey(), PersistentDataType.STRING);

        if (action != null) {
            if (action.equals("sell")) {
                player.openInventory(gui.createSell());
                return;
            }

            if (action.startsWith("page:")) {
                int page = Integer.parseInt(action.substring("page:".length()));
                player.openInventory(gui.create(page));
                return;
            }
        }

        Integer id = meta.getPersistentDataContainer().get(gui.idKey(), PersistentDataType.INTEGER);

        if (id == null) {
            return;
        }

        if (event.isRightClick()) {
            plugin.auctions().takeBack(player, id);
        } else {
            plugin.auctions().buy(player, id);
        }

        player.openInventory(gui.create(gui.pageFromTitle(event.getView().getTitle())));
    }

    @EventHandler
    public void onSellClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!event.getView().getTitle().equals(AuctionGui.SELL_TITLE)) {
            return;
        }

        AuctionGui gui = new AuctionGui(plugin);

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                startPriceInput(event.getCurrentItem(), player);
            }
            return;
        }

        if (event.getSlot() == 22) {
            event.setCancelled(true);
            player.openInventory(gui.create(0));
            return;
        }

        if (event.getSlot() != 13) {
            event.setCancelled(true);
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (cursor != null && cursor.getType() != Material.AIR) {
            event.setCancelled(true);
            plugin.auctions().requestPrice(player, cursor.clone());
            cursor.setAmount(0);
            return;
        }

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onSellClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!event.getView().getTitle().equals(AuctionGui.SELL_TITLE)) {
            return;
        }

        if (plugin.auctions().hasPendingPrice(player)) {
            return;
        }

        ItemStack item = event.getInventory().getItem(13);

        if (item != null && item.getType() != Material.AIR) {
            player.getInventory().addItem(item).values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
        }
    }

    @EventHandler
    public void onSellDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(AuctionGui.SELL_TITLE)) {
            return;
        }

        if (event.getRawSlots().contains(13)) {
            event.setCancelled(true);
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize() && slot != 13) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.auctions().hasPendingPrice(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.auctions().handlePriceInput(event.getPlayer(), event.getMessage()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.auctions().cancelPending(event.getPlayer());
    }

    private void startPriceInput(ItemStack item, Player player) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        plugin.auctions().requestPrice(player, item.clone());
        item.setAmount(0);
    }
}
