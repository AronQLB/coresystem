package net.meetlounge.core.api;

import net.meetlounge.core.Core;
import net.meetlounge.core.config.MessageManager;
import net.meetlounge.core.database.DatabaseManager;
import net.meetlounge.core.player.PlayerDataService;

public final class CoreAPI {

    private final Core plugin;

    public CoreAPI(Core plugin) {
        this.plugin = plugin;
    }

    public MessageManager messages() {
        return plugin.messages();
    }

    public DatabaseManager database() {
        return plugin.database();
    }

    public PlayerDataService players() {
        return plugin.players();
    }
}
