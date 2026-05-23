package net.meetlounge.core.command.sub;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public final class PlayerSubCommand implements SubCommand {

    private final Core plugin;

    public PlayerSubCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "player";
    }

    @Override
    public String permission() {
        return PermissionNode.CORE_PLAYER.node();
    }

    @Override
    public String usage() {
        return Core.prefix + "/core player <name>";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.length() < 2) {
            context.raw("&cNutze: " + usage());
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        PlayerData data = plugin.players().get(target.getUniqueId());

        if (data == null) {
            context.raw(Core.prefix + "&cSpielerdaten sind noch nicht geladen.");
            return;
        }

        String rankId = plugin.ranks().getRankId(target);

        context.raw(Core.prefix + "&8&m--------------------------------");
        context.raw(Core.prefix + "&aPlayerData: &f" + target.getName());
        context.raw(Core.prefix + "&7UUID: &f" + data.uuid());
        context.raw(Core.prefix + "&7Rank: &f" + rankId);
        context.raw(Core.prefix + "&7Coins: &f" + data.coins());
        context.raw(Core.prefix + "&7Playtime: &f" + TimeUtil.formatDuration(data.playtime()));
        context.raw(Core.prefix + "&7First Join: &f" + TimeUtil.formatDateTime(data.firstJoin()));
        context.raw(Core.prefix + "&7Last Join: &f" + TimeUtil.formatDateTime(data.lastJoin()));
        context.raw(Core.prefix + "&8&m--------------------------------");
    }

    @Override
    public List<String> tabComplete(CommandContext context) {
        if (context.length() == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        return List.of();
    }
}
