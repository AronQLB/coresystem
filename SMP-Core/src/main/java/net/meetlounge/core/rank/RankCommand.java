package net.meetlounge.core.rank;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.entity.Player;

import org.bukkit.Bukkit;

public final class RankCommand extends AbstractCommand {

    public RankCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!plugin.permissions().has(context.sender(), PermissionNode.RANK_MANAGE)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 2) {
            sendHelp(context);
            return;
        }

        if (context.arg(0).equalsIgnoreCase("info")) {
            handleInfo(context);
            return;
        }

        if (context.arg(0).equalsIgnoreCase("perm") || context.arg(0).equalsIgnoreCase("permission")) {
            handlePermission(context);
            return;
        }

        handleSetRank(context);
    }

    private void sendHelp(CommandContext context) {
        context.raw("&8&m----------------------------");
        context.raw("&aRank Commands");
        context.raw("&7/rank <spieler> <rang>");
        context.raw("&7/rank info <spieler>");
        context.raw("&7/rank perm add <rang> <permission>");
        context.raw("&7/rank perm remove <rang> <permission>");
        context.raw("&7/rank perm list <rang>");
        context.raw("&7/rank perm create <rang>");
        context.raw("&7/rank perm edit <rang> <weight>");
        context.raw("&7/rank perm setprefix <rang> <prefix>");
        context.raw("&8&m----------------------------");
    }

    private void handlePermission(CommandContext context) {
        if (context.length() < 2) {
            sendHelp(context);
            return;
        }

        String action = context.arg(1).toLowerCase();

        if (action.equals("create")) {
            if (context.length() < 3) {
                context.raw("&cNutze: /rank perm create <rang>");
                return;
            }

            String rankId = plugin.ranks().normalizeId(context.arg(2));

            if (plugin.ranks().createRank(rankId)) {
                context.raw("&aRang &f" + rankId + " &awurde erstellt.");
            } else {
                context.raw("&eDieser Rang existiert bereits oder die ID ist ungültig. Erlaubt: a-z, 0-9, _ und -");
            }
            return;
        }

        if (action.equals("edit")) {
            if (context.length() < 4) {
                context.raw("&cNutze: /rank perm edit <rang> <weight>");
                return;
            }

            String rankId = plugin.ranks().normalizeId(context.arg(2));
            int weight;

            try {
                weight = Integer.parseInt(context.arg(3));
            } catch (NumberFormatException exception) {
                context.raw("&cWeight muss eine Zahl sein.");
                return;
            }

            if (plugin.ranks().setWeight(rankId, weight)) {
                context.raw("&aWeight von Rang &f" + rankId + " &awurde auf &f" + weight + " &agesetzt.");
                plugin.permissions().applyAll();
            } else {
                context.raw("&cDiesen Rang gibt es nicht.");
            }
            return;
        }

        if (action.equals("setprefix")) {
            if (context.length() < 4) {
                context.raw("&cNutze: /rank perm setprefix <rang> <prefix>");
                return;
            }

            String rankId = plugin.ranks().normalizeId(context.arg(2));
            String prefix = context.joined(3);

            if (plugin.ranks().setPrefix(rankId, prefix)) {
                context.raw("&aPrefix von Rang &f" + rankId + " &awurde auf " + prefix + " &agesetzt.");
            } else {
                context.raw("&cDiesen Rang gibt es nicht.");
            }
            return;
        }

        if (context.length() < 3) {
            context.raw("&cNutze: /rank perm <add|remove|list> <rang> [permission]");
            return;
        }

        String rankId = plugin.ranks().normalizeId(context.arg(2));

        if (!plugin.ranks().exists(rankId)) {
            context.raw("&cDiesen Rang gibt es nicht.");
            context.raw("&7Ränge: &f" + String.join(", ", plugin.ranks().rankIds()));
            return;
        }

        if (action.equals("list")) {
            var permissions = plugin.permissions().rankPermissions(rankId);
            context.raw("&aRang: &f" + rankId);
            context.raw("&7Weight: &f" + plugin.ranks().weight(rankId));
            context.raw("&7Prefix: " + plugin.ranks().prefix(rankId));
            context.raw("&aZusätzliche Permissions:");
            context.raw(permissions.isEmpty() ? "&7Keine Zusatz-Permissions gesetzt." : "&f" + String.join("&7, &f", permissions));
            return;
        }

        if (context.length() < 4) {
            context.raw("&cNutze: /rank perm " + action + " <rang> <permission>");
            return;
        }

        String permission = plugin.permissions().normalize(context.arg(3));

        if (action.equals("add")) {
            if (plugin.permissions().addRankPermission(rankId, permission)) {
                context.raw("&aPermission &f" + permission + " &awurde zu Rang &f" + rankId + " &ahinzugefügt.");
            } else {
                context.raw("&eDiese Permission ist dort bereits gesetzt oder der Rang ist ungültig.");
            }
            return;
        }

        if (action.equals("remove")) {
            if (plugin.permissions().removeRankPermission(rankId, permission)) {
                context.raw("&aPermission &f" + permission + " &awurde von Rang &f" + rankId + " &aentfernt.");
            } else {
                context.raw("&eDiese Permission war dort nicht gesetzt oder der Rang ist ungültig.");
            }
            return;
        }

        sendHelp(context);
    }

    private void handleSetRank(CommandContext context) {
        if (context.length() < 2) {
            context.raw("&cNutze: /rank <spieler> <rang>");
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(0));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        String rankId = plugin.ranks().normalizeId(context.arg(1));

        if (!plugin.ranks().exists(rankId)) {
            context.raw("&cDiesen Rang gibt es nicht.");
            context.raw("&7Ränge: &f" + String.join(", ", plugin.ranks().rankIds()));
            return;
        }

        plugin.ranks().setRank(target.getUniqueId(), rankId);

        context.raw("&aDer Rang von &f" + target.getName() + " &awurde auf " + plugin.ranks().prefix(rankId) + " &agesetzt.");
        target.sendMessage(plugin.messages().raw(
                "&aDein Rang wurde auf " + plugin.ranks().prefix(rankId) + " &agesetzt."
        ));
    }

    private void handleInfo(CommandContext context) {
        if (context.length() < 2) {
            context.raw("&cNutze: /rank info <spieler>");
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        String rankId = plugin.ranks().getRankId(target);

        context.raw("&8&m----------------------------");
        context.raw("&aRank Info");
        context.raw("&7Spieler: &f" + target.getName());
        context.raw("&7Rang: &f" + rankId);
        context.raw("&7Prefix: " + plugin.ranks().prefix(rankId));
        context.raw("&7Weight: &f" + plugin.ranks().weight(rankId));
        context.raw("&8&m----------------------------");
    }
}
