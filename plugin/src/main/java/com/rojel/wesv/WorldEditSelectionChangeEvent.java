package com.rojel.wesv;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the WorldEdit selection of a player changed
 *
 * @author rojel
 * @author Martin Ambrus
 */
public class WorldEditSelectionChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Region region;

    /**
     * Creates a new custom "WorldEditSelectionChange" event.
     *
     * @param player The player for who to listen to this event.
     * @param region The region in which to listen to this event.
     */
    public WorldEditSelectionChangeEvent(Player player, Region region) {
        this.player = player;
        this.region = region;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
