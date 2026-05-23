package net.meetlounge.core.report;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class ReportsCommand extends AbstractCommand {

    public ReportsCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.REPORT_STAFF)) {
            context.reply("no-permission");
            return;
        }

        context.player().openInventory(new ReportsGui(plugin).create());
    }
}