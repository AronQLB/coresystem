package net.meetlounge.core.region;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public final class RegionService {

    private final RegionRepository repository;
    private final RegionSelectionService selectionService = new RegionSelectionService();
    private final Map<String, Region> regions = new HashMap<>();

    public RegionService(RegionRepository repository) {
        this.repository = repository;
    }

    public void load() {
        regions.clear();
        for (Region region : repository.findAll()) {
            regions.put(region.name().toLowerCase(), region);
        }
    }

    public RegionSelectionService selections() {
        return selectionService;
    }

    public void create(String name, Location pos1, Location pos2) {
        Map<RegionFlag, Boolean> flags = new EnumMap<>(RegionFlag.class);

        flags.put(RegionFlag.PVP, false);
        flags.put(RegionFlag.BUILD, false);
        flags.put(RegionFlag.BREAK, false);
        flags.put(RegionFlag.MOB_SPAWN, false);
        flags.put(RegionFlag.CROP_TRAMPLE, false);
        flags.put(RegionFlag.FALL_DAMAGE, false);

        Region region = new Region(
                name.toLowerCase(),
                pos1.getWorld().getName(),
                RegionType.CUBOID,

                Math.min(pos1.getBlockX(), pos2.getBlockX()),
                -64,
                Math.min(pos1.getBlockZ(), pos2.getBlockZ()),

                Math.max(pos1.getBlockX(), pos2.getBlockX()),
                320,
                Math.max(pos1.getBlockZ(), pos2.getBlockZ()),

                0,
                0,
                0,

                flags
        );

        regions.put(name.toLowerCase(), region);
        repository.save(region);
    }

    public boolean setFlag(String regionName, RegionFlag flag, boolean value) {
        Region old = regions.get(regionName.toLowerCase());

        if (old == null) {
            return false;
        }

        Map<RegionFlag, Boolean> flags = new EnumMap<>(old.flags());
        flags.put(flag, value);

        Region updated = new Region(
                old.name(),
                old.world(),
                old.type(),

                old.minX(),
                old.minY(),
                old.minZ(),

                old.maxX(),
                old.maxY(),
                old.maxZ(),

                old.centerX(),
                old.centerZ(),
                old.radius(),

                flags
        );

        regions.put(updated.name(), updated);
        repository.save(updated);
        return true;
    }

    public void delete(String name) {
        regions.remove(name.toLowerCase());
        repository.delete(name);
    }

    public List<String> list() {
        return new ArrayList<>(regions.keySet());
    }

    public Optional<Region> regionAt(Location location) {
        return regions.values()
                .stream()
                .filter(region -> region.contains(location))
                .findFirst();
    }

    public boolean allowed(Player player, Location location, RegionFlag flag) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        return regionAt(location)
                .map(region -> region.flag(flag))
                .orElse(true);
    }

    public void save(Region region) {
        regions.put(region.name().toLowerCase(), region);
        repository.save(region);
    }
}