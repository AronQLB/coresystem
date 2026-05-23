package net.meetlounge.core.listener;

import net.meetlounge.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public final class FirstJoinListener implements Listener {

    private final Core plugin;

    public FirstJoinListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {

            giveStarterItems(player);

            double coins = plugin.configs().config().get().getDouble("first-join.reward.coins", 1000.0);
            plugin.economy().add(player.getUniqueId(), coins);

            player.sendMessage(plugin.messages().raw(
                    Core.prefix + "&7Du hast &a" + coins + " Coins &7als Starterbelohnung erhalten."
            ));

            Bukkit.broadcastMessage(plugin.messages().raw(
                    Core.prefix + "&7Der Spieler &c"
                            + player.getName()
                            + " &7ist neu auf dem Server."
            ));
        }
    }

    private void giveStarterItems(Player player) {
        if (!plugin.configs().config().get().getBoolean("first-join.starter-items.enabled", true)) {
            return;
        }

        List<String> items = plugin.configs().config().get().getStringList("first-join.starter-items.items");

        for (String entry : items) {
            String[] parts = entry.split(":");

            if (parts.length < 2) {
                plugin.debug().warn("Ungültiges Starter-Item: " + entry);
                continue;
            }

            Material material = Material.matchMaterial(parts[1].toUpperCase(Locale.ROOT));

            if (material == null) {
                plugin.debug().warn("Unbekanntes Starter-Item: " + parts[1]);
                continue;
            }

            int slot = parseInt(parts[0], -1);
            int amount = parts.length >= 3 ? parseInt(parts[2], 1) : 1;

            if (slot < 0 || slot >= player.getInventory().getSize()) {
                plugin.debug().warn("Ungültiger Starter-Item-Slot: " + entry);
                continue;
            }

            player.getInventory().setItem(slot, new ItemStack(material, Math.max(1, amount)));
        }

        player.updateInventory();
    }

    private int parseInt(String text, int fallback) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }
}
