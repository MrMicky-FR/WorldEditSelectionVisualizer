package fr.mrmicky.worldeditselectionvisualizer.config;

import fr.mrmicky.worldeditselectionvisualizer.display.DisplayType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class VisualizationConfig {

    private final int fadeDelay;
    private final int maxSelectionSize;
    private final boolean cuboidTopBottom;

    private final DisplayConfig primary;
    private final DisplayConfig secondary;
    private final DisplayConfig origin;

    public VisualizationConfig(int fadeDelay, int maxSelectionSize,
                               boolean cuboidTopBottom,
                               DisplayConfig primary,
                               DisplayConfig secondary,
                               DisplayConfig origin) {
        this.fadeDelay = fadeDelay;
        this.maxSelectionSize = maxSelectionSize;
        this.cuboidTopBottom = cuboidTopBottom;
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

    public boolean hasCuboidTopBottom() {
        return this.cuboidTopBottom;
    }

    public DisplayConfig primary() {
        return this.primary;
    }

    public DisplayConfig secondary() {
        return this.secondary;
    }

    public DisplayConfig origin() {
        return this.origin;
    }

    public DisplayConfig byDisplay(DisplayType type) {
        switch (type) {
            case PRIMARY:
                return this.primary;
            case SECONDARY:
                return this.secondary;
            case ORIGIN:
                return this.origin;
            default:
                throw new IllegalArgumentException("Invalid display type: " + type);
        }
    }
}
