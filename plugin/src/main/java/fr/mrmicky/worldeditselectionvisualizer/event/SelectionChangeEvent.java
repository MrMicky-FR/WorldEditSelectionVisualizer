package fr.mrmicky.worldeditselectionvisualizer.event;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when the WorldEdit selection of a player changed
 */
public class SelectionChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    private final Player player;

    @Nullable
    private final Region region;

    /**
     * Creates a new SelectionChangeEvent.
     *
     * @param player The player for who to listen to this event.
     * @param region The region in which to listen to this event.
     */
    public SelectionChangeEvent(@NotNull Player player, @Nullable Region region) {
        this.player = player;
        this.region = region;
    }

    /**
     * Gets a player for who listening to this event is enabled.
     *
     * @return Returns player for who listening to this event is enabled.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets a region in which listening to this event is enabled.
     *
     * @return Returns region in which listening to this event is enabled.
     */
    @Nullable
    public Region getRegion() {
        return region;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
