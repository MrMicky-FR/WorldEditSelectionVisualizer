package fr.mrmicky.worldeditselectionvisualizer.utils;

import org.bukkit.ChatColor;

public final class ChatUtils {

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
