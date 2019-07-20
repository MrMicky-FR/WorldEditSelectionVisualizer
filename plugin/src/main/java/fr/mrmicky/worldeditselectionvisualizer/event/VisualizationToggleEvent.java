package fr.mrmicky.worldeditselectionvisualizer.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player toggled his selection visualizer
 */
public class VisualizationToggleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    private final Player player;

    private final boolean enabled;

    /**
     * Creates a new custom "VisualizationToggleEvent" event.
     *
     * @param player  The player who has toggled their WorldEdit selection visualization.
     * @param enabled The state representing whether or not the player has enabled
     *                or disable their WorldEdit selection visualization.
     */
    public VisualizationToggleEvent(@NotNull Player player, boolean enabled) {
        this.player = player;
        this.enabled = enabled;
    }

    /**
     * Gets a player who has toggled their WorldEdit selection visualization.
     *
     * @return Returns player who has toggled their WorldEdit selection visualization.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets a state representing whether or not a player has enabled
     * or disable their WorldEdit selection visualization.
     *
     * @return Returns true if a player has enabled visualizations, or false if disabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
