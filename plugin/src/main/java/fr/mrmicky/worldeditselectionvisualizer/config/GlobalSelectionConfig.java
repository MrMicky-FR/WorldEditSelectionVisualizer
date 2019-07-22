package fr.mrmicky.worldeditselectionvisualizer.config;

import org.jetbrains.annotations.NotNull;

public class GlobalSelectionConfig {

    private final int fadeDelay;
    private final int maxSelectionSize;

    @NotNull
    private final SelectionConfig primary;
    @NotNull
    private final SelectionConfig secondary;

    public GlobalSelectionConfig(int fadeDelay, int maxSelectionSize, @NotNull SelectionConfig primary, @NotNull SelectionConfig secondary) {
        this.fadeDelay = fadeDelay;
        this.maxSelectionSize = maxSelectionSize;
        this.primary = primary;
        this.secondary = secondary;
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
}
