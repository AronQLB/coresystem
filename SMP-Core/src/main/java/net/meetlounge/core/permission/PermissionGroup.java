package net.meetlounge.core.permission;

import net.meetlounge.core.rank.Rank;

import java.util.EnumSet;
import java.util.Set;

public enum PermissionGroup {

    PLAYER(Rank.PLAYER,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT
    ),

    MEET(Rank.MEET,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT
    ),

    LOUNGE(Rank.LOUNGE,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.CHAT_COLOR,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT
    ),

    CONTENT(Rank.CONTENT,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.CHAT_COLOR,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT,
            PermissionNode.CHAT_BYPASS,
            PermissionNode.MAINTENANCE_BYPASS,
            PermissionNode.VANISH,
            PermissionNode.VANISH_SEE,
            PermissionNode.CHAT_MODERATION
    ),

    SUPPORTER(Rank.SUPPORTER,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.CHAT_COLOR,
            PermissionNode.MAINTENANCE_BYPASS,
            PermissionNode.FLY,
            PermissionNode.FLY_OTHER,
            PermissionNode.VANISH,
            PermissionNode.VANISH_SEE,
            PermissionNode.BAN_INFO,
            PermissionNode.REPORT_STAFF,
            PermissionNode.REPORT_CLOSE,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT,
            PermissionNode.CHAT_BYPASS,
            PermissionNode.CHAT_MODERATION
    ),

    MODERATOR(Rank.MODERATOR,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.CHAT_COLOR,
            PermissionNode.MAINTENANCE_BYPASS,
            PermissionNode.FLY,
            PermissionNode.FLY_OTHER,
            PermissionNode.VANISH,
            PermissionNode.VANISH_SEE,
            PermissionNode.CORE_PLAYER,
            PermissionNode.BAN_MANAGE,
            PermissionNode.BAN_INFO,
            PermissionNode.REPORT_STAFF,
            PermissionNode.REPORT_CLOSE,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT,
            PermissionNode.CHAT_BYPASS,
            PermissionNode.CHAT_MODERATION
    ),

    DEVELOPER(Rank.DEVELOPER,
            PermissionNode.CLAN_CREATE,
            PermissionNode.CLAN_DELETE,
            PermissionNode.CLAN_ADMIN,
            PermissionNode.CHAT_COLOR,
            PermissionNode.CHAT_FORMAT,
            PermissionNode.MAINTENANCE_BYPASS,
            PermissionNode.MAINTENANCE_MANAGE,
            PermissionNode.FLY,
            PermissionNode.FLY_OTHER,
            PermissionNode.VANISH,
            PermissionNode.VANISH_SEE,
            PermissionNode.CORE_PLAYER,
            PermissionNode.BAN_MANAGE,
            PermissionNode.BAN_INFO,
            PermissionNode.REPORT_STAFF,
            PermissionNode.REPORT_CLOSE,
            PermissionNode.PAY_USE,
            PermissionNode.CLAN_BANK_DEPOSIT,
            PermissionNode.CHAT_BYPASS,
            PermissionNode.CHAT_MODERATION
    ),
    ADMIN(Rank.ADMIN,
            PermissionNode.values()
    );

    private final Rank rank;
    private final Set<PermissionNode> permissions;

    PermissionGroup(Rank rank, PermissionNode... permissions) {
        this.rank = rank;
        this.permissions = permissions.length == PermissionNode.values().length
                ? EnumSet.allOf(PermissionNode.class)
                : EnumSet.noneOf(PermissionNode.class);

        for (PermissionNode permission : permissions) {
            this.permissions.add(permission);
        }
    }

    public Rank rank() {
        return rank;
    }

    public boolean has(PermissionNode node) {
        return permissions.contains(node);
    }

    public boolean has(String permission) {
        for (PermissionNode node : permissions) {
            if (node.node().equalsIgnoreCase(permission)) {
                return true;
            }
        }

        return false;
    }

    public Set<String> nodes() {
        Set<String> nodes = new java.util.LinkedHashSet<>();

        for (PermissionNode node : permissions) {
            nodes.add(node.node());
        }

        return nodes;
    }

    public static PermissionGroup fromRank(Rank rank) {
        for (PermissionGroup group : values()) {
            if (group.rank == rank) {
                return group;
            }
        }

        return PLAYER;
    }
}
