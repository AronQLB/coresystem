package net.meetlounge.core.clan;

import net.meetlounge.core.Core;
import org.bukkit.entity.Player;

import java.util.*;

public final class ClanService {

    private final Core plugin;
    private final ClanRepository repository;
    private final Map<UUID, Clan> cache = new HashMap<>();
    private final ClanInviteService inviteService = new ClanInviteService();

    public ClanService(Core plugin, ClanRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public Optional<Clan> getClan(Player player) {
        return getClan(player.getUniqueId());
    }

    public Optional<Clan> getClan(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return Optional.ofNullable(cache.get(uuid));
        }

        Optional<Clan> clan = repository.findByPlayer(uuid);
        cache.put(uuid, clan.orElse(null));

        return clan;
    }

    public CreateClanResult createClan(Player player, String name, String tag) {
        int cost = plugin.configs().config().get().getInt("clans.creation-cost", 10000);
        int tagLength = plugin.configs().config().get().getInt("clans.tag-length", 4);

        if (repository.isInClan(player.getUniqueId())) {
            return CreateClanResult.ALREADY_IN_CLAN;
        }

        if (name.length() < 3 || name.length() > 32) {
            return CreateClanResult.INVALID_NAME;
        }

        if (tag.length() != tagLength || !tag.matches("[A-Za-z]+")) {
            return CreateClanResult.INVALID_TAG;
        }

        if (repository.existsName(name)) {
            return CreateClanResult.NAME_EXISTS;
        }

        if (repository.existsTag(tag)) {
            return CreateClanResult.TAG_EXISTS;
        }

        if (!plugin.economy().has(player.getUniqueId(), cost)) {
            return CreateClanResult.NOT_ENOUGH_COINS;
        }

        boolean removed = plugin.economy().remove(player.getUniqueId(), cost);

        if (!removed) {
            return CreateClanResult.NOT_ENOUGH_COINS;
        }

        repository.createClan(name, tag, player.getUniqueId(), player.getName());

        cache.remove(player.getUniqueId());
        getClan(player);

        return CreateClanResult.SUCCESS;
    }

    public boolean deleteClan(Player player) {
        Optional<Clan> clanOptional = getClan(player);

        if (clanOptional.isEmpty()) {
            return false;
        }

        Clan clan = clanOptional.get();

        if (!clan.ownerUuid().equals(player.getUniqueId())) {
            return false;
        }

        repository.deleteClan(clan.id());
        cache.clear();

        return true;
    }

    public boolean deposit(Player player, double amount) {
        if (amount <= 0) {
            return false;
        }

        Optional<Clan> clanOptional = getClan(player);

        if (clanOptional.isEmpty()) {
            return false;
        }

        if (!plugin.economy().has(player.getUniqueId(), amount)) {
            return false;
        }

        Clan clan = clanOptional.get();

        if (!plugin.economy().remove(player.getUniqueId(), amount)) {
            return false;
        }

        double newBank = clan.bank() + amount;
        repository.updateBank(clan.id(), newBank);

        clearCache();
        return true;
    }

    public boolean withdraw(Player player, double amount) {
        if (amount <= 0) {
            return false;
        }

        Optional<Clan> clanOptional = getClan(player);

        if (clanOptional.isEmpty()) {
            return false;
        }

        Clan clan = clanOptional.get();

        if (!clan.ownerUuid().equals(player.getUniqueId())) {
            return false;
        }

        if (clan.bank() < amount) {
            return false;
        }

        double newBank = clan.bank() - amount;
        repository.updateBank(clan.id(), newBank);
        plugin.economy().add(player.getUniqueId(), amount);

        clearCache();
        return true;
    }

    public boolean invite(Player inviter, Player target) {
        Optional<Clan> clanOptional = getClan(inviter);

        if (clanOptional.isEmpty()) {
            return false;
        }

        Optional<ClanMember> inviterMember = repository.findMember(inviter.getUniqueId());

        if (inviterMember.isEmpty() || !inviterMember.get().role().isAtLeast(ClanRole.MODERATOR)) {
            return false;
        }

        if (repository.isInClan(target.getUniqueId())) {
            return false;
        }

        inviteService.invite(target.getUniqueId(), clanOptional.get().id());
        return true;
    }

