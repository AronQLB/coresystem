package net.meetlounge.civclaims.storage;

import net.meetlounge.civclaims.model.City;
import net.meetlounge.civclaims.model.CityRole;
import net.meetlounge.civclaims.model.ClaimChunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class YamlCityStorage implements CityStorage {

    private final JavaPlugin plugin;
    private final File file;

    public YamlCityStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cities.yml");
    }

    @Override
    public List<City> loadCities() {
        if (!file.exists()) {
            return List.of();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection citiesSection = config.getConfigurationSection("cities");
        if (citiesSection == null) {
            return List.of();
        }

        List<City> cities = new ArrayList<>();
        for (String cityName : citiesSection.getKeys(false)) {
            String path = "cities." + cityName;
            UUID mayorId = UUID.fromString(config.getString(path + ".mayor"));
            City city = new City(cityName, mayorId);
            city.setBalance(config.getDouble(path + ".balance"));
            city.setDailyTax(config.getDouble(path + ".daily-tax"));
            city.setPvpAllowed(config.getBoolean(path + ".pvp"));

            ConfigurationSection membersSection = config.getConfigurationSection(path + ".members");
            if (membersSection != null) {
                for (String uuidText : membersSection.getKeys(false)) {
                    UUID playerId = UUID.fromString(uuidText);
                    CityRole role = CityRole.valueOf(membersSection.getString(uuidText, "CITIZEN"));
                    city.members().put(playerId, role);
                }
            }

            for (String claimText : config.getStringList(path + ".claims")) {
                String[] split = claimText.split(":");
                if (split.length == 3) {
                    city.claims().add(new ClaimChunk(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2])));
                }
            }
            cities.add(city);
        }
        return cities;
    }

    @Override
    public void saveCities(Collection<City> cities) {
        YamlConfiguration config = new YamlConfiguration();
        for (City city : cities) {
            String path = "cities." + city.name();
            config.set(path + ".mayor", city.mayorId().toString());
            config.set(path + ".balance", city.balance());
            config.set(path + ".daily-tax", city.dailyTax());
            config.set(path + ".pvp", city.pvpAllowed());
            config.set(path + ".claims", city.claims().stream().map(ClaimChunk::key).toList());

            for (Map.Entry<UUID, CityRole> entry : city.members().entrySet()) {
                config.set(path + ".members." + entry.getKey(), entry.getValue().name());
            }
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().severe("cities.yml konnte nicht gespeichert werden: " + exception.getMessage());
        }
    }
}