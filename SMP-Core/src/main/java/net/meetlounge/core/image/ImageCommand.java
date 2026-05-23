package net.meetlounge.core.image;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import org.bukkit.block.BlockFace;

public final class ImageCommand extends AbstractCommand {

    public ImageCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (context.length() < 1) {
            help(context);
            return;
        }

        switch (context.arg(0).toLowerCase()) {
            case "spawn" -> spawn(context);
            case "move" -> move(context);
            case "delete" -> delete(context);
            default -> help(context);
        }
    }

    private void spawn(CommandContext context) {
        if (context.length() < 3) {
            context.raw(Core.prefix + "&7Nutze: /image spawn <id> <file> [north|south|east|west]");
            return;
        }

        BlockFace face = context.length() >= 4
                ? parseFace(context.arg(3))
                : context.player().getFacing();

        plugin.images().spawnImage(
                context.arg(1),
                context.arg(2),
                context.player().getEyeLocation().subtract(0, 1.6, 0),
                face
        );

        context.raw(Core.prefix + "&aBild wurde gespawnt.");
    }

    private void move(CommandContext context) {
        if (context.length() < 5) {
            context.raw(Core.prefix + "&7Nutze: /image move <id> <x> <y> <z>");
            return;
        }

        try {
            plugin.images().move(
                    context.arg(1),
                    Double.parseDouble(context.arg(2)),
                    Double.parseDouble(context.arg(3)),
                    Double.parseDouble(context.arg(4))
            );

            context.raw(Core.prefix + "&aBild wurde bewegt.");

        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cUngültige Koordinaten.");
        }
    }

    private void delete(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /image delete <id>");
            return;
        }

        plugin.images().delete(context.arg(1));
        context.raw(Core.prefix + "&aBild wurde gelöscht.");
    }

    private BlockFace parseFace(String input) {
        return switch (input.toLowerCase()) {
            case "north" -> BlockFace.NORTH;
            case "south" -> BlockFace.SOUTH;
            case "east" -> BlockFace.EAST;
            case "west" -> BlockFace.WEST;
            default -> BlockFace.NORTH;
        };
    }

    private void help(CommandContext context) {
        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aImageSystem");
        context.raw(Core.prefix + "&7/image spawn <id> <file> [north|south|east|west]");
        context.raw(Core.prefix + "&7/image move <id> <x> <y> <z>");
        context.raw(Core.prefix + "&7/image delete <id>");
        context.raw(Core.prefix + "&8&m----------------------------");
    }
}