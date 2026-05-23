package net.meetlounge.core.scheduler;

import net.meetlounge.core.Core;

public final class SchedulerService {

    private final Core plugin;

    public SchedulerService(Core plugin) {
        this.plugin = plugin;
    }

    public void sync(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public void async(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public void later(Runnable runnable, long ticks) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, ticks);
    }

    public void asyncTimer(Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }
}