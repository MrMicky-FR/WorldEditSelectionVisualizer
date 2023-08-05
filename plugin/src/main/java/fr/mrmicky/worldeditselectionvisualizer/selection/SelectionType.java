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

    public static SelectionType from(String type) {
        try {
            return valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static SelectionType[] getValues() {
        return VALUES;
    }
}
