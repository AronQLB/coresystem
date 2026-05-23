package net.meetlounge.core.config;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageManager {

    private static final String DEFAULT_PREFIX = "&8[&aSMP&8] ";

    private final Core plugin;
    private String prefix = DEFAULT_PREFIX;

    public MessageManager(Core plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.prefix = plugin.configs().messages().get().getString("prefix", DEFAULT_PREFIX);
    }

    public void reload() {
        load();
    }

    public String prefix() {
        return TextUtil.color(prefix);
    }

    public String get(String path) {
        String message = plugin.configs().messages().get().getString(path, "&cMissing message: " + path);

        return TextUtil.color(applyGlobalPlaceholders(message));
    }

    public String get(String path, Player player) {
        String message = get(path);
        return plugin.placeholders().apply(message, player);
    }

    public void send(CommandSender sender, String path) {
        String message = get(path);

        if (sender instanceof Player player) {
            message = plugin.placeholders().apply(message, player);
        }

        sender.sendMessage(message);
    }

    public void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(raw(message));
    }

    public String raw(String message) {
        return TextUtil.color(applyGlobalPlaceholders(message == null ? "" : message));
    }

    public void raw(CommandSender sender, String message) {
        sendRaw(sender, message);
    }

    private String applyGlobalPlaceholders(String message) {
        return message
                .replace("%prefix%", prefix)
                .replace("%server%", plugin.configs().config().get().getString("server.name", "SMP-Core"))
                .replace("%brand%", plugin.configs().config().get().getString("branding.name", "SMP-Core"))
                .replace("%author%", plugin.configs().config().get().getString("branding.author", "YourName"))
                .replace("%website%", plugin.configs().config().get().getString("branding.website", "https://example.com"));
    }
}
