package net.meetlounge.core.grave;

import net.meetlounge.core.Core;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public final class GraveListener implements Listener {

    private static final long REMOVE_AFTER_TICKS = 20L * 60L * 10L;

    private final Core plugin;

    public GraveListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Location location = event.getEntity().getLocation().getBlock().getLocation();

        location.getBlock().setType(Material.CHEST);

        if (!(location.getBlock().getState() instanceof Chest chest)) {
            return;
        }

        for (ItemStack item : event.getDrops()) {
            if (item != null) {
                chest.getInventory().addItem(item);
            }
        }

        event.getDrops().clear();

        event.getEntity().sendMessage(plugin.messages().raw(
                Core.prefix + "&7Dein Grave wurde erstellt bei &fX:"
                        + location.getBlockX()
                        + " Y:" + location.getBlockY()
                        + " Z:" + location.getBlockZ()
                        + "&7. Es verschwindet in &c10 Minuten&7."
        ));

        scheduleRemove(location);
    }

    private void scheduleRemove(Location location) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Block block = location.getBlock();

            if (block.getType() != Material.CHEST) {
                return;
            }

            block.setType(Material.AIR);
        }, REMOVE_AFTER_TICKS);
    }
}
