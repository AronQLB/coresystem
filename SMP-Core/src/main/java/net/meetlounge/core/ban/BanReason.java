package net.meetlounge.core.ban;

public enum BanReason {

    HACKING(1, "Hacking", 30),
    BUGUSING(2, "Bugusing", 14),
    INSULTING(3, "Beleidigung", 3),
    ADVERTISING(4, "Werbung", 7),
    REAL_MONEY_TRADING(5, "Echtgeldhandel", -1);

    private final int id;
    private final String displayName;
    private final int days;

    BanReason(int id, String displayName, int days) {
        this.id = id;
        this.displayName = displayName;
        this.days = days;
    }

    public int id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public int days() {
        return days;
    }

    public boolean permanent() {
        return days == -1;
    }

    public long durationMillis() {
        if (permanent()) {
            return -1;
        }

        return days * 24L * 60L * 60L * 1000L;
    }

    public static BanReason fromId(int id) {
        for (BanReason reason : values()) {
            if (reason.id == id) {
                return reason;
            }
        }

        return null;
    }
}