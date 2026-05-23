package net.meetlounge.core.command;

import net.meetlounge.core.Core;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandContext {

    private final Core plugin;
    private final CommandSender sender;
    private final String label;
    private final String[] args;

    public CommandContext(Core plugin, CommandSender sender, String label, String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    public CommandSender sender() {
        return sender;
    }

    public String label() {
        return label;
    }

    public String[] args() {
        return args;
    }

    public int length() {
        return args.length;
    }

    public String arg(int index) {
        if (index < 0 || index >= args.length) {
            return "";
        }

        return args[index];
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player player() {
        if (!(sender instanceof Player player)) {
            throw new IllegalStateException("CommandSender ist kein Spieler.");
        }

        return player;
    }

    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    public void reply(String messagePath) {
        plugin.messages().send(sender, messagePath);
    }

    public void raw(String message) {
        plugin.messages().sendRaw(sender, message);
    }

    public String joined(int startIndex) {

        if (startIndex >= args.length) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < args.length; i++) {

            builder.append(args[i]);

            if (i + 1 < args.length) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }
}