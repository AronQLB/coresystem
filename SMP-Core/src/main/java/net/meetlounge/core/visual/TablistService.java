package net.meetlounge.core.visual;

import net.meetlounge.core.Core;
import net.meetlounge.core.clan.Clan;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public final class TablistService {

    private final Core plugin;

    public TablistService(Core plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.configs().config().get().getBoolean("visuals.tablist.enabled", true)) {
            plugin.debug().warn("Tablist ist deaktiviert.");
            return;
        }

        int seconds = plugin.configs().config().get().getInt("visuals.tablist.update-interval-seconds", 2);
        long ticks = Math.max(20L, seconds * 20L);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, 20L, ticks);

        plugin.debug().info("TablistService gestartet.");
    }

    public void update(Player player) {
        player.setPlayerListName(displayName(player, player));
        updateNameTags(player);

        List<String> headerLines = plugin.configs().config().get().getStringList("visuals.tablist.header");
        List<String> footerLines = plugin.configs().config().get().getStringList("visuals.tablist.footer");

        if (headerLines.isEmpty()) {
            headerLines = List.of(
                    "&a&lSMP",
                    "&7Willkommen, &f%player%"
            );
        }

        if (footerLines.isEmpty()) {
            footerLines = List.of(
                    "&7Rang: %rank%",
                    "&7Online: &f%online%"
            );
        }

        String header = build(player, headerLines);
        String footer = build(player, footerLines);

        player.setPlayerListHeaderFooter(header, footer);
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    private String build(Player player, List<String> lines) {
        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(apply(player, line)).append("\n");
        }

        return builder.toString();
    }

    private String apply(Player player, String text) {
        PlayerData data = plugin.players().get(player.getUniqueId());
        String rankId = data == null ? "player" : plugin.ranks().getRankId(player);

        double coins = data == null ? 0.0 : data.coins();

        return TextUtil.color(text
                .replace("%player%", player.getName())
                .replace("%rank%", TextUtil.color(plugin.ranks().prefix(rankId)))
                .replace("%coins%", String.valueOf(coins))
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%ping%", String.valueOf(player.getPing()))
                .replace("%level%", String.valueOf(level(player))));
    }

    public void updateNameTags(Player viewer) {
        Scoreboard scoreboard = viewer.getScoreboard();

        for (Player target : Bukkit.getOnlinePlayers()) {
            applyNameTag(scoreboard, viewer, target);
        }
    }

    private void applyNameTag(Scoreboard scoreboard, Player viewer, Player player) {
        String entry = player.getName();
        String teamName = teamName(player);
        Team team = scoreboard.getTeam(teamName);

        for (Team existing : scoreboard.getTeams()) {
            if (existing.getName().startsWith("mln_") && existing.hasEntry(entry) && !existing.getName().equals(teamName)) {
                existing.removeEntry(entry);
            }
        }

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        String rankId = rankId(player);
        String clanTag = clanTag(player);
        int level = level(player);
        String vanishMarker = vanishMarker(viewer, player);

        team.setPrefix(TextUtil.color(vanishMarker + namePrefix(rankId)));
        team.setSuffix(TextUtil.color(clanTag + " &8| &aLevel " + level));

        if (!team.hasEntry(entry)) {
            team.addEntry(entry);
        }
    }

    private String displayName(Player viewer, Player player) {
        return TextUtil.color(vanishMarker(viewer, player)
                + namePrefix(rankId(player))
                + player.getName()
                + clanTag(player)
                + " &8| &aLevel " + level(player));
    }

    private String vanishMarker(Player viewer, Player player) {
        if (!plugin.vanish().isVanished(player) || !plugin.vanish().canSeeVanished(viewer)) {
            return "";
        }

        return "&c&lV &8| ";
    }

    private String clanTag(Player player) {
        Clan clan = plugin.clans().getClan(player).orElse(null);
        return clan == null ? "" : " &8[&a" + clan.tag() + "&8]";
    }

    private String rankId(Player player) {
        PlayerData data = plugin.players().get(player.getUniqueId());
        return data == null ? "player" : plugin.ranks().getRankId(player);
    }

    private String namePrefix(String rankId) {
        String prefix = plugin.ranks().prefix(rankId);
        return prefix.contains("|") ? prefix : prefix + " &8| &7";
    }

    private int level(Player player) {
        PlayerData data = plugin.players().get(player.getUniqueId());
        return data == null ? 1 : data.level();
    }

    private String teamName(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "").substring(0, 8);
        int order = 99 - Math.min(99, Math.max(0, plugin.ranks().weight(rankId(player))));
        return String.format("mln_%02d_%s", order, uuid);
    }
}
