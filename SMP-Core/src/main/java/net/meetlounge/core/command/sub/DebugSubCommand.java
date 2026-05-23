package net.meetlounge.core.command.sub;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import net.meetlounge.core.util.TimeUtil;

public final class DebugSubCommand implements SubCommand {

    private final Core plugin;

    public DebugSubCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "debug";
    }

    @Override
    public String permission() {
        return PermissionNode.CORE_DEBUG.node();
    }

    @Override
    public String usage() {
        return Core.prefix + "/core debug";
    }

    @Override
    public void execute(CommandContext context) {
        context.raw(Core.prefix + "&8&m--------------------------------");
        context.raw(Core.prefix + "&aSMP-Core Debug");
        context.raw(Core.prefix + "&7MySQL: &f" + plugin.database().isConnected());
        context.raw(Core.prefix + "&7Maintenance: &f" + plugin.maintenance().isEnabled());
        context.raw(Core.prefix + "&7AutoSave läuft: &f" + plugin.autosave().isRunning());
        context.raw(Core.prefix + "&7Letzter Save: &f" + TimeUtil.formatDuration(plugin.autosave().lastSaveDuration()));
        context.raw(Core.prefix + "&7Online: &f" + plugin.getServer().getOnlinePlayers().size());
        context.raw(Core.prefix + "&8&m--------------------------------");
    }
}
