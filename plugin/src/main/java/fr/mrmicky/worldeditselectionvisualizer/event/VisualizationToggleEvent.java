package fr.mrmicky.worldeditselectionvisualizer.event;

import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

/**
 * Called when a player toggles a visualization with {@code /wesv toggle}.
 * Fired after the new state is applied, and not when a saved state is restored on join.
 */
@NullMarked
public class VisualizationToggleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SelectionType selectionType;
    private final boolean enabled;

    /**
     * Creates a new {@link VisualizationToggleEvent} for the given player with the specified enabled state.
     *
     * @param player        the player whose visualization was toggled
     * @param selectionType the type of visualization that was toggled
     * @param enabled       true if the visualization is enabled, false if it is disabled
     */
    public VisualizationToggleEvent(Player player, SelectionType selectionType, boolean enabled) {
        this.player = Objects.requireNonNull(player, "player");
        this.selectionType = Objects.requireNonNull(selectionType, "selectionType");
        this.enabled = enabled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the player whose visualization was toggled.
     *
     * @return the player whose visualization was toggled
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns which visualization was toggled, either the WorldEdit selection or the clipboard.
     *
     * @return the type of visualization that was toggled
     */
    public SelectionType getSelectionType() {
        return this.selectionType;
    }

    /**
     * Returns whether the visualization is enabled or disabled.
     *
     * @return true if the visualization is enabled, false if it is disabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
