package net.meetlounge.core.npc;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class NpcCommand extends AbstractCommand {

    public NpcCommand(Core plugin) {
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

        if (context.length() < 1) {
            help(context);
            return;
        }

        switch (context.arg(0).toLowerCase()) {
            case "create" -> create(context);
            case "delete" -> delete(context);
            case "reload" -> {
                plugin.database().createTables();
                plugin.npcs().load();
                context.raw(Core.prefix + "&aNPCs wurden neu geladen.");
            }

            case "deleteall" -> {

                plugin.npcs().deleteAllWorldNpcs();

                context.raw("&aAlle NPCs wurden gelöscht.");
            }
            default -> help(context);
        }
    }

    private void create(CommandContext context) {

        if (context.length() < 5) {
            context.raw(Core.prefix + "&cNutze: /npc create <name> <allay|blaze|enderman|villager> <color> <command>");
            return;
        }

        String name = context.arg(1);

        NpcType type;

        try {
            type = parseType(context.arg(2));
        } catch (IllegalArgumentException exception) {
            context.raw(Core.prefix + "&cUngültiger NPC-Type. Nutze: allay, blaze, enderman, villager");
            return;
        }

        NpcColor color;

        try {
            color = NpcColor.valueOf(context.arg(3).toUpperCase());
        } catch (IllegalArgumentException exception) {
            context.raw("&cUngültige Farbe.");
            return;
        }

        String command = context.joined(4);

        boolean created = plugin.npcs().create(
                name,
                type,
                color,
                context.player().getLocation(),
                command
        );

        if (created) {
            context.raw(Core.prefix + "&aNPC wurde erstellt.");
        } else {
            context.raw(Core.prefix + "&cNPC konnte nicht in der Datenbank gespeichert werden.");
        }
    }

    private void delete(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /npc delete <id>");
            return;
        }

        plugin.npcs().delete(context.arg(1));
        context.raw(Core.prefix + "&aNPC wurde gelöscht.");
    }

    private NpcType parseType(String input) {
        return switch (input.toUpperCase()) {
            case "ENDERMANN" -> NpcType.ENDERMAN;
            case "VILLIGAR", "VILLAGER" -> NpcType.VILLAGER;
            default -> NpcType.valueOf(input.toUpperCase());
        };
    }

    private void help(CommandContext context) {
        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aNPC-System");
        context.raw(Core.prefix + "&7/npc create <name> <allay|blaze|enderman|villager> <color> <command>");
        context.raw(Core.prefix + "&7/npc delete <id>");
        context.raw(Core.prefix + "&7/npc reload");
        context.raw("&8&m----------------------------");
    }
}
