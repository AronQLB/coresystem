package net.meetlounge.core.warp;

import org.bukkit.Material;

public enum WarpType {

    RTP("rtp", "&aRTP", Material.GRASS_BLOCK, 11),
    NETHER("nether", "&cNether", Material.NETHERRACK, 13),
    END("end", "&5End", Material.END_STONE, 15);

    private final String id;
    private final String displayName;
    private final Material icon;
    private final int slot;

    WarpType(String id, String displayName, Material icon, int slot) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.slot = slot;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }

    public int slot() {
        return slot;
    }

    public static WarpType fromId(String id) {
        if (id == null) {
            return null;
        }

        for (WarpType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }

        return null;
    }
}