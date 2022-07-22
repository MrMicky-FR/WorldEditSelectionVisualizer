package fr.mrmicky.worldeditselectionvisualizer.selection;

import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;

public class SelectionPoints {

    private final @NotNull Collection<Shape> primary;
    private final @NotNull Collection<Shape> secondary;
    private final @NotNull Collection<Shape> origin;

    public SelectionPoints(Collection<Shape> primary, Collection<Shape> secondary) {
        this(primary, secondary, null);
    }

    public SelectionPoints(Collection<Shape> primary,
                           Collection<Shape> secondary,
                           @Nullable Shape origin) {
        this.primary = Collections.unmodifiableCollection(primary);
        this.secondary = Collections.unmodifiableCollection(secondary);
        this.origin = (origin != null) ? Collections.singletonList(origin) : Collections.emptyList();
    }

    @Unmodifiable
    public @NotNull Collection<Shape> get(DisplayType type) {
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
