package fr.mrmicky.worldeditselectionvisualizer.selection;

import java.util.Locale;

public enum SelectionType {

    SELECTION(true),
    CLIPBOARD(false);

    private static final SelectionType[] VALUES = values();

    private final boolean enabledByDefault;
    private final String name;

    SelectionType(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
        this.name = name().toLowerCase(Locale.ROOT);
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public String getName() {
        return name;
    }

    static SelectionType[] getValues() {
        return VALUES;
    }
}
