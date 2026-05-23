package net.meetlounge.core.staff;

import net.meetlounge.core.Core;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishService {

    private final Core plugin;
    private final Set<UUID> vanished = new HashSet<>();

    public VanishService(Core plugin) {
        this.plugin = plugin;
    }

    public boolean toggle(Player player) {
        if (isVanished(player)) {
            show(player);
            return false;
        }

        hide(player);
        return true;
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public int vanishedCount() {
        return vanished.size();
    }

    public void hide(Player player) {
        vanished.add(player.getUniqueId());
        player.setInvisible(true);
        player.setCollidable(false);
        applyVisibility();
        plugin.tablist().refreshAll();
    }

    public void show(Player player) {
        vanished.remove(player.getUniqueId());
        player.setInvisible(false);
        player.setCollidable(true);
        applyVisibility();
        plugin.tablist().refreshAll();
    }

    public void removeOnQuit(Player player) {
        vanished.remove(player.getUniqueId());
        player.setInvisible(false);
        player.setCollidable(true);
    }

    public void applyVisibility() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (viewer.equals(target)) {
                    continue;
                }

                if (isVanished(target) && !canSeeVanished(viewer)) {
                    viewer.hidePlayer(plugin, target);
                } else {
                    viewer.showPlayer(plugin, target);
                }
            }
        }
    }

    public boolean canSeeVanished(Player player) {
        return plugin.permissions().has(player, PermissionNode.VANISH_SEE);
    }
}
