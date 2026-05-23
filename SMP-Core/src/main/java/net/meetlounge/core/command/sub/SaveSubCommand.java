package net.meetlounge.core.command.sub;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class SaveSubCommand implements SubCommand {

    private final Core plugin;

    public SaveSubCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "save";
    }

    @Override
    public String permission() {
        return PermissionNode.CORE_SAVE.node();
    }

    @Override
    public String usage() {
        return Core.prefix + "/core save";
    }

    @Override
    public void execute(CommandContext context) {
        plugin.scheduler().async(() -> {
            plugin.autosave().forceSave();
            context.raw(Core.prefix + "&aAlle Core-Daten wurden gespeichert.");
        });
    }
}
