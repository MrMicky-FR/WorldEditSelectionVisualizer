package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.math.BlockVector3;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class SelectionPoints {
    private final Collection<Vector3d> primaryPositions = new HashSet<>();
    private Vector3d secondaryPosition = null;

    private final Collection<Vector3d> primaryPoints = new HashSet<>();
    private final Collection<Vector3d> secondaryPoints = new HashSet<>();

    @NotNull
    private final Vector3d origin;

    public SelectionPoints(@NotNull Vector3d origin) {
        this.origin = origin;
    }

    @NotNull
    public Collection<Vector3d> primary() {
        return primaryPoints;
    }

    @NotNull
    public Collection<Vector3d> secondary() {
        return secondaryPoints;
    }

    @NotNull
    public Vector3d origin() {
        return origin;
    }

    @NotNull
    public Collection<Vector3d> primaryPositions() { return primaryPositions; }

    public void setSecondaryPosition(Vector3d secondaryPosition) {
        this.secondaryPosition = secondaryPosition;
    }

    public Vector3d getSecondaryPosition() { return secondaryPosition; }
}
