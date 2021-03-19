package fr.mrmicky.worldeditselectionvisualizer.selection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public class PlayerSelection {

    @NotNull
    private final SelectionType selectionType;

    @Nullable
    private SelectionPoints selectionPoints;

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

    public void setSelectionPoints(@Nullable SelectionPoints selectionPoints) {
        this.selectionPoints = selectionPoints;
    }

    @Nullable
    public Instant getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(@Nullable Instant expireTime) {
        this.expireTime = expireTime;
    }

    @Nullable
    public RegionInfo getLastSelectedRegion() {
        return lastSelectedRegion;
    }

    public void setLastSelectedRegion(@Nullable RegionInfo lastSelectedRegion) {
        this.lastSelectedRegion = lastSelectedRegion;
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

    public void checkExpireTime() {
        if (expireTime != null && expireTime.isBefore(Instant.now())) {
            expireTime = null;
            selectionPoints = null;
        }
    }

    public void updateSelection(@Nullable SelectionPoints selectionPoints, @Nullable RegionInfo lastSelectedRegion, int expireSeconds) {
        this.selectionPoints = selectionPoints;
        this.lastSelectedRegion = lastSelectedRegion;

        lastSelectionTooLarge = false;

        expireTime = expireSeconds > 0 ? Instant.now().plusSeconds(expireSeconds) : null;
    }

    public void resetSelection() {
        resetSelection(null);
    }

    public void resetSelection(RegionInfo lastSelectedRegion) {
        this.lastSelectedRegion = lastSelectedRegion;

        selectionPoints = null;
        expireTime = null;
        lastSelectionTooLarge = false;
    }
}
