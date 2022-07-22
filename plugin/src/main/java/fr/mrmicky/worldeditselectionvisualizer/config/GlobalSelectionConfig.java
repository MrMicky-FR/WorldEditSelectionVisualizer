package fr.mrmicky.worldeditselectionvisualizer.config;

import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import org.jetbrains.annotations.NotNull;

public class GlobalSelectionConfig {

    private final int fadeDelay;
    private final int maxSelectionSize;

    private final @NotNull SelectionConfig primary;
    private final @NotNull SelectionConfig secondary;
    private final @NotNull SelectionConfig origin;

    public GlobalSelectionConfig(int fadeDelay, int maxSelectionSize,
                                 @NotNull SelectionConfig primary,
                                 @NotNull SelectionConfig secondary,
                                 @NotNull SelectionConfig origin) {
        this.fadeDelay = fadeDelay;
        this.maxSelectionSize = maxSelectionSize;
        this.primary = primary;
        this.secondary = secondary;
        this.origin = origin;
    }

    public int getFadeDelay() {
        return this.fadeDelay;
    }

    public int getMaxSelectionSize() {
        return this.maxSelectionSize;
    }

    public @NotNull SelectionConfig primary() {
        return this.primary;
    }

    public @NotNull SelectionConfig secondary() {
        return this.secondary;
    }

    public @NotNull SelectionConfig origin() {
        return this.origin;
    }

    public @NotNull SelectionConfig byType(DisplayType type) {
        switch (type) {
            case PRIMARY:
                return this.primary;
            case SECONDARY:
                return this.secondary;
            case ORIGIN:
                return this.origin;
        }

        throw new IllegalArgumentException("Invalid display type: " + type);
    }
}
