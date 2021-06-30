package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;

import java.util.Collection;
import java.util.Collections;

public class SelectionPoints {

    private final Collection<Shape> primary;
    private final Collection<Shape> secondary;

    public SelectionPoints(Collection<Shape> primary, Collection<Shape> secondary) {
        this.primary = Collections.unmodifiableCollection(primary);
        this.secondary = Collections.unmodifiableCollection(secondary);
    }

    public Collection<Shape> getPrimary() {
        return this.primary;
    }

    public Collection<Shape> getSecondary() {
        return this.secondary;
    }
}
