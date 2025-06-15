package fr.mrmicky.worldeditselectionvisualizer.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public final class ChatUtils {

    private static final boolean HEX_COLORS;

    static {
        boolean hasHexColors = true;

        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
            ChatColor.class.getMethod("of", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            hasHexColors = false;
        }

        HEX_COLORS = hasHexColors;
    }

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull String color(@NotNull String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', colorHex(s));
    }

    /**
     * Convert <code>&#rrggbb</code> codes to Spigot/Bungee RGB format.
     *
     * @param s the input string
     * @return string with replaced color codes
     */
    @VisibleForTesting
    public static @NotNull String colorHex(@NotNull String s) {
        if (!HEX_COLORS) {
            return s;
        }

        for (int i = 0; i < s.length() - 7; i++) {
            if (s.charAt(i) != '&' || s.charAt(i + 1) != '#') {
                continue;
            }

            // Extract the #rrggbb color (without '&')
            String color = ChatColor.of(s.substring(i + 1, i + 8)).toString();
            // Remove the &#rrggbb (including '&') and replace it with Bungee format
            s = s.substring(0, i) + color + s.substring(i + 8);
            // Fast-forward to the end of the replaced part
            i += color.length() - 1;
        }

        return s;
    }
}
