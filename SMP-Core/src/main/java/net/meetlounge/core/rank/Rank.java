package net.meetlounge.core.rank;

public enum Rank {

    PLAYER("player", "&7Spieler", 0),
    MEET("meet", "&eMeet", 5),
    LOUNGE("lounge", "&bLounge", 10),
    CONTENT("content", "&6Content", 15),
    SUPPORTER("supporter", "&bSupporter", 20),
    MODERATOR("moderator", "&cModerator", 25),
    DEVELOPER("developer", "&bDeveloper", 30),
    ADMIN("admin", "&4Administrator", 35);

    private final String id;
    private final String displayName;
    private final int weight;

    Rank(String id, String displayName, int weight) {
        this.id = id;
        this.displayName = displayName;
        this.weight = weight;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public int weight() {
        return weight;
    }

    public boolean isAtLeast(Rank rank) {
        return this.weight >= rank.weight;
    }

    public static Rank fromId(String id) {
        if (id == null) {
            return PLAYER;
        }

        for (Rank rank : values()) {
            if (rank.id.equalsIgnoreCase(id)) {
                return rank;
            }
        }

        return PLAYER;
    }
}