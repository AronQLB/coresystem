package net.meetlounge.core.ban;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class BanService {

    private final Core plugin;
    private final BanRepository repository;

    public BanService(Core plugin, BanRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public boolean ban(CommandSender staff, OfflinePlayer target, BanReason reason) {
        if (reason == null || target.getUniqueId() == null) {
            return false;
        }

        if (!canBan(staff, target)) {
            staff.sendMessage(plugin.messages().raw(
                    Core.prefix + "&7Du kannst keine &cTeammitglieder &7bannen."
            ));
            return false;
        }

        long now = System.currentTimeMillis();
        long expiresAt = reason.permanent() ? -1 : now + reason.durationMillis();

        BanData data = new BanData(
                target.getUniqueId(),
                target.getName() == null ? "Unknown" : target.getName(),
                reason.id(),
                reason.displayName(),
                staff.getName(),
                now,
                expiresAt,
                true
        );

        repository.save(data);

        Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());

        if (onlineTarget != null) {
            onlineTarget.kickPlayer(buildKickMessage(data));
        }

        return true;
    }

    public void unban(UUID uuid) {
        repository.deactivate(uuid);
    }

    private boolean canBan(CommandSender staff, OfflinePlayer target) {
        if (!(staff instanceof Player staffPlayer)) {
            return true;
        }

        if (target.getUniqueId() == null) {
            return false;
        }

        String staffRank = plugin.ranks().getRankId(staffPlayer);

        if (staffRank.equals("admin")) {
            return true;
        }

        Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());

        if (onlineTarget == null) {
            return true;
        }

        return !plugin.ranks().hasAtLeast(onlineTarget, "supporter");
    }

    public Optional<BanData> getActiveBan(UUID uuid) {
        Optional<BanData> banOptional = repository.findActive(uuid);

        if (banOptional.isEmpty()) {
            return Optional.empty();
        }

        BanData ban = banOptional.get();

        if (ban.expired()) {
            repository.deactivate(uuid);
            return Optional.empty();
        }

        return Optional.of(ban);
    }

    public String buildKickMessage(BanData data) {
        String duration = data.permanent()
                ? "Permanent"
                : TimeUtil.formatDateTime(data.expiresAt());

        String brand = plugin.configs().config().get().getString("branding.name", "SMP-Core");

        return plugin.messages().raw(
                "&cDu wurdest von " + brand + " ausgeschlossen.\n\n" +
                        "&7Grund: &f" + data.reason() + "\n" +
                        "&7Ban-ID: &f" + data.reasonId() + "\n" +
                        "&7Von: &f" + data.staff() + "\n" +
                        "&7Bis: &f" + duration + "\n\n" +
                        "&7Wenn du denkst, dass dies ein Fehler ist,\n" +
                        "&7melde dich im Discord."
        );
    }
}
