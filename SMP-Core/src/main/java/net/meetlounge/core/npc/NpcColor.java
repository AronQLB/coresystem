package net.meetlounge.core.npc;

import org.bukkit.ChatColor;

public enum NpcColor {

    RED(ChatColor.RED),
    GREEN(ChatColor.GREEN),
    BLUE(ChatColor.AQUA),
    YELLOW(ChatColor.YELLOW),
    PURPLE(ChatColor.LIGHT_PURPLE),
    WHITE(ChatColor.WHITE),
    GOLD(ChatColor.GOLD);

    private final ChatColor color;

    NpcColor(ChatColor color) {
        this.color = color;
    }

    public ChatColor color() {
        return color;
    }
}