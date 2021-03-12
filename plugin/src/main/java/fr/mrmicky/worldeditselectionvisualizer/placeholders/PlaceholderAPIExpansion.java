package fr.mrmicky.worldeditselectionvisualizer.placeholders;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    private final WorldEditSelectionVisualizer plugin;

    public PlaceholderAPIExpansion(WorldEditSelectionVisualizer plugin) {
        this.plugin = plugin;
    }

    public void registerExpansion() {
        if (register()) {
            plugin.getLogger().info("PlaceholderAPI extension successfully registered.");
        }
    }

    @NotNull
    @Override
    public String getName() {
        return plugin.getName();
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "wesv";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @NotNull
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @NotNull
    @Override
    public List<String> getPlaceholders() {
        return Arrays.asList("%wesv_toggled_selection%", "%wesv_toggled_clipboard%");
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("toggled_selection")) {
            return translateBool(plugin.getPlayerInfos(player).isSelectionVisible(SelectionType.SELECTION));
        }

        if (identifier.equals("toggled_clipboard")) {
            return translateBool(plugin.getPlayerInfos(player).isSelectionVisible(SelectionType.CLIPBOARD));
        }

        return null;
    }

    private String translateBool(boolean bool) {
        return bool ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
    }
}
