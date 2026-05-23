package net.meetlounge.core.economy;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;

import java.util.UUID;

public final class EconomyService {

    private final Core plugin;

    public EconomyService(Core plugin) {
        this.plugin = plugin;
    }

    public double getBalance(UUID uuid) {
        PlayerData data = plugin.players().get(uuid);

        if (data == null) {
            return 0.0;
        }

        return data.coins();
    }

    public void setBalance(UUID uuid, double amount) {
        PlayerData data = plugin.players().get(uuid);

        if (data == null) {
            return;
        }

        data.setCoins(Math.max(0.0, amount));
        plugin.players().save(data);
    }

    public void add(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean remove(UUID uuid, double amount) {
        if (getBalance(uuid) < amount) {
            return false;
        }

        setBalance(uuid, getBalance(uuid) - amount);
        return true;
    }

    public void set(UUID uuid, double amount) {
        PlayerData data = plugin.players().data(uuid);

        if (data == null) {
            return;
        }

        data.setCoins(amount);
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
}