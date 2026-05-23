package net.meetlounge.core.language;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.util.TextUtil;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class PlaceholderService {

    private final Core plugin;

    public PlaceholderService(Core plugin) {
        this.plugin = plugin;
    }

    public String apply(String text, Player player) {
        if (text == null) {
            return "";
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%player%", player.getName());
        placeholders.put("%uuid%", player.getUniqueId().toString());

        PlayerData data = plugin.players().get(player.getUniqueId());

        if (data != null) {
            String rankId = plugin.ranks().getRankId(player);

            placeholders.put("%coins%", String.valueOf(data.coins()));
            placeholders.put("%rank%", TextUtil.color(plugin.ranks().prefix(rankId)));
            placeholders.put("%playtime%", TimeUtil.formatDuration(plugin.players().currentPlaytime(player.getUniqueId())));
            placeholders.put("%first_join%", TimeUtil.formatDateTime(data.firstJoin()));
            placeholders.put("%last_join%", TimeUtil.formatDateTime(data.lastJoin()));
        }

        String result = text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return TextUtil.color(result);
    }

    public String applyGlobal(String text) {
        if (text == null) {
            return "";
        }

        return TextUtil.color(text
                .replace("%server%", plugin.configs().config().get().getString("server.name", "SMP"))
                .replace("%version%", plugin.getDescription().getVersion()));
    }
}
