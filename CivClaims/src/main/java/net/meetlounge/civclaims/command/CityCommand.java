package net.meetlounge.civclaims.command;

import net.meetlounge.civclaims.model.City;
import net.meetlounge.civclaims.service.CityService;
import net.meetlounge.civclaims.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CityCommand implements CommandExecutor, TabCompleter {

    private final CityService cityService;

    public CityCommand(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl benutzen.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> createCity(player, args);
            case "claim" -> claim(player);
            case "unclaim" -> unclaim(player);
            case "info" -> info(player);
            case "invite" -> invite(player, args);
            case "list" -> list(player);
            default -> sendHelp(player);
        }
        return true;
    }

    private void createCity(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Messages.text("Nutze: /city create <name>"));
            return;
        }

        boolean created = cityService.createCity(player, args[1]);
        if (!created) {
            player.sendMessage(Messages.text("Du bist bereits in einer Stadt oder der Name existiert schon."));
            return;
        }
        player.sendMessage(Messages.text("Stadt " + args[1] + " wurde gegründet."));
    }

    private void claim(Player player) {
        CityService.ClaimResult result = cityService.claim(player);
        switch (result) {
            case SUCCESS -> player.sendMessage(Messages.text("Dieser Chunk gehört jetzt deiner Stadt."));
            case NO_CITY -> player.sendMessage(Messages.text("Du bist in keiner Stadt."));
            case NO_PERMISSION -> player.sendMessage(Messages.text("Du darfst keine Claims verwalten."));
            case ALREADY_CLAIMED -> player.sendMessage(Messages.text("Dieser Chunk ist bereits geclaimt."));
            default -> player.sendMessage(Messages.text("Claim fehlgeschlagen."));
        }
    }

    private void unclaim(Player player) {
        CityService.ClaimResult result = cityService.unclaim(player);
        switch (result) {
            case SUCCESS -> player.sendMessage(Messages.text("Der Chunk wurde freigegeben."));
            case NO_CITY -> player.sendMessage(Messages.text("Du bist in keiner Stadt."));
            case NO_PERMISSION -> player.sendMessage(Messages.text("Du darfst keine Claims verwalten."));
            case NOT_CLAIMED_BY_CITY -> player.sendMessage(Messages.text("Dieser Chunk gehört nicht deiner Stadt."));
            default -> player.sendMessage(Messages.text("Unclaim fehlgeschlagen."));
        }
    }

    private void info(Player player) {
        Optional<City> city = cityService.cityAt(player.getLocation());
        if (city.isEmpty()) {
            player.sendMessage(Messages.text("Du stehst in der Wildnis."));
            return;
        }

        City currentCity = city.get();
        player.sendMessage(Messages.text("Stadt: " + currentCity.name()));
        player.sendMessage(Messages.text("Claims: " + currentCity.claims().size()));
        player.sendMessage(Messages.text("Mitglieder: " + currentCity.members().size()));
        player.sendMessage(Messages.text("PvP: " + (currentCity.pvpAllowed() ? "an" : "aus")));
    }

    private void invite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Messages.text("Nutze: /city invite <spieler>"));
            return;
        }

        Optional<City> optionalCity = cityService.cityOf(player.getUniqueId());
        if (optionalCity.isEmpty()) {
            player.sendMessage(Messages.text("Du bist in keiner Stadt."));
            return;
        }

        City city = optionalCity.get();
        if (!city.roleOf(player.getUniqueId()).orElseThrow().canManageClaims()) {
            player.sendMessage(Messages.text("Du darfst keine Spieler einladen."));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage(Messages.text("Spieler nicht gefunden."));
            return;
        }

        if (cityService.cityOf(target.getUniqueId()).isPresent()) {
            player.sendMessage(Messages.text("Dieser Spieler ist bereits in einer Stadt."));
            return;
        }

        city.addMember(target.getUniqueId());
        cityService.save();
        player.sendMessage(Messages.text(target.getName() + " ist jetzt Bürger deiner Stadt."));
        target.sendMessage(Messages.text("Du bist der Stadt " + city.name() + " beigetreten."));
    }

    private void list(Player player) {
        player.sendMessage(Messages.text("Städte:"));
        for (City city : cityService.cities()) {
            player.sendMessage("- " + city.name() + " | Claims: " + city.claims().size() + " | Bürger: " + city.members().size());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(Messages.text("/city create <name>"));
        player.sendMessage(Messages.text("/city claim"));
        player.sendMessage(Messages.text("/city unclaim"));
        player.sendMessage(Messages.text("/city info"));
        player.sendMessage(Messages.text("/city invite <spieler>"));
        player.sendMessage(Messages.text("/city list"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("create", "claim", "unclaim", "info", "invite", "list", "help");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        }
        return List.of();
    }
}
