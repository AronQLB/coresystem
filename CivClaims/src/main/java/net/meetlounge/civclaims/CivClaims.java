package net.meetlounge.civclaims;

import net.meetlounge.civclaims.command.CityCommand;
import net.meetlounge.civclaims.listener.ClaimProtectionListener;
import net.meetlounge.civclaims.service.CityService;
import net.meetlounge.civclaims.storage.YamlCityStorage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CivClaims extends JavaPlugin {

    private CityService cityService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        YamlCityStorage storage = new YamlCityStorage(this);
        this.cityService = new CityService(storage);
        this.cityService.load();

        PluginCommand cityCommand = Objects.requireNonNull(getCommand("city"), "Command /city fehlt in plugin.yml");
        CityCommand executor = new CityCommand(cityService);
        cityCommand.setExecutor(executor);
        cityCommand.setTabCompleter(executor);

        getServer().getPluginManager().registerEvents(new ClaimProtectionListener(cityService), this);
        getLogger().info("CivClaims aktiviert.");
    }

    @Override
    public void onDisable() {
        if (cityService != null) {
            cityService.save();
        }
    }
}
