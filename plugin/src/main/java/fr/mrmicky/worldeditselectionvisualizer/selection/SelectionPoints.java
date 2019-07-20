package fr.mrmicky.worldeditselectionvisualizer.selection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class SelectionPoints {

    private final Collection<ImmutableVector> primaryPoints = new HashSet<>();
    private final Collection<ImmutableVector> secondaryPoints = new HashSet<>();

    @NotNull
    public Collection<ImmutableVector> primary() {
        return primaryPoints;
    }

    @NotNull
    public Collection<ImmutableVector> secondary() {
        return secondaryPoints;
    }
}
