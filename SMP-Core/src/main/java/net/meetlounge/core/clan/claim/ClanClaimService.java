package net.meetlounge.core.clan.claim;

import net.meetlounge.core.Core;
import net.meetlounge.core.clan.Clan;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ClanClaimService {

    private static final int CLAIM_SIZE = 32;

    private final Core plugin;
    private final ClanClaimRepository repository;
    private final Map<Integer, ClanClaim> claims = new HashMap<>();

    public ClanClaimService(Core plugin, ClanClaimRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public void load() {
        claims.clear();

        for (ClanClaim claim : repository.findAll()) {
            claims.put(claim.clanId(), claim);
        }
    }

    public boolean create(Player player) {
        Optional<Clan> clanOptional = plugin.clans().getClan(player);

        if (clanOptional.isEmpty()) {
            return false;
        }

        Clan clan = clanOptional.get();

        if (!clan.ownerUuid().equals(player.getUniqueId())) {
            return false;
        }

        if (claims.containsKey(clan.id())) {
            return false;
        }

        Location location = player.getLocation();

        int centerX = location.getBlockX();
        int centerZ = location.getBlockZ();

        int half = CLAIM_SIZE / 2;

        ClanClaim claim = new ClanClaim(
                clan.id(),
                clan.name(),
                location.getWorld().getName(),
                centerX,
                centerZ,
                centerX - half,
                centerZ - half,
                centerX + half - 1,
                centerZ + half - 1,
                System.currentTimeMillis()
        );

        claims.put(clan.id(), claim);
        repository.save(claim);
        return true;
    }

    public boolean delete(Player player) {
        Optional<Clan> clanOptional = plugin.clans().getClan(player);

        if (clanOptional.isEmpty()) {
            return false;
        }

        Clan clan = clanOptional.get();

        if (!clan.ownerUuid().equals(player.getUniqueId())) {
            return false;
        }

        if (!claims.containsKey(clan.id())) {
            return false;
        }

        claims.remove(clan.id());
        repository.delete(clan.id());
        return true;
    }

    public boolean teleport(Player player) {
        Optional<Clan> clanOptional = plugin.clans().getClan(player);

        if (clanOptional.isEmpty()) {
            return false;
        }

        Clan clan = clanOptional.get();
        ClanClaim claim = claims.get(clan.id());

        if (claim == null) {
            return false;
        }

        Location location = claim.teleportLocation();

        if (location == null) {
            return false;
        }

        player.teleport(location);
        return true;
    }

    public Optional<ClanClaim> claimAt(Location location) {
        return claims.values()
                .stream()
                .filter(claim -> claim.contains(location))
                .findFirst();
    }

    public boolean canBuild(Player player, Location location) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }

        Optional<ClanClaim> claimOptional = claimAt(location);

        if (claimOptional.isEmpty()) {
            return true;
        }

        Optional<Clan> playerClan = plugin.clans().getClan(player);

        return playerClan.isPresent()
                && playerClan.get().id() == claimOptional.get().clanId();
    }

    public Optional<ClanClaim> getClanClaim(int clanId) {
        return Optional.ofNullable(claims.get(clanId));
    }
}