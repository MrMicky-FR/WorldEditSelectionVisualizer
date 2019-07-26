package fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

public final class Vectors7 {

    private Vectors7() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Vector3d toVector3d(Vector3 vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    @NotNull
    public static Vector3d toVector3d(BlockVector3 vec) {
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    @NotNull
    public static Vector3 toVector3(Vector3d vec) {
        return Vector3.at(vec.getX(), vec.getY(), vec.getZ());
    }

    @NotNull
    public static BlockVector3 toBlockVector3(Vector3d vec) {
        return BlockVector3.at(vec.getX(), vec.getY(), vec.getZ());
    }
}
