package net.meetlounge.core.mute;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class MuteService {

    private final Core plugin;
    private final MuteRepository repository;

    private boolean chatMuted;
    private int slowChatSeconds;

    public MuteService(Core plugin, MuteRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public void mute(Player staff, OfflinePlayer target, String reason, long durationMillis) {
        long now = System.currentTimeMillis();
        long expiresAt = durationMillis == -1 ? -1 : now + durationMillis;

        MuteData data = new MuteData(
                target.getUniqueId(),
                target.getName(),
                reason,
                staff.getName(),
                now,
                expiresAt,
                true
        );

        repository.save(data);
    }

    public void unmute(OfflinePlayer target) {
        repository.unmute(target.getUniqueId());
    }

    public Optional<MuteData> getMute(Player player) {
        Optional<MuteData> muteOptional = repository.find(player.getUniqueId());

        if (muteOptional.isEmpty()) {
            return Optional.empty();
        }

        MuteData mute = muteOptional.get();

        if (mute.expired()) {
            repository.unmute(player.getUniqueId());
            return Optional.empty();
        }

        return Optional.of(mute);
    }

    public boolean isMuted(Player player) {
        return getMute(player).isPresent();
    }

    public String muteMessage(MuteData data) {
        String expires = data.permanent()
                ? "Permanent"
                : TimeUtil.formatDateTime(data.expiresAt());

        return plugin.messages().raw("""
                
                &8&m--------------------------------
                &c&lDU WURDEST GEMUTET
                &8&m--------------------------------
                
                &7Grund: &c""" + data.reason() + """
                &7Von: &f""" + data.staff() + """
                &7Bis: &f""" + expires + """
                
                &8&m--------------------------------
                """);
    }

    public boolean chatMuted() {
        return chatMuted;
    }

    public void setChatMuted(boolean chatMuted) {
        this.chatMuted = chatMuted;
    }

    public int slowChatSeconds() {
        return slowChatSeconds;
    }

    public void setSlowChatSeconds(int slowChatSeconds) {
        this.slowChatSeconds = slowChatSeconds;
    }
}
