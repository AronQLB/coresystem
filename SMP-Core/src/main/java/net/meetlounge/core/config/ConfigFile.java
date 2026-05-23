package net.meetlounge.core.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ConfigFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private File file;
    private FileConfiguration config;

    public ConfigFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        config = YamlConfiguration.loadConfiguration(file);
        applyDefaults();
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("Config konnte nicht gespeichert werden: " + fileName);
        }
    }

    public void reload() {
        if (file == null) {
            load();
            return;
        }

        config = YamlConfiguration.loadConfiguration(file);
        applyDefaults();
        save();
    }

    public FileConfiguration get() {
        return config;
    }

    private void applyDefaults() {
        try (InputStreamReader reader = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8)) {
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
            config.setDefaults(defaults);
            config.options().copyDefaults(true);
        } catch (IOException | NullPointerException exception) {
            plugin.getLogger().warning("Defaults konnten nicht geladen werden: " + fileName);
        }
    }
}