    public boolean acceptInvite(Player player) {
        Integer clanId = inviteService.getInvite(player.getUniqueId());

        if (clanId == null) {
            return false;
        }

        if (repository.isInClan(player.getUniqueId())) {
            inviteService.clear(player.getUniqueId());
            return false;
        }

        Optional<Clan> clanOptional = repository.findById(clanId);

        if (clanOptional.isEmpty()) {
            inviteService.clear(player.getUniqueId());
            return false;
        }

        repository.addMember(player.getUniqueId(), player.getName(), clanId, ClanRole.MEMBER);
        inviteService.clear(player.getUniqueId());
        clearCache(player.getUniqueId());

        return true;
    }

    public boolean denyInvite(Player player) {
        if (!inviteService.hasInvite(player.getUniqueId())) {
            return false;
        }

        inviteService.clear(player.getUniqueId());
        return true;
    }

    public boolean leave(Player player) {
        Optional<ClanMember> memberOptional = repository.findMember(player.getUniqueId());

        if (memberOptional.isEmpty()) {
            return false;
        }

        if (memberOptional.get().role() == ClanRole.OWNER) {
            return false;
        }

        repository.removeMember(player.getUniqueId());
        clearCache(player.getUniqueId());
        return true;
    }

    public boolean kick(Player actor, Player target) {
        Optional<ClanMember> actorMember = repository.findMember(actor.getUniqueId());
        Optional<ClanMember> targetMember = repository.findMember(target.getUniqueId());

        if (actorMember.isEmpty() || targetMember.isEmpty()) {
            return false;
        }

        if (actorMember.get().clanId() != targetMember.get().clanId()) {
            return false;
        }

        if (!actorMember.get().role().isAtLeast(ClanRole.MODERATOR)) {
            return false;
        }

        if (targetMember.get().role().isAtLeast(actorMember.get().role())) {
            return false;
        }

        repository.removeMember(target.getUniqueId());
        clearCache(target.getUniqueId());
        return true;
    }

    public boolean promote(Player actor, Player target) {
        Optional<ClanMember> actorMember = repository.findMember(actor.getUniqueId());
        Optional<ClanMember> targetMember = repository.findMember(target.getUniqueId());

        if (actorMember.isEmpty() || targetMember.isEmpty()) {
            return false;
        }

        if (actorMember.get().clanId() != targetMember.get().clanId()) {
            return false;
        }

        if (actorMember.get().role() != ClanRole.OWNER) {
            return false;
        }

        if (targetMember.get().role() != ClanRole.MEMBER) {
            return false;
        }

        repository.updateMemberRole(target.getUniqueId(), ClanRole.MODERATOR);
        return true;
    }

    public boolean demote(Player actor, Player target) {
        Optional<ClanMember> actorMember = repository.findMember(actor.getUniqueId());
        Optional<ClanMember> targetMember = repository.findMember(target.getUniqueId());

        if (actorMember.isEmpty() || targetMember.isEmpty()) {
            return false;
        }

        if (actorMember.get().clanId() != targetMember.get().clanId()) {
            return false;
        }

        if (actorMember.get().role() != ClanRole.OWNER) {
            return false;
        }

        if (targetMember.get().role() != ClanRole.MODERATOR) {
            return false;
        }

        repository.updateMemberRole(target.getUniqueId(), ClanRole.MEMBER);
        return true;
    }

    public List<ClanMember> members(Player player) {
        Optional<Clan> clanOptional = getClan(player);

        if (clanOptional.isEmpty()) {
            return List.of();
        }

        return repository.findMembers(clanOptional.get().id());
    }

    public void addKill(Player player) {
        getClan(player).ifPresent(clan -> {
            repository.addKill(clan.id());
            clearCache();
        });
    }

    public void addDeath(Player player) {
        getClan(player).ifPresent(clan -> {
            repository.addDeath(clan.id());
            clearCache();
        });
    }

    public void clearCache(UUID uuid) {
        cache.remove(uuid);
    }

    public void clearCache() {
        cache.clear();
    }
    public ClanInviteService invites() {
        return inviteService;
    }

}