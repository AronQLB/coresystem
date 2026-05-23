package net.meetlounge.core.ban;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public final class BanInfoCommand extends AbstractCommand {

    public BanInfoCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!plugin.permissions().has(context.sender(), PermissionNode.BAN_INFO)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&7Nutze: /&cbaninfo &8[&aSpieler&8]");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(context.arg(0));
        Optional<BanData> banOptional = plugin.bans().getActiveBan(target.getUniqueId());

        if (banOptional.isEmpty()) {
            context.raw(Core.prefix + "&7Dieser &fSpieler &7ist nicht gebannt.");
            return;
        }

        BanData ban = banOptional.get();

        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw("&1");
        context.raw(Core.prefix + "&cBanInfo: &f" + ban.name());
        context.raw(Core.prefix + "&7Grund: &f" + ban.reason());
        context.raw(Core.prefix + "&7ID: &f" + ban.reasonId());
        context.raw(Core.prefix + "&7Von: &f" + ban.staff());
        context.raw(Core.prefix + "&7Gebannt am: &f" + TimeUtil.formatDateTime(ban.createdAt()));
        context.raw(Core.prefix + "&7Bis: &f" + (ban.permanent() ? "Permanent" : TimeUtil.formatDateTime(ban.expiresAt())));
        context.raw("&2");
        context.raw(Core.prefix + "&8&m----------------------------");
    }
}