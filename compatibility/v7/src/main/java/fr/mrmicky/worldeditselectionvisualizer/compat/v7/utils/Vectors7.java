package fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

public final class Vectors7 {

    private Vectors7() {
        throw new UnsupportedOperationException();
    }

    public static Vector3d toVector3d(Vector3 vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vector3d toVector3d(BlockVector3 vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    public static BlockVector3 toBlockVector3(Vector3d vec) {
        return BlockVector3.at(vec.getX(), vec.getY(), vec.getZ());
    }
}
