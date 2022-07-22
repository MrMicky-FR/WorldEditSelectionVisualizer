package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public class PlayerSelection {

    private final @NotNull SelectionType selectionType;

    private @Nullable SelectionPoints selectionPoints;
    private @NotNull Vector3d origin = Vector3d.ZERO;
    private @Nullable Instant expireTime;
    private @Nullable RegionInfo lastSelectedRegion;
    private boolean lastSelectionTooLarge;

    public PlayerSelection(@NotNull SelectionType selectionType) {
        this.selectionType = Objects.requireNonNull(selectionType, "selectionType");
    }

    public @Nullable SelectionPoints getSelectionPoints() {
        return this.selectionPoints;
    }

    public @NotNull Vector3d getOrigin() {
        return this.origin;
    }

    public @Nullable Instant getExpireTime() {
        return this.expireTime;
    }

    public @Nullable RegionInfo getLastSelectedRegion() {
        return this.lastSelectedRegion;
    }

    public boolean isLastSelectionTooLarge() {
        return this.lastSelectionTooLarge;
    }

    public void setLastSelectionTooLarge(boolean lastSelectionTooLarge) {
        this.lastSelectionTooLarge = lastSelectionTooLarge;
    }

    public @NotNull SelectionType getSelectionType() {
        return this.selectionType;
    }

    @Contract("-> this")
    public PlayerSelection verifyExpireTime() {
        if (this.expireTime != null && this.expireTime.isBefore(Instant.now())) {
            this.expireTime = null;
            this.selectionPoints = null;
        }
        return this;
    }

    public void updateSelection(@Nullable SelectionPoints selectionPoints,
                                @Nullable RegionInfo lastSelectedRegion,
                                @NotNull Vector3d origin,
                                int expireSeconds) {
        this.selectionPoints = selectionPoints;
        this.lastSelectedRegion = lastSelectedRegion;
        this.origin = origin;
        this.lastSelectionTooLarge = false;
        this.expireTime = expireSeconds > 0 ? Instant.now().plusSeconds(expireSeconds) : null;
    }

    public void resetSelection() {
        resetSelection(null);
    }

    public void resetSelection(RegionInfo lastSelectedRegion) {
        this.lastSelectedRegion = lastSelectedRegion;
        this.selectionPoints = null;
        this.origin = Vector3d.ZERO;
        this.expireTime = null;
        this.lastSelectionTooLarge = false;
    }
}
