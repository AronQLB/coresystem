package net.meetlounge.core.permission;

public enum PermissionNode {

    CORE_ADMIN("core.admin"),
    CORE_RELOAD("core.reload"),
    CORE_DEBUG("core.debug"),
    CORE_SAVE("core.save"),
    CORE_PLAYER("core.player"),
    GAMEMODE("gamemode"),
    FLY("fly"),
    FLY_OTHER("fly.other"),
    VANISH("vanish"),
    VANISH_SEE("vanish.see"),


    MAINTENANCE_BYPASS("maintenance.bypass"),
    MAINTENANCE_MANAGE("maintenance.manage"),

    CHAT_BYPASS("chat.bypass"),
    CHAT_MODERATION("chat.moderation"),

    ECONOMY_ADMIN("economy.admin"),
    RANK_MANAGE("rank.manage"),

    CLAN_CREATE("clan.create"),
    CLAN_DELETE("clan.delete"),
    CLAN_ADMIN("clan.admin"),

    BAN_MANAGE("ban.manage"),
    BAN_INFO("ban.info"),

    REPORT_CREATE("report.create"),
    REPORT_STAFF("report.staff"),
    REPORT_CLOSE("report.close"),

    PAY_USE("pay.use"),
    CLAN_BANK_DEPOSIT("clan.bank.deposit"),
    CLAN_BANK_WITHDRAW("clan.bank.withdraw"),

    CHAT_COLOR("chat.color"),
    CHAT_FORMAT("chat.format");


    private final String node;

    PermissionNode(String node) {
        this.node = "meetlounge." + node;
    }

    public String node() {
        return node;
    }
}
