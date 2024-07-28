package com.missyu.dev.flcombatlog.utils;

import org.bukkit.ChatColor;

public class Utils {

    public static String chat(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatTime(long milliseconds) {
        // Constants for time conversions
        final long ONE_SECOND = 1000;
        final long ONE_MINUTE = 60 * ONE_SECOND;
        final long ONE_HOUR = 60 * ONE_MINUTE;

        // Calculate hours, minutes, and seconds
        long hours = milliseconds / ONE_HOUR;
        milliseconds %= ONE_HOUR;

        long minutes = milliseconds / ONE_MINUTE;
        milliseconds %= ONE_MINUTE;

        long seconds = milliseconds / ONE_SECOND;

        // Build the formatted time string
        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours).append(" hours");
        }

        if (minutes > 0) {
            if (timeString.length() > 0) {
                timeString.append(", ");
            }
            timeString.append(minutes).append(" minutes");
        }

        if (seconds > 0) {
            if (timeString.length() > 0) {
                timeString.append(", ");
            }
            timeString.append(seconds).append(" seconds");
        }

        if (timeString.length() <= 0)
            return "0 seconds";

        return timeString.toString();
    }

}
