package net.meetlounge.civclaims.service;

import net.meetlounge.civclaims.model.City;
import net.meetlounge.civclaims.model.CityRole;
import net.meetlounge.civclaims.model.ClaimChunk;
import net.meetlounge.civclaims.storage.CityStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class CityService {

    private final CityStorage storage;
    private final Map<String, City> citiesByName = new HashMap<>();
    private final Map<ClaimChunk, City> citiesByClaim = new HashMap<>();

    public CityService(CityStorage storage) {
        this.storage = storage;
    }

    public void load() {
        citiesByName.clear();
        citiesByClaim.clear();
        for(City city : storage.loadCities()) {
            citiesByName.put(city.name().toLowerCase(), city);
            for(ClaimChunk claim : city.claims()) {
                citiesByClaim.put(claim, city);
            }
        }
    }


    public void save() {
        storage.saveCities(citiesByName.values());
    }

    public Collection<City> cities() {
        return citiesByName.values();
    }
    public Optional<City> cityOf(UUID playerId) {
        return citiesByName.values().stream().filter(city -> city.isMember(playerId)).findFirst();
    }

    public Optional<City> cityAt(Location location) {
        return Optional.ofNullable(citiesByClaim.get(ClaimChunk.from(location)));
    }

    public boolean createCity(Player mayor, String name) {
        if (citiesByName.containsKey(name.toLowerCase()) || cityOf(mayor.getUniqueId()).isPresent()) {
            return false;
        }
        City city = new City(name, mayor.getUniqueId());
        citiesByName.put(name.toLowerCase(), city);
        save();
        return true;
    }

    public ClaimResult claim(Player player) {
        Optional<City> optionalCity = cityOf(player.getUniqueId());
        if (optionalCity.isEmpty()) {
            return ClaimResult.NO_CITY;
        }

        City city = optionalCity.get();
        CityRole role = city.roleOf(player.getUniqueId()).orElse(CityRole.GUEST);
        if (!role.canManageClaims()) {
            return ClaimResult.NO_PERMISSION;
        }

        ClaimChunk claim = ClaimChunk.from(player.getLocation());
        if (citiesByClaim.containsKey(claim)) {
            return ClaimResult.ALREADY_CLAIMED;
        }

        city.claims().add(claim);
        citiesByClaim.put(claim, city);
        save();
        return ClaimResult.SUCCESS;
    }

    public ClaimResult unclaim(Player player) {
        Optional<City> optionalCity = cityOf(player.getUniqueId());
        if (optionalCity.isEmpty()) {
            return ClaimResult.NO_CITY;
        }

        City city = optionalCity.get();
        CityRole role = city.roleOf(player.getUniqueId()).orElse(CityRole.GUEST);
        if (!role.canManageClaims()) {
            return ClaimResult.NO_PERMISSION;
        }

        ClaimChunk claim = ClaimChunk.from(player.getLocation());
        if (!city.claims().remove(claim)) {
            return ClaimResult.NOT_CLAIMED_BY_CITY;
        }

        citiesByClaim.remove(claim);
        save();
        return ClaimResult.SUCCESS;
    }

    public boolean canBuild(Player player, Location location) {
        Optional<City> optionalCity = cityAt(location);
        if (optionalCity.isEmpty()) {
            return true;
        }
        City city = optionalCity.get();
        return city.roleOf(player.getUniqueId()).map(CityRole::canBuild).orElse(false);
    }

    public enum ClaimResult {
        SUCCESS,
        NO_CITY,
        NO_PERMISSION,
        ALREADY_CLAIMED,
        NOT_CLAIMED_BY_CITY
    }
}
