package fr.mrmicky.worldeditselectionvisualizer.config;

import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import org.jetbrains.annotations.NotNull;

public class GlobalSelectionConfig {

    private final int fadeDelay;
    private final int maxSelectionSize;

    @NotNull
    private final SelectionConfig primary;
    @NotNull
    private final SelectionConfig secondary;
    @NotNull
    private final SelectionConfig origin;

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
        return fadeDelay;
    }

    public int getMaxSelectionSize() {
        return maxSelectionSize;
    }

    @NotNull
    public SelectionConfig primary() {
        return primary;
    }

    @NotNull
    public SelectionConfig secondary() {
        return secondary;
    }

    @NotNull
    public SelectionConfig origin() {
        return origin;
    }

    @NotNull
    public SelectionConfig byType(DisplayType type) {
        switch (type) {
            case PRIMARY:
                return primary;
            case SECONDARY:
                return secondary;
            case ORIGIN:
                return origin;
        }

        throw new IllegalArgumentException("Invalid display type: " + type);
    }
}
