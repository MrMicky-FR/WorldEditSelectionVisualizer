package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;

public class SelectionPoints {

    private final Collection<Shape> primary;
    private final Collection<Shape> secondary;

    public SelectionPoints(Collection<Shape> primary, Collection<Shape> secondary) {
        this.primary = Collections.unmodifiableCollection(primary);
        this.secondary = Collections.unmodifiableCollection(secondary);
    }

    @Unmodifiable
    public Collection<Shape> getPrimary() {
        return this.primary;
    }

    @Unmodifiable
    public Collection<Shape> getSecondary() {
        return this.secondary;
    }
}
