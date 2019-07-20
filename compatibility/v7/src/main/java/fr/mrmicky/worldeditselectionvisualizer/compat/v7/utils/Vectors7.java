package fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;

public final class Vectors7 {

    private Vectors7() {
        throw new UnsupportedOperationException();
    }

    public static ImmutableVector toImmutableVector(Vector3 vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static ImmutableVector toImmutableVector(BlockVector3 vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    public static BlockVector3 toBlockVector3(ImmutableVector vec) {
        return BlockVector3.at(vec.getX(), vec.getY(), vec.getZ());
    }
}
