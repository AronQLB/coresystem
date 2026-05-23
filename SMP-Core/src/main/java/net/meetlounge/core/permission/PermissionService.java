package net.meetlounge.core.permission;

import net.meetlounge.core.Core;
import net.meetlounge.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public final class PermissionService {

    private static final String RANK_PERMISSIONS_PATH = "permissions.rank-permissions.";
    private static final String CUSTOM_NODES_PATH = "permissions.custom-nodes";

    private final Core plugin;
    private final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<>();

    public PermissionService(Core plugin) {
        this.plugin = plugin;
    }

    public boolean has(CommandSender sender, PermissionNode node) {
        return has(sender, node.node());
    }

    public boolean has(CommandSender sender, String permission) {
        if (sender == null || permission == null || permission.isBlank()) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            return true;
        }

        String normalized = normalize(permission);

        if (sender.hasPermission(normalized) || sender.hasPermission(permission) || sender.hasPermission("meetlounge.*")) {
            return true;
        }

        Rank rank = plugin.ranks().getRank(player);
        PermissionGroup group = PermissionGroup.fromRank(rank);

        return group.has(normalized) || rankPermissions(plugin.ranks().getRankId(player)).contains(normalized);
    }

    public boolean hasRank(Player player, Rank rank) {
        return plugin.ranks().hasAtLeast(player, rank);
    }

    public boolean rankHas(String rankId, PermissionNode node) {
        String normalizedRankId = plugin.ranks().normalizeId(rankId);
        String permission = node.node();
        return PermissionGroup.fromRank(Rank.fromId(normalizedRankId)).has(permission)
                || rankPermissions(normalizedRankId).contains(permission);
    }

    public Rank getRank(Player player) {
        return plugin.ranks().getRank(player);
    }

    public boolean addRankPermission(String rankId, String permission) {
        if (!isRank(rankId)) {
            return false;
        }

        String normalized = normalize(permission);
        Set<String> permissions = new LinkedHashSet<>(rankPermissions(rankId));

        if (!permissions.add(normalized)) {
            return false;
        }

        setRankPermissions(rankId, permissions);
        addCustomNode(normalized);
        applyAll();
        return true;
    }

    public boolean removeRankPermission(String rankId, String permission) {
        if (!isRank(rankId)) {
            return false;
        }

        String normalized = normalize(permission);
        Set<String> permissions = new LinkedHashSet<>(rankPermissions(rankId));

        if (!permissions.remove(normalized)) {
            return false;
        }

        setRankPermissions(rankId, permissions);
        applyAll();
        return true;
    }

    public List<String> rankPermissions(String rankId) {
        if (!isRank(rankId)) {
            return List.of();
        }

        return plugin.configs().config().get()
                .getStringList(RANK_PERMISSIONS_PATH + plugin.ranks().normalizeId(rankId))
                .stream()
                .map(this::normalize)
                .distinct()
                .toList();
    }

    public List<String> rankPermissions(Rank rank) {
        return rankPermissions(rank.id());
    }

    public boolean addCustomNode(String permission) {
        String normalized = normalize(permission);
        Set<String> nodes = new LinkedHashSet<>(customNodes());

        if (!nodes.add(normalized)) {
            return false;
        }

        FileConfiguration config = plugin.configs().config().get();
        config.set(CUSTOM_NODES_PATH, new ArrayList<>(nodes));
        plugin.configs().config().save();
        return true;
    }

    public List<String> customNodes() {
        Set<String> nodes = new LinkedHashSet<>();

        for (PermissionNode node : PermissionNode.values()) {
            nodes.add(node.node());
        }

        nodes.addAll(plugin.configs().config().get().getStringList(CUSTOM_NODES_PATH)
                .stream()
                .map(this::normalize)
                .toList());

        return new ArrayList<>(nodes);
    }

    public String normalize(String permission) {
        if (permission == null) {
            return "";
        }

        String normalized = permission.trim();

        if (normalized.isBlank()) {
            return "";
        }

        if (normalized.regionMatches(true, 0, "meetlounge.", 0, "meetlounge.".length())) {
            return normalized;
        }

        for (PermissionNode node : PermissionNode.values()) {
            String shortNode = node.node().substring("meetlounge.".length());

            if (shortNode.equalsIgnoreCase(normalized)) {
                return node.node();
            }
        }

        return normalized;
    }

    public boolean isRank(String rankId) {
        return plugin.ranks().exists(rankId);
    }

    public void apply(Player player) {
        PermissionAttachment oldAttachment = attachments.remove(player.getUniqueId());

        if (oldAttachment != null) {
            player.removeAttachment(oldAttachment);
        }

        PermissionAttachment attachment = player.addAttachment(plugin);
        Rank rank = plugin.ranks().getRank(player);
        Set<String> permissions = new LinkedHashSet<>(PermissionGroup.fromRank(rank).nodes());
        permissions.addAll(rankPermissions(plugin.ranks().getRankId(player)));

        for (String permission : permissions) {
            if (!permission.isBlank()) {
                attachment.setPermission(permission, true);
            }
        }

        attachments.put(player.getUniqueId(), attachment);
        player.recalculatePermissions();
    }

    public void applyAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            apply(player);
        }
    }

    public void remove(Player player) {
        PermissionAttachment attachment = attachments.remove(player.getUniqueId());

        if (attachment != null) {
            player.removeAttachment(attachment);
            player.recalculatePermissions();
        }
    }

    public void clearAttachments() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            remove(player);
        }

        attachments.clear();
    }

    private void setRankPermissions(String rankId, Set<String> permissions) {
        FileConfiguration config = plugin.configs().config().get();
        String pathRankId = plugin.ranks().normalizeId(rankId);
        config.set(RANK_PERMISSIONS_PATH + pathRankId, new ArrayList<>(permissions));
        plugin.configs().config().save();
    }
}
