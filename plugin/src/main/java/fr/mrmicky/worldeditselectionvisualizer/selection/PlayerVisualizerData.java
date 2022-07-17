package fr.mrmicky.worldeditselectionvisualizer.selection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains the visualizer data of a connected {@link Player}.
 */
public class PlayerVisualizerData {

    private final Map<SelectionType, PlayerSelection> enabledVisualizations = new EnumMap<>(SelectionType.class);

    @NotNull
    private final Player player;

    @Nullable
    private Location clipboardLockLocation;

    private boolean holdingSelectionItem = true;

    public PlayerVisualizerData(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "player");
    }

    @NotNull
    public Optional<PlayerSelection> getSelection(SelectionType type) {
        return Optional.ofNullable(enabledVisualizations.get(type));
    }

    @NotNull
    public Collection<PlayerSelection> getEnabledVisualizations() {
        return enabledVisualizations.values();
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public boolean isHoldingSelectionItem() {
        return holdingSelectionItem;
    }

    public void setHoldingSelectionItem(boolean holdingSelectionItem) {
        this.holdingSelectionItem = holdingSelectionItem;
    }

    public boolean isSelectionVisible(SelectionType type) {
        return enabledVisualizations.containsKey(type);
    }

    public void toggleSelectionVisibility(SelectionType type, boolean enable) {
        if (!enable) {
            enabledVisualizations.remove(type);
            return;
        }

        enabledVisualizations.computeIfAbsent(type, PlayerSelection::new);
    }

    public @NotNull Location getClipboardLocation() {
        if (clipboardLockLocation == null) {
            return player.getLocation();
        }

        return clipboardLockLocation;
    }

    public @Nullable Location getClipboardLockLocation() {
        return clipboardLockLocation;
    }

    public void setClipboardLockLocation(@Nullable Location location) {
        clipboardLockLocation = location;
    }
}
