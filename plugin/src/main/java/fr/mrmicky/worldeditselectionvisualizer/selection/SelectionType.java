package fr.mrmicky.worldeditselectionvisualizer.selection;

public enum SelectionType {

    SELECTION(true),
    CLIPBOARD(false);

    private final boolean enabledByDefault;

    SelectionType(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }
}
