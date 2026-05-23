package net.meetlounge.core.region;

import net.meetlounge.core.Core;

import java.sql.*;
import java.util.*;

public final class RegionRepository {

    private final Core plugin;

    public RegionRepository(Core plugin) {
        this.plugin = plugin;
    }

    public void save(Region region) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO regions
                (name, world, type,
                min_x, min_y, min_z,
                max_x, max_y, max_z,
                center_x, center_z, radius,
                pvp, build_flag, break_flag,
                mob_spawn, crop_trample, fall_damage)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                world = VALUES(world),
                min_x = VALUES(min_x),
                min_y = VALUES(min_y),
                min_z = VALUES(min_z),
                max_x = VALUES(max_x),
                max_y = VALUES(max_y),
                max_z = VALUES(max_z),
                pvp = VALUES(pvp),
                build_flag = VALUES(build_flag),
                break_flag = VALUES(break_flag),
                mob_spawn = VALUES(mob_spawn),
                crop_trample = VALUES(crop_trample),
                fall_damage = VALUES(fall_damage)
                 """)) {

            statement.setString(1, region.name());
            statement.setString(2, region.world());
            statement.setString(3, region.type().name());

            statement.setInt(4, region.minX());
            statement.setInt(5, region.minY());
            statement.setInt(6, region.minZ());

            statement.setInt(7, region.maxX());
            statement.setInt(8, region.maxY());
            statement.setInt(9, region.maxZ());

            statement.setInt(10, region.centerX());
            statement.setInt(11, region.centerZ());
            statement.setInt(12, region.radius());

            statement.setBoolean(13, region.flag(RegionFlag.PVP));
            statement.setBoolean(14, region.flag(RegionFlag.BUILD));
            statement.setBoolean(15, region.flag(RegionFlag.BREAK));
            statement.setBoolean(16, region.flag(RegionFlag.MOB_SPAWN));
            statement.setBoolean(17, region.flag(RegionFlag.CROP_TRAMPLE));
            statement.setBoolean(18, region.flag(RegionFlag.FALL_DAMAGE));
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Region konnte nicht gespeichert werden", exception);
        }
    }

    public List<Region> findAll() {
        List<Region> regions = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM regions");
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                regions.add(read(result));
            }

        } catch (SQLException exception) {
            plugin.debug().error("Regionen konnten nicht geladen werden", exception);
        }

        return regions;
    }

    public void delete(String name) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM regions WHERE name = ?")) {

            statement.setString(1, name.toLowerCase());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Region konnte nicht gelöscht werden", exception);
        }
    }

    private Region read(ResultSet result) throws SQLException {

        Map<RegionFlag, Boolean> flags = new EnumMap<>(RegionFlag.class);
        flags.put(RegionFlag.PVP, result.getBoolean("pvp"));
        flags.put(RegionFlag.BUILD, result.getBoolean("build_flag"));
        flags.put(RegionFlag.BREAK, result.getBoolean("break_flag"));
        flags.put(RegionFlag.MOB_SPAWN, result.getBoolean("mob_spawn"));
        flags.put(RegionFlag.CROP_TRAMPLE, result.getBoolean("crop_trample"));
        flags.put(RegionFlag.FALL_DAMAGE, result.getBoolean("fall_damage"));

        RegionType type = RegionType.valueOf(
                result.getString("type")
        );


        return new Region(
                result.getString("name"),
                result.getString("world"),
                type,

                result.getInt("min_x"),
                result.getInt("min_y"),
                result.getInt("min_z"),

                result.getInt("max_x"),
                result.getInt("max_y"),
                result.getInt("max_z"),

                result.getInt("center_x"),
                result.getInt("center_z"),
                result.getInt("radius"),

                flags
        );
    }
}