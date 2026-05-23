package net.meetlounge.core.debug;

import net.meetlounge.core.Core;

public final class DebugLogger {

    private final Core plugin;

    public DebugLogger(Core plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.configs().config().get().getBoolean("debug", false);
    }

    public void debug(String message) {
        if (isEnabled()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public void info(String message) {
        plugin.getLogger().info("[INFO] " + message);
    }

    public void warn(String message) {
        plugin.getLogger().warning("[WARN] " + message);
    }

    public void error(String message) {
        plugin.getLogger().severe("[ERROR] " + message);
    }

    public void error(String message, Throwable throwable) {
        plugin.getLogger().severe("[ERROR] " + message);
        plugin.getLogger().severe("[ERROR] " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
    }

    public void startup(String system) {
        info(system + " geladen.");
    }

    public void shutdown(String system) {
        info(system + " gestoppt.");
    }
}