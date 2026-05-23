package net.meetlounge.core.report;

import net.meetlounge.core.Core;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public final class ReportService {

    private final Core plugin;
    private final ReportRepository repository;
    private final ReportSession session = new ReportSession();

    public ReportService(Core plugin, ReportRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public ReportSession session() {
        return session;
    }

    public int createReport(Player reporter, Player target, ReportReason reason) {
        int id = repository.create(
                reporter.getUniqueId(),
                reporter.getName(),
                target.getUniqueId(),
                target.getName(),
                reason
        );

        if (id != -1) {
            notifyStaff(id, reporter, target, reason);
        }

        return id;
    }

    public List<ReportData> openReports() {
        return repository.findOpenReports();
    }

    public void closeReport(int id) {
        repository.close(id);
    }

    private void notifyStaff(int id, Player reporter, Player target, ReportReason reason) {
        String message = plugin.messages().raw(
                Core.prefix + "&8&m----------------------------\n" +
                        Core.prefix + "&c&lNeuer Report &8#&f" + id + "\n" +
                        Core.prefix + "&7Von: &f" + reporter.getName() + "\n" +
                        Core.prefix + "&7Gegen: &f" + target.getName() + "\n" +
                        Core.prefix + "&7Grund: &c" + reason.displayName() + "\n" +
                        Core.prefix + "&7Nutze: &f/reports\n" +
                        Core.prefix + "&8&m----------------------------"
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.permissions().has(player, PermissionNode.REPORT_STAFF)) {
                player.sendMessage(message);
            }
        }
    }
}
