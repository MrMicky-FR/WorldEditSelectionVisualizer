package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public class PlayerSelection {

    @NotNull
    private final SelectionType selectionType;

    @Nullable
    private SelectionPoints selectionPoints;

    @NotNull
    private Vector3d origin = Vector3d.ZERO;

    @Nullable
    private Instant expireTime;

    @Nullable
    private RegionInfo lastSelectedRegion;
    private boolean lastSelectionTooLarge;

    public PlayerSelection(@NotNull SelectionType selectionType) {
        this.selectionType = Objects.requireNonNull(selectionType, "selectionType");
    }

    @Nullable
    public SelectionPoints getSelectionPoints() {
        return selectionPoints;
    }

    @NotNull
    public Vector3d getOrigin() {
        return origin;
    }

    @Nullable
    public Instant getExpireTime() {
        return expireTime;
    }

    @Nullable
    public RegionInfo getLastSelectedRegion() {
        return lastSelectedRegion;
    }

    public boolean isLastSelectionTooLarge() {
        return lastSelectionTooLarge;
    }

    public void setLastSelectionTooLarge(boolean lastSelectionTooLarge) {
        this.lastSelectionTooLarge = lastSelectionTooLarge;
    }

    @NotNull
    public SelectionType getSelectionType() {
        return selectionType;
    }

    @Contract("-> this")
    public PlayerSelection verifyExpireTime() {
        if (expireTime != null && expireTime.isBefore(Instant.now())) {
            expireTime = null;
            selectionPoints = null;
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

        lastSelectionTooLarge = false;
        expireTime = expireSeconds > 0 ? Instant.now().plusSeconds(expireSeconds) : null;
    }

    public void resetSelection() {
        resetSelection(null);
    }

    public void resetSelection(RegionInfo lastSelectedRegion) {
        this.lastSelectedRegion = lastSelectedRegion;

        selectionPoints = null;
        origin = Vector3d.ZERO;
        expireTime = null;
        lastSelectionTooLarge = false;
    }
}
