package fr.mrmicky.worldeditselectionvisualizer.selection;

import java.util.Locale;

public enum SelectionType {

    SELECTION, CLIPBOARD;

    private static final SelectionType[] VALUES = values();

    private final String name;

    SelectionType() {
        this.name = name().toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return this.name;
    }

    static SelectionType[] getValues() {
        return VALUES;
    }
}
