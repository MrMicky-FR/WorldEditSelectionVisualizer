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

    private final @NotNull Player player;

    private @Nullable Location clipboardLockLocation;
    private boolean holdingSelectionItem = true;

    public PlayerVisualizerData(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "player");
    }

    public @NotNull Optional<PlayerSelection> getSelection(SelectionType type) {
        return Optional.ofNullable(this.enabledVisualizations.get(type));
    }

    public @NotNull Collection<PlayerSelection> getEnabledVisualizations() {
        return this.enabledVisualizations.values();
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public boolean isHoldingSelectionItem() {
        return this.holdingSelectionItem;
    }

    public void setHoldingSelectionItem(boolean holdingSelectionItem) {
        this.holdingSelectionItem = holdingSelectionItem;
    }

    public boolean isSelectionVisible(SelectionType type) {
        return this.enabledVisualizations.containsKey(type);
    }

    public void toggleSelectionVisibility(SelectionType type, boolean enable) {
        if (!enable) {
            this.enabledVisualizations.remove(type);
            return;
        }

        this.enabledVisualizations.computeIfAbsent(type, PlayerSelection::new);
    }

    public @NotNull Location getClipboardLocation() {
        if (this.clipboardLockLocation == null) {
            return this.player.getLocation();
        }

        return this.clipboardLockLocation;
    }

    public @Nullable Location getClipboardLockLocation() {
        return this.clipboardLockLocation;
    }

    public void setClipboardLockLocation(@Nullable Location location) {
        this.clipboardLockLocation = location;
    }
}
