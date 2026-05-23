package net.meetlounge.core.report;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ReportCommand extends AbstractCommand {

    public ReportCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.REPORT_CREATE)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&7Nutze: /&creport &7<spieler>");
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(0));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        if (target.getUniqueId().equals(context.player().getUniqueId())) {
            context.raw("&cDu kannst dich nicht selbst reporten.");
            return;
        }

        new ReportGui(plugin).open(context.player(), target);
    }
}