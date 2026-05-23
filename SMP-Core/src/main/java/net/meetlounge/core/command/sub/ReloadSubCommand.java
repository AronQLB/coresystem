package net.meetlounge.core.command.sub;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class ReloadSubCommand implements SubCommand {

    private final Core plugin;

    public ReloadSubCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return PermissionNode.CORE_RELOAD.node();
    }

    @Override
    public String usage() {
        return Core.prefix + "/core reload";
    }

    @Override
    public void execute(CommandContext context) {
        plugin.reloadCore();
        plugin.maintenance().load();
        plugin.autosave().restart();

        context.reply("core-reloaded");
    }
}
