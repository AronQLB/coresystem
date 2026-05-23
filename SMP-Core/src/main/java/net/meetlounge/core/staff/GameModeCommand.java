package net.meetlounge.core.staff;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.GameMode;

public final class GameModeCommand extends AbstractCommand {

    public GameModeCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.GAMEMODE)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&cNutze: /gm <1|2|3>");
            return;
        }

        GameMode gameMode = switch (context.arg(0)) {
            case "1" -> GameMode.SURVIVAL;
            case "2" -> GameMode.CREATIVE;
            case "3" -> GameMode.SPECTATOR;
            default -> null;
        };

        if (gameMode == null) {
            context.raw(Core.prefix + "&cNutze: /gm <1|2|3>");
            return;
        }

        context.player().setGameMode(gameMode);
        context.raw(Core.prefix + "&7Dein Spielmodus wurde auf &a" + gameMode.name() + " &7gesetzt.");
    }
}
