package fr.mrmicky.worldeditselectionvisualizer.event;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Called when the WorldEdit selection of a player changed
 */
public class SelectionChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final @NotNull Player player;
    private final @Nullable Region region;

    /**
     * Creates a new SelectionChangeEvent.
     *
     * @param player The player for who to listen to this event.
     * @param region The region in which to listen to this event.
     */
    public SelectionChangeEvent(@NotNull Player player, @Nullable Region region) {
        this.player = Objects.requireNonNull(player, "player");
        this.region = region;
    }

    /**
     * Gets a player for who listening to this event is enabled.
     *
     * @return Returns player for who listening to this event is enabled.
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * Gets a region in which listening to this event is enabled.
     *
     * @return Returns region in which listening to this event is enabled.
     */
    public @Nullable Region getRegion() {
        return this.region;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
