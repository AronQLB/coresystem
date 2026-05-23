package net.meetlounge.core.region;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

import java.util.HashMap;
import java.util.Map;

public final class RegionCommand extends AbstractCommand {

    public RegionCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.CORE_ADMIN)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() == 0) {
            help(context);
            return;
        }

        switch (context.arg(0).toLowerCase()) {
            case "pos1" -> {
                plugin.regions().selections().setPos1(context.player().getUniqueId(), context.player().getLocation());
                context.raw(Core.prefix + "&aPosition 1 gesetzt.");
            }
            case "pos2" -> {
                plugin.regions().selections().setPos2(context.player().getUniqueId(), context.player().getLocation());
                context.raw(Core.prefix + "&aPosition 2 gesetzt.");
            }
            case "create" -> create(context);
            case "flag" -> flag(context);
            case "delete" -> delete(context);
            case "list" -> context.raw(Core.prefix + "&aRegionen: &f" + String.join("&7, &f", plugin.regions().list()));
            case "circle" -> circle(context);
            default -> help(context);
        }
    }

    private void create(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&cNutze: /region create <name>");
            return;
        }

        var selections = plugin.regions().selections();

        if (!selections.hasSelection(context.player().getUniqueId())) {
            context.raw(Core.prefix + "&cDu musst zuerst /region pos1 und /region pos2 setzen.");
            return;
        }

        var pos1 = selections.pos1(context.player().getUniqueId());
        var pos2 = selections.pos2(context.player().getUniqueId());

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            context.raw(Core.prefix + "&cBeide Positionen müssen in derselben Welt sein.");
            return;
        }

        plugin.regions().create(context.arg(1), pos1, pos2);
        selections.clear(context.player().getUniqueId());

        context.raw(Core.prefix + "&aRegion &f" + context.arg(1) + " &awurde erstellt.");
    }

    private void flag(CommandContext context) {
        if (context.length() < 4) {
            context.raw(Core.prefix + "&7Nutze: /region flag <name> <pvp|build|break|mob-spawn|crop-trample|fall-damage> <true|false>");
            return;
        }

        RegionFlag flag = RegionFlag.fromId(context.arg(2));

        if (flag == null) {
            context.raw(Core.prefix + "&cDiese Flag gibt es nicht.");
            return;
        }

        boolean value = Boolean.parseBoolean(context.arg(3));

        if (!plugin.regions().setFlag(context.arg(1), flag, value)) {
            context.raw(Core.prefix + "&cDiese Region existiert nicht.");
            return;
        }

        context.raw(Core.prefix + "&7Flag &f" + flag.id() + " &7wurde auf &f" + value + " &7gesetzt.");
    }

    private void delete(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /region delete <name>");
            return;
        }

        plugin.regions().delete(context.arg(1));
        context.raw(Core.prefix + "&aRegion gelöscht.");
    }

    private void circle(CommandContext context) {

        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (context.length() < 3) {
            context.raw(Core.prefix + "&7Nutze: /region circle <name> <radius>");
            return;
        }

        String name = context.arg(1);

        int radius;

        try {
            radius = Integer.parseInt(context.arg(2));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cRadius muss eine Zahl sein.");
            return;
        }

        var location = context.player().getLocation();

        Map<RegionFlag, Boolean> flags = new HashMap<>();

        for (RegionFlag flag : RegionFlag.values()) {
            flags.put(flag, true);
        }

        Region region = new Region(
                name,
                location.getWorld().getName(),
                RegionType.CIRCLE,

                0,
                -64,
                0,

                0,
                320,
                0,

                location.getBlockX(),
                location.getBlockZ(),
                radius,

                flags
        );

        plugin.regions().save(region);

        context.raw(Core.prefix + "&aKreis-Region erstellt. Radius: &f" + radius);
    }

    private void help(CommandContext context) {
        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aRegionSystem");
        context.raw(Core.prefix + "&7/region pos1");
        context.raw(Core.prefix + "&7/region pos2");
        context.raw(Core.prefix + "&7/region create <name>");
        context.raw(Core.prefix + "&7/region flag <name> <flag> <true|false>");
        context.raw(Core.prefix + "&7/region list");
        context.raw(Core.prefix + "&7/region delete <name>");
        context.raw(Core.prefix + "&7/region circle <name> <radius>");
        context.raw(Core.prefix + "&8&m----------------------------");
    }

}