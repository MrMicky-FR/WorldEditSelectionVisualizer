package com.rojel.wesv.event;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A custom "WorldEditSelectionChange" Bukkit event class.
 *
 * @author rojel
 * @author Martin Ambrus
 * @since 1.0a
 */
public class WorldEditSelectionChangeEvent extends Event {

    /**
     * A list of all handlers that listen for this event.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * A player for who to listen to this event.
     */
    private final Player player;

    /**
     * WorldEdit region for this event.
     */
    private final Region region;

    /**
     * Constructor. Creates a new custom "WorldEditSelectionChange" event.
     *
     * @param player The player for who to listen to this event.
     * @param region The region in which to listen to this event.
     */
    public WorldEditSelectionChangeEvent(final Player player, final Region region) {
        this.player = player;
        this.region = region;
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
     * Gets a player for who listening to this event is enabled.
     *
     * @return Returns player for who listening to this event is enabled.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets a region in which listening to this event is enabled.
     *
     * @return Returns region in which listening to this event is enabled.
     */
    public Region getRegion() {
        return region;
    }
}
