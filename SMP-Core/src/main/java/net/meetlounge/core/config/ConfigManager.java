package net.meetlounge.core.config;

import net.meetlounge.core.Core;

public class ConfigManager {

    private final ConfigFile config;
    private final ConfigFile messages;

    public ConfigManager(Core plugin) {
        this.config = new ConfigFile(plugin, "config.yml");
        this.messages = new ConfigFile(plugin, "messages.yml");
    }

    public void load() {
        config.load();
        messages.load();
    }

    public void reload() {
        config.reload();
        messages.reload();
    }

    public ConfigFile config() {
        return config;
    }

    public ConfigFile messages() {
        return messages;
    }

}
