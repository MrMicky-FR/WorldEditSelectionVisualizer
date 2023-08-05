package fr.mrmicky.worldeditselectionvisualizer.placeholders;

import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerSelection;
import fr.mrmicky.worldeditselectionvisualizer.selection.PlayerVisualizerData;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        return Arrays.asList(
                "%wesv_toggled_selection%",
                "%wesv_toggled_clipboard%",
                "%wesv_volume_selection%",
                "%wesv_volume_clipboard%"
        );
    }

    @Override
    public @Nullable String onPlaceholderRequest(@Nullable Player player,
                                                 @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        String[] args = identifier.split("_", 2);
        Optional<PlayerVisualizerData> playerData = this.plugin.getOptionalPlayerData(player);

        if (!playerData.isPresent() || args.length != 2) {
            return null;
        }

        SelectionType type = SelectionType.from(args[1]);

        return type != null ? parsePlaceholder(playerData.get(), args[0], type) : null;
    }

    private @Nullable String parsePlaceholder(@NotNull PlayerVisualizerData player,
                                              @NotNull String identifier,
                                              @NotNull SelectionType type) {
        if (identifier.equals("toggled")) {
            return player.isSelectionVisible(type)
                    ? PlaceholderAPIPlugin.booleanTrue()
                    : PlaceholderAPIPlugin.booleanFalse();
        }

        if (identifier.equals("volume")) {
            long volume = player.getSelection(type)
                    .map(PlayerSelection::getSelectedVolume)
                    .orElse(0L);

            return Long.toString(volume);
        }

        return null;
    }
}
