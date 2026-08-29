package fr.mrmicky.worldeditselectionvisualizer.selection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Contains the visualizer data of a connected {@link Player}.
 */
@NullMarked
public class PlayerVisualizerData {

    private final Map<SelectionType, PlayerSelection> enabledVisualizations = new EnumMap<>(SelectionType.class);

    private final Player player;

    private volatile @Nullable Location clipboardLockLocation;
    private volatile boolean holdingSelectionItem = true;

    public PlayerVisualizerData(Player player) {
        this.player = Objects.requireNonNull(player, "player");
    }

    public Optional<PlayerSelection> getSelection(SelectionType type) {
        return Optional.ofNullable(this.enabledVisualizations.get(type));
    }

    public Collection<PlayerSelection> getSelections() {
        return this.enabledVisualizations.values();
    }

    public Player getPlayer() {
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

    public void setSelectionVisible(SelectionType type, boolean visible) {
        if (!visible) {
            this.enabledVisualizations.remove(type);
            return;
        }

        this.enabledVisualizations.computeIfAbsent(type, PlayerSelection::new);
    }

    public @Nullable Location getClipboardLockLocation() {
        return this.clipboardLockLocation;
    }

    public void setClipboardLockLocation(@Nullable Location location) {
        this.clipboardLockLocation = location;
    }
}
