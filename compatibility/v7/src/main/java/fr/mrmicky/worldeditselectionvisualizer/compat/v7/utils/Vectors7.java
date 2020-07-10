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
        if (vec == Vector3.ZERO)
            return Vector3d.ZERO;
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    @NotNull
    public static Vector3d toVector3d(BlockVector3 vec) {
        if (vec == BlockVector3.ZERO)
            return Vector3d.ZERO;
        return new Vector3d(vec.getX(), vec.getY(), vec.getZ());
    }

    @NotNull
    public static Vector3 toVector3(Vector3d vec) {
        if (vec == Vector3d.ZERO)
            return Vector3.ZERO;
        return Vector3.at(vec.getX(), vec.getY(), vec.getZ());
    }

    @NotNull
    public static BlockVector3 toBlockVector3(Vector3d vec) {
        if (vec == Vector3d.ZERO)
            return BlockVector3.ZERO;
        return BlockVector3.at(vec.getX(), vec.getY(), vec.getZ());
    }
}
