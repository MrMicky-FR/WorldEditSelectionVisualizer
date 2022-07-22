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
            this.plugin.getLogger().info("PlaceholderAPI extension successfully registered.");
        }
    }

    @Override
    public @NotNull String getName() {
        return this.plugin.getName();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "wesv";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", this.plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return Arrays.asList("%wesv_toggled_selection%", "%wesv_toggled_clipboard%");
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player,
                                                 @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("toggled_selection")) {
            return translateBool(this.plugin.getPlayerData(player).isSelectionVisible(SelectionType.SELECTION));
        }

        if (identifier.equals("toggled_clipboard")) {
            return translateBool(this.plugin.getPlayerData(player).isSelectionVisible(SelectionType.CLIPBOARD));
        }

        return null;
    }

    private @NotNull String translateBool(boolean bool) {
        return bool ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
    }
}
