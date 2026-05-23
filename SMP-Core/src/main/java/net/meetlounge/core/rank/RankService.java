package net.meetlounge.core.rank;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class RankService {

    private static final String CUSTOM_RANKS_PATH = "ranks.custom";

    private final Core plugin;

    public RankService(Core plugin) {
        this.plugin = plugin;
    }

    public Rank getRank(UUID uuid) {
        PlayerData data = plugin.players().get(uuid);

        if (data == null) {
            return Rank.PLAYER;
        }

        return Rank.fromId(data.rank());
    }

    public String getRankId(UUID uuid) {
        PlayerData data = plugin.players().get(uuid);

        if (data == null || !exists(data.rank())) {
            return Rank.PLAYER.id();
        }

        return normalizeId(data.rank());
    }

    public String getRankId(Player player) {
        return getRankId(player.getUniqueId());
    }

    public Rank getRank(Player player) {
        return getRank(player.getUniqueId());
    }

    public void setRank(UUID uuid, Rank rank) {
        setRank(uuid, rank.id());
    }

    public void setRank(UUID uuid, String rankId) {
        PlayerData data = plugin.players().get(uuid);

        if (data == null || !exists(rankId)) {
            return;
        }

        data.setRank(normalizeId(rankId));
        plugin.players().save(data);

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            plugin.permissions().apply(player);
        }

        plugin.tablist().refreshAll();
    }

    public boolean hasAtLeast(Player player, Rank requiredRank) {
        return weight(getRankId(player)) >= requiredRank.weight();
    }

    public boolean hasAtLeast(Player player, String rankId) {
        return exists(rankId) && weight(getRankId(player)) >= weight(rankId);
    }

    public boolean createRank(String rankId) {
        String id = normalizeId(rankId);

        if (!isValidId(id) || exists(id)) {
            return false;
        }

        plugin.configs().config().get().set(path(id, "prefix"), "&7" + id + " &8| &f");
        plugin.configs().config().get().set(path(id, "weight"), 0);
        plugin.configs().config().save();
        return true;
    }

    public boolean setPrefix(String rankId, String prefix) {
        if (!exists(rankId)) {
            return false;
        }

        String id = normalizeId(rankId);
        plugin.configs().config().get().set(path(id, "prefix"), prefix);
        plugin.configs().config().save();
        plugin.tablist().refreshAll();
        return true;
    }

    public boolean setWeight(String rankId, int weight) {
        if (!exists(rankId)) {
            return false;
        }

        String id = normalizeId(rankId);
        plugin.configs().config().get().set(path(id, "weight"), weight);
        plugin.configs().config().save();
        plugin.tablist().refreshAll();
        return true;
    }

    public String prefix(String rankId) {
        String id = normalizeId(rankId);
        Rank builtIn = builtIn(id);

        if (builtIn != null && !plugin.configs().config().get().contains(path(id, "prefix"))) {
            return builtIn.displayName();
        }

        return plugin.configs().config().get().getString(path(id, "prefix"), "&7Spieler");
    }

    public int weight(String rankId) {
        String id = normalizeId(rankId);
        Rank builtIn = builtIn(id);

        if (builtIn != null && !plugin.configs().config().get().contains(path(id, "weight"))) {
            return builtIn.weight();
        }

        return plugin.configs().config().get().getInt(path(id, "weight"), builtIn == null ? 0 : builtIn.weight());
    }

    public boolean exists(String rankId) {
        String id = normalizeId(rankId);
        return builtIn(id) != null || plugin.configs().config().get().contains(CUSTOM_RANKS_PATH + "." + id);
    }

    public List<String> rankIds() {
        Set<String> ids = new LinkedHashSet<>();
        Arrays.stream(Rank.values()).map(Rank::id).forEach(ids::add);

        ConfigurationSection section = plugin.configs().config().get().getConfigurationSection(CUSTOM_RANKS_PATH);

        if (section != null) {
            ids.addAll(section.getKeys(false));
        }

        return new ArrayList<>(ids);
    }

    public String normalizeId(String rankId) {
        if (rankId == null) {
            return "";
        }

        return rankId.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isValidId(String rankId) {
        return rankId != null && rankId.matches("[a-z0-9_-]{2,32}");
    }

    private Rank builtIn(String rankId) {
        String id = normalizeId(rankId);

        for (Rank rank : Rank.values()) {
            if (rank.id().equalsIgnoreCase(id)) {
                return rank;
            }
        }

        return null;
    }

    private String path(String rankId, String key) {
        return CUSTOM_RANKS_PATH + "." + normalizeId(rankId) + "." + key;
    }
}
