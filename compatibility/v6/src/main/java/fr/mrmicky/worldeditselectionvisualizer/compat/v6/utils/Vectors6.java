package fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

public final class Vectors6 {

    private Vectors6() {
        throw new UnsupportedOperationException();
    }

    public static Vector3d toVector3d(BlockVector vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3d toVector3d(Vector vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector toVector(Vector3d vec) {
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }
}
