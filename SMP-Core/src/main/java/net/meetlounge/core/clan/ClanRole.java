package net.meetlounge.core.clan;

public enum ClanRole {

    OWNER(3, "Ersteller"),
    MODERATOR(2, "Moderator"),
    MEMBER(1, "Mitglied");

    private final int weight;
    private final String displayName;

    ClanRole(int weight, String displayName) {
        this.weight = weight;
        this.displayName = displayName;
    }

    public int weight() {
        return weight;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isAtLeast(ClanRole role) {
        return this.weight >= role.weight;
    }

    public static ClanRole fromString(String value) {
        if (value == null) {
            return MEMBER;
        }

        try {
            return ClanRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return MEMBER;
        }
    }
}