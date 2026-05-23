package net.meetlounge.core.util;


import org.bukkit.ChatColor;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile(
            "<gradient:#([A-Fa-f0-9]{6}):#([A-Fa-f0-9]{6})>(.*?)</gradient>"
    );

    private TextUtil() {}

    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        text = applyGradients(text);
        text = applyHex(text);

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static String applyHex(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(builder, toMinecraftHex("#" + hex));
        }

        matcher.appendTail(builder);
        return builder.toString();
    }

    private static String applyGradients(String text) {
        Matcher matcher = GRADIENT_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String startHex = "#" + matcher.group(1);
            String endHex = "#" + matcher.group(2);
            String content = matcher.group(3);

            String gradient = gradient(content, startHex, endHex);
            matcher.appendReplacement(builder, Matcher.quoteReplacement(gradient));
        }

        matcher.appendTail(builder);
        return builder.toString();
    }

    public static String gradient(String text, String startHex, String endHex) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Color start = Color.decode(startHex);
        Color end = Color.decode(endHex);

        StringBuilder builder = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            double ratio = length == 1 ? 0 : (double) i / (length - 1);

            int red = interpolate(start.getRed(), end.getRed(), ratio);
            int green = interpolate(start.getGreen(), end.getGreen(), ratio);
            int blue = interpolate(start.getBlue(), end.getBlue(), ratio);

            String hex = String.format("#%02x%02x%02x", red, green, blue);
            builder.append(toMinecraftHex(hex)).append(text.charAt(i));
        }

        return builder.toString();
    }

    public static String boldGradient(String text, String startHex, String endHex) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Color start = Color.decode(startHex);
        Color end = Color.decode(endHex);

        StringBuilder builder = new StringBuilder();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            double ratio = length == 1 ? 0 : (double) i / (length - 1);

            int red = interpolate(start.getRed(), end.getRed(), ratio);
            int green = interpolate(start.getGreen(), end.getGreen(), ratio);
            int blue = interpolate(start.getBlue(), end.getBlue(), ratio);

            String hex = String.format("#%02x%02x%02x", red, green, blue);

            builder.append(toMinecraftHex(hex))
                    .append("§l")
                    .append(text.charAt(i));
        }

        return builder.toString();
    }

    private static int interpolate(int start, int end, double ratio) {
        return (int) Math.round(start + (end - start) * ratio);
    }

    private static String toMinecraftHex(String hex) {
        hex = hex.replace("#", "");

        StringBuilder builder = new StringBuilder("§x");

        for (char character : hex.toCharArray()) {
            builder.append('§').append(character);
        }

        return builder.toString();
    }
}