package net.meetlounge.core.warp;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class WarpsCommand extends AbstractCommand {

    public WarpsCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        context.player().openInventory(new WarpsGui(plugin).create());
    }
}