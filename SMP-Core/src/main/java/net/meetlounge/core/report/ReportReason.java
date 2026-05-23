package net.meetlounge.core.report;

import org.bukkit.Material;

public enum ReportReason {

    HACKING("Hacking", Material.DIAMOND_SWORD),
    BUGUSING("Bugusing", Material.REDSTONE),
    CHAT("Chatverhalten", Material.PAPER),
    TEAMING("Teaming", Material.SHIELD),
    SKIN_NAME("Skin/Name", Material.NAME_TAG),
    OTHER("Sonstiges", Material.BOOK);

    private final String displayName;
    private final Material icon;

    ReportReason(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }
}