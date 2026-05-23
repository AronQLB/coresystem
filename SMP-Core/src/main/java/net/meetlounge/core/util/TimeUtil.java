package net.meetlounge.core.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    private TimeUtil() {}

    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    public static long nowSeconds() {
        return Instant.now().getEpochSecond();
    }

    public static String formatDateTime(long millis) {
        return DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(millis));
    }

    public static String formatDuration(long millis) {
        if (millis <= 0) {
            return "0s";
        }

        Duration duration = Duration.ofMillis(millis);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append("d ");
        }

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        if (minutes > 0) {
            builder.append(minutes).append("m ");
        }

        if (seconds > 0 || builder.isEmpty()) {
            builder.append(seconds).append("s");
        }

        return builder.toString().trim();
    }

    public static long secondsToMillis(long seconds) {
        return seconds * 1000L;
    }

    public static long minutesToMillis(long minutes) {
        return minutes * 60_000L;
    }

    public static long hoursToMillis(long hours) {
        return minutesToMillis(hours * 60L);
    }

    public static long daysToMillis(long days) {
        return hoursToMillis(days * 24L);
    }
}