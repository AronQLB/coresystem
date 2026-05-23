package net.meetlounge.core.visual;

import net.meetlounge.core.Core;
import net.meetlounge.core.clan.Clan;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.util.TextUtil;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public final class SidebarService {

    private final Core plugin;

    public SidebarService(Core plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.configs().config().get().getBoolean("visuals.sidebar.enabled", true)) {
            plugin.debug().warn("Sidebar ist deaktiviert.");
            return;
        }

        long ticks = 20L;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, 0L, ticks);

        plugin.debug().info("SidebarService gestartet.");
    }

    public void update(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager == null) {
            return;
        }

        Scoreboard scoreboard = manager.getNewScoreboard();

        String title = TextUtil.color(plugin.configs().config().get().getString(
                "visuals.sidebar.title",
                "&a&lSMP"
        ));

        Objective objective = scoreboard.registerNewObjective("smpcore", "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = plugin.configs().config().get().getStringList("visuals.sidebar.lines");

        if (lines.isEmpty()) {
            lines = List.of(
                    "&8&m----------------",
                    "&7Spieler: &f%player%",
                    "&7Rang: %rank%",
                    "&7Online: &f%online%",
                    "&8&m----------------"
            );
        }

        int score = lines.size();

        for (String line : lines) {
            String text = apply(player, line);

            if (text.length() > 40) {
                text = text.substring(0, 40);
            }

            objective.getScore(unique(text, score)).setScore(score);
            score--;
        }

        player.setScoreboard(scoreboard);
        plugin.tablist().updateNameTags(player);
    }

    private String unique(String text, int score) {
        return text + ChatColor.values()[score % ChatColor.values().length];
    }

    private String apply(Player player, String text) {
        PlayerData data = plugin.players().get(player.getUniqueId());
        String rankId = data == null ? "player" : plugin.ranks().getRankId(player);
        Clan clan = safeClan(player);

        long playtime = data == null ? 0L : plugin.players().currentPlaytime(player.getUniqueId());
        double coins = data == null ? 0.0 : data.coins();

        return TextUtil.color(text
                .replace("%player%", player.getName())
                .replace("%ping%", String.valueOf(player.getPing()))
                .replace("%rank%", TextUtil.color(plugin.ranks().prefix(rankId)))
                .replace("%playtime%", TimeUtil.formatDuration(playtime))
                .replace("%coins%", String.valueOf(coins))
                .replace("%level%", data == null ? "1" : String.valueOf(data.level()))
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%clan_name%", clan == null ? "Kein Clan" : clan.name())
                .replace("%clan_owner%", clan == null ? "-" : clan.ownerName())
                .replace("%clan_kills%", clan == null ? "0" : String.valueOf(clan.kills()))
                .replace("%clan_kd%", clan == null ? "0.00" : String.format("%.2f", clan.kd()))
                .replace("%clan_bank%", clan == null ? "0" : String.valueOf(clan.bank()))
        );
    }

    private Clan safeClan(Player player) {
        try {
            return plugin.clans().getClan(player).orElse(null);
        } catch (RuntimeException exception) {
            plugin.debug().error("Clan-Platzhalter konnte nicht geladen werden", exception);
            return null;
        }
    }
}
