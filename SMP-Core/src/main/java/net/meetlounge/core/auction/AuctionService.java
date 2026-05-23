package net.meetlounge.core.auction;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AuctionService {

    public static final int PAGE_SIZE = 45;
    public static final long EXPIRE_AFTER_MILLIS = 7L * 24L * 60L * 60L * 1000L;
    public static final long REMINDER_BEFORE_MILLIS = 24L * 60L * 60L * 1000L;

    private final Core plugin;
    private final AuctionRepository repository;
    private final Map<UUID, ItemStack> pendingPrice = new ConcurrentHashMap<>();

    public AuctionService(Core plugin, AuctionRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public List<AuctionListing> page(int page) {
        return repository.findPage(page, PAGE_SIZE);
    }

    public void requestPrice(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        pendingPrice.put(player.getUniqueId(), item.clone());
        player.closeInventory();
        player.sendMessage(plugin.messages().raw(Core.prefix + "&7Schreibe den Preis in den Chat. &8(&cAbbrechen: cancel&8)"));
    }

    public boolean hasPendingPrice(Player player) {
        return pendingPrice.containsKey(player.getUniqueId());
    }

    public void handlePriceInput(Player player, String message) {
        ItemStack item = pendingPrice.get(player.getUniqueId());

        if (item == null) {
            return;
        }

        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("abbrechen")) {
            pendingPrice.remove(player.getUniqueId());
            giveBack(player, item);
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cAuktion abgebrochen."));
            return;
        }

        double price;

        try {
            price = Double.parseDouble(message.replace(",", "."));
        } catch (NumberFormatException exception) {
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cBitte gib eine gültige Zahl ein."));
            return;
        }

        if (price <= 0) {
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cDer Preis muss größer als 0 sein."));
            return;
        }

        pendingPrice.remove(player.getUniqueId());
        long now = System.currentTimeMillis();
        if (!repository.create(player.getUniqueId(), player.getName(), item, price, now, now + EXPIRE_AFTER_MILLIS)) {
            giveBack(player, item);
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cAuktion konnte nicht gespeichert werden. Du hast dein Item zurückbekommen."));
            return;
        }

        player.sendMessage(plugin.messages().raw(Core.prefix + "&aItem wurde für &f" + price + " Coins &ains Auktionshaus gestellt."));
    }

    public void cancelPending(Player player) {
        ItemStack item = pendingPrice.remove(player.getUniqueId());

        if (item != null) {
            giveBack(player, item);
        }
    }

    public void buy(Player buyer, int id) {
        AuctionListing listing = repository.findById(id).orElse(null);

        if (listing == null) {
            buyer.sendMessage(plugin.messages().raw(Core.prefix + "&cDiese Auktion existiert nicht mehr."));
            return;
        }

        if (listing.sellerUuid().equals(buyer.getUniqueId())) {
            buyer.sendMessage(plugin.messages().raw(Core.prefix + "&cDu kannst dein eigenes Item nicht kaufen. Rechtsklick zum Zurücknehmen."));
            return;
        }

        if (buyer.getInventory().firstEmpty() == -1) {
            buyer.sendMessage(plugin.messages().raw(Core.prefix + "&cDein Inventar ist voll."));
            return;
        }

        PlayerData sellerData = findPlayerData(listing.sellerUuid());

        if (sellerData == null) {
            buyer.sendMessage(plugin.messages().raw(Core.prefix + "&cDer Verkäufer konnte nicht ausgezahlt werden. Kauf abgebrochen."));
            return;
        }

        if (!plugin.economy().remove(buyer.getUniqueId(), listing.price())) {
            buyer.sendMessage(plugin.messages().raw(Core.prefix + "&cDu hast nicht genug Coins."));
            return;
        }

        if (!repository.delete(id)) {
            plugin.economy().add(buyer.getUniqueId(), listing.price());
            buyer.sendMessage(plugin.messages().raw(Core.prefix + "&cDiese Auktion wurde gerade schon verarbeitet."));
            return;
        }

        buyer.getInventory().addItem(listing.item().clone());
        addCoins(sellerData, listing.price());

        Player seller = Bukkit.getPlayer(listing.sellerUuid());

        if (seller != null) {
            seller.sendMessage(plugin.messages().raw(Core.prefix + "&aDein Item wurde für &f" + listing.price() + " Coins &averkauft."));
        }

        buyer.sendMessage(plugin.messages().raw(Core.prefix + "&aDu hast das Item für &f" + listing.price() + " Coins &agekauft."));
    }

    public void takeBack(Player player, int id) {
        AuctionListing listing = repository.findById(id).orElse(null);

        if (listing == null) {
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cDiese Auktion existiert nicht mehr."));
            return;
        }

        if (!listing.sellerUuid().equals(player.getUniqueId())) {
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cDu kannst nur deine eigenen Items zurücknehmen."));
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cDein Inventar ist voll."));
            return;
        }

        if (!repository.delete(id)) {
            player.sendMessage(plugin.messages().raw(Core.prefix + "&cDiese Auktion wurde gerade schon verarbeitet."));
            return;
        }

        player.getInventory().addItem(listing.item().clone());
        player.sendMessage(plugin.messages().raw(Core.prefix + "&aItem wurde aus dem Auktionshaus genommen."));
    }

    public void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanupAndRemind, 20L * 60L, 20L * 60L * 30L);
    }

    private void cleanupAndRemind() {
        long now = System.currentTimeMillis();

        for (AuctionListing listing : repository.findReminders(now)) {
            Player seller = Bukkit.getPlayer(listing.sellerUuid());

            if (seller != null) {
                seller.sendMessage(plugin.messages().raw(Core.prefix + "&eDein Auktions-Item läuft bald ab. Nimm es mit Rechtsklick im Auktionshaus zurück."));
                repository.markReminderSent(listing.id());
            }
        }

        repository.deleteExpired(now);
    }

    private PlayerData findPlayerData(UUID uuid) {
        PlayerData data = plugin.players().get(uuid);

        if (data == null) {
            data = plugin.playerDataRepository().findByUuid(uuid).orElse(null);
        }

        return data;
    }

    private void addCoins(PlayerData data, double amount) {
        data.setCoins(data.coins() + amount);
        plugin.playerDataRepository().save(data);
    }

    private void giveBack(Player player, ItemStack item) {
        Map<Integer, ItemStack> remaining = player.getInventory().addItem(item);

        for (ItemStack leftover : remaining.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }
}
