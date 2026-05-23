package net.meetlounge.core.command.sub;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class RankSubCommand implements SubCommand {

    private final Core plugin;

    public RankSubCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "rank";
    }

    @Override
    public String permission() {
        return PermissionNode.RANK_MANAGE.node();
    }

    @Override
    public String usage() {
        return Core.prefix + "/core rank <player> <rank> | /core rank perm <add|remove|list|create>";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.length() >= 2 && (context.arg(1).equalsIgnoreCase("perm") || context.arg(1).equalsIgnoreCase("permission"))) {
            handlePermission(context);
            return;
        }

        if (context.length() < 3) {
            context.raw(Core.prefix + "&cNutze: " + usage());
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        String rankId = plugin.ranks().normalizeId(context.arg(2));

        if (!plugin.ranks().exists(rankId)) {
            context.raw(Core.prefix + "&cDiesen Rang gibt es nicht.");
            context.raw("&7Ränge: &f" + String.join(", ", plugin.ranks().rankIds()));
            return;
        }

        plugin.ranks().setRank(target.getUniqueId(), rankId);
        context.raw(Core.prefix + "&aRang von &f" + target.getName() + " &awurde auf &f" + rankId + " &agesetzt.");
    }

    private void handlePermission(CommandContext context) {
        if (context.length() < 3) {
            context.raw(Core.prefix + "&cNutze: /core rank perm <add|remove|list|create> <rang> [permission]");
            return;
        }

        String action = context.arg(2).toLowerCase();

        if (action.equals("create")) {
            if (context.length() < 4) {
                context.raw(Core.prefix + "&cNutze: /core rank perm create <rang>");
                return;
            }

            String rankId = plugin.ranks().normalizeId(context.arg(3));

            if (plugin.ranks().createRank(rankId)) {
                context.raw(Core.prefix + "&aRang &f" + rankId + " &awurde erstellt.");
            } else {
                context.raw(Core.prefix + "&eDieser Rang existiert bereits oder die ID ist ungültig.");
            }
            return;
        }

        if (action.equals("edit")) {
            if (context.length() < 5) {
                context.raw(Core.prefix + "&cNutze: /core rank perm edit <rang> <weight>");
                return;
            }

            String rankId = plugin.ranks().normalizeId(context.arg(3));
            int weight;

            try {
                weight = Integer.parseInt(context.arg(4));
            } catch (NumberFormatException exception) {
                context.raw(Core.prefix + "&cWeight muss eine Zahl sein.");
                return;
            }

            if (plugin.ranks().setWeight(rankId, weight)) {
                context.raw(Core.prefix + "&aWeight von Rang &f" + rankId + " &awurde auf &f" + weight + " &agesetzt.");
                plugin.permissions().applyAll();
            } else {
                context.raw(Core.prefix + "&cDiesen Rang gibt es nicht.");
            }
            return;
        }

        if (action.equals("setprefix")) {
            if (context.length() < 5) {
                context.raw(Core.prefix + "&cNutze: /core rank perm setprefix <rang> <prefix>");
                return;
            }

            String rankId = plugin.ranks().normalizeId(context.arg(3));
            String prefix = context.joined(4);

            if (plugin.ranks().setPrefix(rankId, prefix)) {
                context.raw(Core.prefix + "&aPrefix von Rang &f" + rankId + " &awurde auf " + prefix + " &agesetzt.");
            } else {
                context.raw(Core.prefix + "&cDiesen Rang gibt es nicht.");
            }
            return;
        }

        if (context.length() < 4) {
            context.raw(Core.prefix + "&cNutze: /core rank perm <add|remove|list> <rang> [permission]");
            return;
        }

        String rankId = plugin.ranks().normalizeId(context.arg(3));

        if (!plugin.ranks().exists(rankId)) {
            context.raw(Core.prefix + "&cDiesen Rang gibt es nicht.");
            context.raw("&7Ränge: &f" + String.join(", ", plugin.ranks().rankIds()));
            return;
        }

        if (action.equals("list")) {
            var permissions = plugin.permissions().rankPermissions(rankId);
            context.raw(Core.prefix + "&aRang: &f" + rankId);
            context.raw("&7Weight: &f" + plugin.ranks().weight(rankId));
            context.raw("&7Prefix: " + plugin.ranks().prefix(rankId));
            context.raw("&aZusätzliche Permissions:");
            context.raw(permissions.isEmpty() ? "&7Keine Zusatz-Permissions gesetzt." : "&f" + String.join("&7, &f", permissions));
            return;
        }

        if (context.length() < 5) {
            context.raw(Core.prefix + "&cNutze: /core rank perm " + action + " <rang> <permission>");
            return;
        }

        String permission = plugin.permissions().normalize(context.arg(4));

        if (action.equals("add")) {
            if (plugin.permissions().addRankPermission(rankId, permission)) {
                context.raw(Core.prefix + "&aPermission &f" + permission + " &awurde zu Rang &f" + rankId + " &ahinzugefügt.");
            } else {
                context.raw(Core.prefix + "&eDiese Permission ist dort bereits gesetzt oder der Rang ist ungültig.");
            }
            return;
        }

        if (action.equals("remove")) {
            if (plugin.permissions().removeRankPermission(rankId, permission)) {
                context.raw(Core.prefix + "&aPermission &f" + permission + " &awurde von Rang &f" + rankId + " &aentfernt.");
            } else {
                context.raw(Core.prefix + "&eDiese Permission war dort nicht gesetzt oder der Rang ist ungültig.");
            }
            return;
        }

        context.raw(Core.prefix + "&cNutze: /core rank perm <add|remove|list|create>");
    }

    @Override
    public List<String> tabComplete(CommandContext context) {
        if (context.length() == 2) {
            List<String> result = new ArrayList<>();
            result.add("perm");
            result.add("permission");
            result.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            return result;
        }

        if (context.length() == 3) {
            if (context.arg(1).equalsIgnoreCase("perm") || context.arg(1).equalsIgnoreCase("permission")) {
                return List.of("add", "remove", "list", "create", "edit", "setprefix");
            }

            return plugin.ranks().rankIds();
        }

        if (context.length() == 4 && (context.arg(1).equalsIgnoreCase("perm") || context.arg(1).equalsIgnoreCase("permission"))) {
            if (context.arg(2).equalsIgnoreCase("create")) {
                return List.of("<rang>");
            }

            return plugin.ranks().rankIds();
        }

        if (context.length() == 5 && (context.arg(1).equalsIgnoreCase("perm") || context.arg(1).equalsIgnoreCase("permission"))) {
            if (context.arg(2).equalsIgnoreCase("add")) {
                return plugin.permissions().customNodes();
            }

            if (context.arg(2).equalsIgnoreCase("remove")) {
                return plugin.permissions().rankPermissions(context.arg(3));
            }
        }

        return List.of();
    }
}
