package fr.mrmicky.worldeditselectionvisualizer.event;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Called when the WorldEdit clipboard of a player changes.
 * Detected by polling, so it may lag by up to {@code selection-update-interval} ticks,
 * and is only fired for players who have clipboard visualization enabled (off by default).
 */
@NullMarked
public class ClipboardChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final @Nullable Region region;

    /**
     * Creates a new {@link ClipboardChangeEvent} for the given player with the specified region.
     *
     * @param player the player whose clipboard changed
     * @param region the new region in the clipboard, or null if the clipboard is empty
     */
    public ClipboardChangeEvent(Player player, @Nullable Region region) {
        this.player = Objects.requireNonNull(player, "player");
        this.region = region;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns the player whose clipboard changed.
     *
     * @return the player whose clipboard changed
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the region in the clipboard of the player.
     * This is a transformed copy, so modifying it does not affect the clipboard.
     *
     * @return the region in the clipboard, or null if the clipboard is empty
     */
    public @Nullable Region getRegion() {
        return this.region;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
