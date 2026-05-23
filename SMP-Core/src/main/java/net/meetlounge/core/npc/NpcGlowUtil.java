package net.meetlounge.core.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class NpcGlowUtil {

    public static void apply(Entity entity, NpcColor color) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String name = "npc_" + color.name().toLowerCase();

        Team team = scoreboard.getTeam(name);

        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        team.setColor(color.color());
        team.addEntry(entity.getUniqueId().toString());
    }
}