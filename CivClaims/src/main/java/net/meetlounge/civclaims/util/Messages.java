package net.meetlounge.civclaims.util;

import org.bukkit.ChatColor;

public final class Messages {

    public static final String PREFIX = ChatColor.GOLD + "CivClaims " + ChatColor.DARK_GRAY + "» " + ChatColor.WHITE;

    private Messages() {
    }

    public static String text(String message) {
        return PREFIX + message;
    }

}
