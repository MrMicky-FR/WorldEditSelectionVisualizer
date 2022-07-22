package fr.mrmicky.worldeditselectionvisualizer.utils;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public final class ChatUtils {

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull String color(@NotNull String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
