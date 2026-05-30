package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

@NullMarked
public class SelectionPoints {

    private final Collection<Shape> primary;
    private final Collection<Shape> secondary;
    private final Collection<Shape> origin;

    public SelectionPoints(Collection<Shape> primary, Collection<Shape> secondary) {
        this(primary, secondary, null);
    }

    public SelectionPoints(Collection<Shape> primary,
                           Collection<Shape> secondary,
                           @Nullable Shape origin) {
        this.primary = Collections.unmodifiableCollection(primary);
        this.secondary = Collections.unmodifiableCollection(secondary);
        this.origin = origin != null ? Collections.singletonList(origin) : Collections.emptyList();
    }

    public Collection<Shape> get(DisplayType type) {
        switch (type) {
            case PRIMARY:
                return this.primary;
            case SECONDARY:
                return this.secondary;
            case ORIGIN:
                return this.origin;
            default:
                return Collections.emptyList();
        }
    }
}
