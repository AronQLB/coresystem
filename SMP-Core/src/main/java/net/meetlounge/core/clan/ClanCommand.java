package net.meetlounge.core.clan;

import net.meetlounge.core.Core;
import net.meetlounge.core.clan.claim.ClanClaim;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Optional;

public final class ClanCommand extends AbstractCommand {

    private static final DecimalFormat KD_FORMAT = new DecimalFormat("0.00");

    public ClanCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (context.length() == 0) {
            sendHelp(context);
            return;
        }

        switch (context.arg(0).toLowerCase()) {
            case "create" -> create(context);
            case "info" -> info(context);
            case "delete" -> delete(context);
            case "bank" -> bank(context);
            case "deposit" -> deposit(context);
            case "withdraw" -> withdraw(context);
            case "invite" -> invite(context);
            case "accept" -> accept(context);
            case "deny" -> deny(context);
            case "leave" -> leave(context);
            case "kick" -> kick(context);
            case "members" -> members(context);
            case "promote" -> promote(context);
            case "demote" -> demote(context);
            case "claim" -> claim(context);
            default -> sendHelp(context);
        }
    }

    private void sendHelp(CommandContext context) {
        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aClan Hilfe");
        context.raw(Core.prefix + "&7/clan create <name> <tag>");
        context.raw(Core.prefix + "&7/clan info");
        context.raw(Core.prefix + "&7/clan delete");
        context.raw(Core.prefix + "&7/clan invite <spieler>");
        context.raw(Core.prefix + "&7/clan accept");
        context.raw(Core.prefix + "&7/clan deny");
        context.raw(Core.prefix + "&7/clan leave");
        context.raw(Core.prefix + "&7/clan kick <spieler>");
        context.raw(Core.prefix + "&7/clan members");
        context.raw(Core.prefix + "&7/clan promote <spieler>");
        context.raw(Core.prefix + "&7/clan demote <spieler>");
        context.raw(Core.prefix + "&7");
        context.raw(Core.prefix + "&7/clan bank");
        context.raw(Core.prefix + "&7/clan deposit <coins>");
        context.raw(Core.prefix + "&7/clan withdraw <coins>");
        context.raw(Core.prefix + "&9");
        context.raw(Core.prefix + "&7/clan claim  - Teleportiert dich zur Clan-Base");
        context.raw(Core.prefix + "&7/clan claim create");
        context.raw(Core.prefix + "&7/clan claim delete");
        context.raw(Core.prefix + "&7/clan claim show");
        context.raw(Core.prefix + "&8&m----------------------------");
    }

    private void create(CommandContext context) {
        if (context.length() < 3) {
            context.raw(Core.prefix + "&7Nutze: /clan create <name> <tag>");
            return;
        }

        CreateClanResult result = plugin.clans().createClan(
                context.player(),
                context.arg(1),
                context.arg(2)
        );

        switch (result) {
            case SUCCESS -> context.raw(Core.prefix + "&aClan &7wurde erfolgreich erstellt.");
            case ALREADY_IN_CLAN -> context.raw(Core.prefix + "&7Du bist bereits in einem &aClan&7.");
            case INVALID_NAME -> context.raw(Core.prefix + "&7Der &aClanname &7muss &c3-32 &7Zeichen lang sein.");
            case INVALID_TAG -> context.raw(Core.prefix + "&7Das &aKürzel &7muss genau &c4 &7Buchstaben haben.");
            case NAME_EXISTS -> context.raw(Core.prefix + "&7Dieser &aClanname &7ist bereits vergeben.");
            case TAG_EXISTS -> context.raw(Core.prefix + "&7Dieses &aKürzel &7ist bereits vergeben.");
            case NOT_ENOUGH_COINS -> context.raw(Core.prefix + "7cDu brauchst &c10.000 Coins&7, um einen &aClan &7zu erstellen.");
        }
    }

    private void info(CommandContext context) {
        Optional<Clan> clanOptional = plugin.clans().getClan(context.player());

        if (clanOptional.isEmpty()) {
            context.raw(Core.prefix + "&7Du bist in keinem Clan.");
            return;
        }

        Clan clan = clanOptional.get();

        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aClan: &f" + clan.name() + " &8[&a" + clan.tag() + "&8]");
        context.raw(Core.prefix + "&7Ersteller: &f" + clan.ownerName());
        context.raw(Core.prefix + "&7Kills: &f" + clan.kills());
        context.raw(Core.prefix + "&7Deaths: &f" + clan.deaths());
        context.raw(Core.prefix + "&7K/D: &f" + KD_FORMAT.format(clan.kd()));
        context.raw(Core.prefix + "&7Clankasse: &a" + clan.bank());
        context.raw(Core.prefix + "&8&m----------------------------");
    }

    private void delete(CommandContext context) {
        boolean deleted = plugin.clans().deleteClan(context.player());

        if (!deleted) {
            context.raw(Core.prefix + "&cDu bist kein Clan-Ersteller oder in keinem Clan.");
            return;
        }

        context.raw(Core.prefix + "&aClan wurde gelöscht.");
    }

    private void bank(CommandContext context) {
        var clanOptional = plugin.clans().getClan(context.player());

        if (clanOptional.isEmpty()) {
            context.raw(Core.prefix + "&cDu bist in keinem Clan.");
            return;
        }

        var clan = clanOptional.get();

        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aClankasse");
        context.raw(Core.prefix + "&7Clan: &f" + clan.name());
        context.raw(Core.prefix + "&7Kontostand: &a" + clan.bank() + " Coins");
        context.raw(Core.prefix + "&8&m----------------------------");
    }

    private void deposit(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /clan deposit <coins>");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(context.arg(1));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cBitte gib eine gültige Zahl ein.");
            return;
        }

        boolean success = plugin.clans().deposit(context.player(), amount);

        if (!success) {
            context.raw(Core.prefix + "&cEinzahlung fehlgeschlagen. &7Prüfe Clan, Betrag und Coins.");
            return;
        }

        context.raw(Core.prefix + "&7Du hast &f" + amount + " &7Coins in die Clankasse eingezahlt.");
    }

    private void withdraw(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /clan withdraw <coins>");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(context.arg(1));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cBitte gib eine gültige Zahl ein.");
            return;
        }

        boolean success = plugin.clans().withdraw(context.player(), amount);

        if (!success) {
            context.raw(Core.prefix + "&cAuszahlung fehlgeschlagen. &7Nur der Clan-Ersteller kann auszahlen.");
            return;
        }


        context.raw(Core.prefix + "&7Du hast &f" + amount + " &7Coins &aaus der Clankasse ausgezahlt.");
    }

    private void invite(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&Nutze: /clan invite <spieler>");
            return;
        }

        Player target = org.bukkit.Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        if (target.getUniqueId().equals(context.player().getUniqueId())) {
            context.raw(Core.prefix + "&cDu kannst dich nicht selbst einladen.");
            return;
        }

        boolean success = plugin.clans().invite(context.player(), target);

        if (!success) {
            context.raw(Core.prefix + "&cEinladung fehlgeschlagen. &7Du brauchst Clan-Moderator Rechte oder der Spieler ist schon in einem Clan.");
            return;
        }

        context.raw("&aDu hast &f" + target.getName() + " &ain deinen Clan eingeladen.");
        target.sendMessage(plugin.messages().raw(
                Core.prefix + "&7Du wurdest in einen Clan eingeladen. &7Nutze &f/clan accept &7oder &f/clan deny"
        ));
    }

    private void accept(CommandContext context) {
        boolean success = plugin.clans().acceptInvite(context.player());

        if (!success) {
            context.raw(Core.prefix + "&cDu hast keine offene Clan-Einladung.");
            return;
        }

        context.raw(Core.prefix + "&aDu bist dem Clan beigetreten.");
    }

    private void deny(CommandContext context) {
        boolean success = plugin.clans().denyInvite(context.player());

        if (!success) {
            context.raw(Core.prefix + "&cDu hast keine offene Clan-Einladung.");
            return;
        }

        context.raw(Core.prefix + "&cDu hast die Clan-Einladung abgelehnt.");
    }

    private void leave(CommandContext context) {
        boolean success = plugin.clans().leave(context.player());

        if (!success) {
            context.raw(Core.prefix + "&cDu bist in keinem Clan oder bist der Ersteller. Ersteller müssen den Clan löschen.");
            return;
        }

        context.raw(Core.prefix + "&aDu hast deinen Clan verlassen.");
    }

    private void kick(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /clan kick <spieler>");
            return;
        }

        Player target = org.bukkit.Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        boolean success = plugin.clans().kick(context.player(), target);

        if (!success) {
            context.raw(Core.prefix + "&cDu kannst diesen Spieler nicht aus dem Clan werfen.");
            return;
        }

        context.raw(Core.prefix + "&aDu hast &f" + target.getName() + " &aaus dem Clan geworfen.");
        target.sendMessage(plugin.messages().raw("&cDu wurdest aus deinem Clan geworfen."));
    }

    private void members(CommandContext context) {
        var members = plugin.clans().members(context.player());

        if (members.isEmpty()) {
            context.raw(Core.prefix + "&cDu bist in keinem Clan.");
            return;
        }

        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aClan Mitglieder");

        for (ClanMember member : members) {
            context.raw("&7- &f" + member.name() + " &8| &a" + member.role().displayName());
        }

        context.raw(Core.prefix + "&8&m----------------------------");
    }

    private void promote(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /clan promote <spieler>");
            return;
        }

        Player target = org.bukkit.Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        boolean success = plugin.clans().promote(context.player(), target);

        if (!success) {
            context.raw(Core.prefix + "&cDu kannst diesen Spieler nicht befördern.");
            return;
        }

        context.raw(Core.prefix + "&aSpieler wurde zum Clan-Moderator befördert.");
    }

    private void demote(CommandContext context) {
        if (context.length() < 2) {
            context.raw(Core.prefix + "&cNutze: /clan demote <spieler>");
            return;
        }

        Player target = org.bukkit.Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        boolean success = plugin.clans().demote(context.player(), target);

        if (!success) {
            context.raw(Core.prefix + "&cDu kannst diesen Spieler nicht degradieren.");
            return;
        }

        context.raw(Core.prefix + "&aSpieler wurde zum Clan-Mitglied degradiert.");
    }

    private void claim(CommandContext context) {
        if (context.length() == 1) {
            boolean teleported = plugin.claims().teleport(context.player());

            if (!teleported) {
                context.raw("&cDein Clan hat kein Claim.");
                return;
            }

            context.raw("&aDu wurdest zum Clan-Claim teleportiert.");
            return;
        }

        switch (context.arg(1).toLowerCase()) {
            case "create" -> {
                boolean created = plugin.claims().create(context.player());

                if (!created) {
                    context.raw("&cClaim konnte nicht erstellt werden. Nur der Clan-Ersteller kann ein Claim setzen und jeder Clan darf nur ein Claim besitzen.");
                    return;
                }

                context.raw("&aClan-Claim wurde erstellt. &7Größe: &f32x32");
            }

            case "delete" -> {
                boolean deleted = plugin.claims().delete(context.player());

                if (!deleted) {
                    context.raw("&cClaim konnte nicht gelöscht werden. Nur der Clan-Ersteller kann das Claim löschen.");
                    return;
                }

                context.raw("&aClan-Claim wurde gelöscht.");
            }

            case "show" -> {

                var clanOptional = plugin.clans().getClan(context.player());

                if (clanOptional.isEmpty()) {
                    context.raw(Core.prefix + "&7Du bist in keinem Clan.");
                    return;
                }

                var claimOptional = plugin.claims().getClanClaim(clanOptional.get().id());

                if (claimOptional.isEmpty()) {
                    context.raw(Core.prefix + "&7Dein Clan besitzt kein Claim.");
                    return;
                }

                plugin.claimVisualizer().show(context.player(), claimOptional.get());

                context.raw(Core.prefix + "&7Claim-Grenzen werden angezeigt.");
            }

            default -> context.raw("&cNutze: /clan claim, /clan claim create, /clan claim delete");
        }
    }
}
