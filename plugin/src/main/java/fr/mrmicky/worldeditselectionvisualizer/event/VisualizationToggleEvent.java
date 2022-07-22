package fr.mrmicky.worldeditselectionvisualizer.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Called when a player toggled his selection visualizer
 */
public class VisualizationToggleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final @NotNull Player player;
    private final boolean enabled;

    /**
     * Creates a new custom "VisualizationToggleEvent" event.
     *
     * @param player  The player who has toggled their WorldEdit selection visualization.
     * @param enabled The state representing whether the player has enabled
     *                or disable their WorldEdit selection visualization.
     */
    public VisualizationToggleEvent(@NotNull Player player, boolean enabled) {
        this.player = Objects.requireNonNull(player, "player");
        this.enabled = enabled;
    }

    /**
     * Gets a player who has toggled their WorldEdit selection visualization.
     *
     * @return Returns player who has toggled their WorldEdit selection visualization.
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * Gets a state representing whether a player has enabled
     * or disable their WorldEdit selection visualization.
     *
     * @return Returns true if a player has enabled visualizations, or false if disabled.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
