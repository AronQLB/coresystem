package net.meetlounge.core.command.sub;

import net.meetlounge.core.command.CommandContext;

import java.util.List;

public interface SubCommand {

    String name();

    String permission();

    String usage();

    void execute(CommandContext context);

    default List<String> tabComplete(CommandContext context) {
        return List.of();
    }
}