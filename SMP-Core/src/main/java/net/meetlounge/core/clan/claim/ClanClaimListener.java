package net.meetlounge.core.clan.claim;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClanClaimListener implements Listener {

    private final Core plugin;
    private final Map<UUID, Integer> lastClaim = new HashMap<>();

    public ClanClaimListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (!plugin.claims().canBuild(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.messages().raw("&cDieser Bereich gehört einem anderen Clan."));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!plugin.claims().canBuild(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.messages().raw("&cDieser Bereich gehört einem anderen Clan."));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        var claimOptional = plugin.claims().claimAt(to);

        if (claimOptional.isEmpty()) {
            lastClaim.remove(player.getUniqueId());
            return;
        }

        ClanClaim claim = claimOptional.get();

        Integer last = lastClaim.get(player.getUniqueId());

        if (last != null && last == claim.clanId()) {
            return;
        }

        lastClaim.put(player.getUniqueId(), claim.clanId());

        player.sendTitle(
                TextUtil.color("&a" + claim.clanName()),
                TextUtil.color("&7Clan-Gebiet"),
                10,
                40,
                10
        );
    }
}
