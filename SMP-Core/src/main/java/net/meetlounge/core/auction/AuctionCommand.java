package net.meetlounge.core.auction;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class AuctionCommand extends AbstractCommand {

    public AuctionCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        context.player().openInventory(new AuctionGui(plugin).create(0));
    }
}
