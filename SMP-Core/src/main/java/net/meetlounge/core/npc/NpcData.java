package net.meetlounge.core.npc;

import org.bukkit.Location;

public final class NpcData {

    private final String id;
    private final Location location;
    private final String command;
    private final NpcType type;
    private final NpcColor color;

    public NpcData(
            String id,
            NpcType type,
            NpcColor color,
            Location location,
            String command
    ) {
        this.id = id;
        this.type = type;
        this.color = color;
        this.location = location;
        this.command = command;
    }

    public String id() {
        return id;
    }


    public Location location() {
        return location;
    }

    public String command() {
        return command;
    }

    public NpcType type() {
        return type;
    }

    public NpcColor color() {
        return color;
    }
}