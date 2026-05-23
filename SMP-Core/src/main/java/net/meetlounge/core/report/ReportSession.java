package net.meetlounge.core.report;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ReportSession {

    private final Map<UUID, UUID> targets = new HashMap<>();

    public void setTarget(UUID reporter, UUID target) {
        targets.put(reporter, target);
    }

    public UUID getTarget(UUID reporter) {
        return targets.get(reporter);
    }

    public void clear(UUID reporter) {
        targets.remove(reporter);
    }
}