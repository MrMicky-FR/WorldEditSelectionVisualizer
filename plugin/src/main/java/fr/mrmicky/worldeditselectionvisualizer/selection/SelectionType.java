package fr.mrmicky.worldeditselectionvisualizer.selection;

public enum SelectionType {

    SELECTION(true),
    CLIPBOARD(false);

    private final boolean enabledByDefault;
    private final String name;

    SelectionType(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
        this.name = name().toLowerCase();
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public String getName() {
        return name;
    }
}
