package net.meetlounge.core.report;

import java.util.UUID;

public final class ReportData {

    private final int id;
    private final UUID reporterUuid;
    private final String reporterName;
    private final UUID targetUuid;
    private final String targetName;
    private final String reason;
    private final long createdAt;
    private final boolean open;

    public ReportData(int id, UUID reporterUuid, String reporterName, UUID targetUuid, String targetName,
                      String reason, long createdAt, boolean open) {
        this.id = id;
        this.reporterUuid = reporterUuid;
        this.reporterName = reporterName;
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.reason = reason;
        this.createdAt = createdAt;
        this.open = open;
    }

    public int id() {
        return id;
    }

    public UUID reporterUuid() {
        return reporterUuid;
    }

    public String reporterName() {
        return reporterName;
    }

    public UUID targetUuid() {
        return targetUuid;
    }

    public String targetName() {
        return targetName;
    }

    public String reason() {
        return reason;
    }

    public long createdAt() {
        return createdAt;
    }

    public boolean open() {
        return open;
    }
}