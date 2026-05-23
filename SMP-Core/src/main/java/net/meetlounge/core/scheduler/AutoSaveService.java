package net.meetlounge.core.scheduler;

import net.meetlounge.core.Core;
import org.bukkit.scheduler.BukkitTask;

public final class AutoSaveService {

    private final Core plugin;

    private BukkitTask task;
    private boolean running;
    private long lastSaveStarted;
    private long lastSaveFinished;
    private long lastSaveDuration;

    public AutoSaveService(Core plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.configs().config().get().getBoolean("autosave.enabled", true)) {
            plugin.debug().info("AutoSave ist deaktiviert.");
            return;
        }

        int intervalMinutes = plugin.configs().config().get().getInt("autosave.interval", 5);
        long intervalTicks = intervalMinutes * 60L * 20L;

        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin,
                this::runSave,
                intervalTicks,
                intervalTicks
        );

        plugin.debug().info("AutoSave gestartet. Intervall: " + intervalMinutes + " Minuten.");
    }

    public void runSave() {
        if (running) {
            plugin.debug().warn("AutoSave übersprungen, weil bereits ein AutoSave läuft.");
            return;
        }

        running = true;
        lastSaveStarted = System.currentTimeMillis();

        try {
            plugin.players().saveAllNow();
            lastSaveFinished = System.currentTimeMillis();
            lastSaveDuration = lastSaveFinished - lastSaveStarted;

            plugin.debug().debug("AutoSave fertig. Dauer: " + lastSaveDuration + "ms");
        } finally {
            running = false;
        }
    }

    public void forceSave() {
        runSave();
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void restart() {
        stop();
        start();
    }

    public boolean isRunning() {
        return running;
    }

    public long lastSaveStarted() {
        return lastSaveStarted;
    }

    public long lastSaveFinished() {
        return lastSaveFinished;
    }

    public long lastSaveDuration() {
        return lastSaveDuration;
    }
}