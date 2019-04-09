package com.rojel.wesv;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldEditSelectionToggleEvent extends Event {

    /**
     * A list of all handlers that listen for this event.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * A player who has toggled their WorldEdit selection visualization.
     */
    private final Player player;

    /**
     * A state representing whether or not a player has enabled
     * or disable their WorldEdit selection visualization.
     */
    private final boolean toggle;

    /**
     * Constructor. Creates a new custom "WorldEditSelectionToggleEvent" event.
     * 
     * @param player The player who has toggled their WorldEdit selection visualization.
     * @param toggle The state representing whether or not the player has enabled
     *               or disable their WorldEdit selection visualization.
     */
    public WorldEditSelectionToggleEvent(Player player, boolean toggle) {
        this.player = player;
        this.toggle = toggle;
    }

    /**
     * Gets a list of handlers for this event.
     *
     * @return Returns list of handlers which listen to this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets a list of handlers for this event.
     *
     * @return Returns list of handlers which listen to this event.
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets a player who has toggled their WorldEdit selection visualization.
     *
     * @return Returns player who has toggled their WorldEdit selection visualization.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets a state representing whether or not a player has enabled
     * or disable their WorldEdit selection visualization.
     * 
     * @return Returns true if a player has enabled visualizations, or false if disabled. 
     */
    public boolean getToggleState() {
        return toggle;
    }
}
