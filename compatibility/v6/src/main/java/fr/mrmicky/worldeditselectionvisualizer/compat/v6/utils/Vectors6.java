package fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;

public final class Vectors6 {

    private Vectors6() {
        throw new UnsupportedOperationException();
    }

    public static ImmutableVector toImmutableVector(BlockVector vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static ImmutableVector toImmutableVector(Vector vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector toVector(ImmutableVector vec) {
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }
}
