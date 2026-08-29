package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class RegionInfo {

    private final Vector3d minimum;
    private final Vector3d maximum;
    private final Vector3d origin;

    private final int width;
    private final int length;
    private final int height;

    private final long volume;
    private final int triangles;

    public RegionInfo(RegionAdapter regionAdapter) {
        Region region = regionAdapter.getRegion();
        this.minimum = regionAdapter.getMinimumPoint();
        this.maximum = regionAdapter.getMaximumPoint();
        this.origin = region instanceof CuboidRegion
                ? regionAdapter.getCuboidPos1()
                : regionAdapter.getCenter();
        this.width = region.getWidth();
        this.length = region.getLength();
        this.height = region.getHeight();
        this.volume = regionAdapter.getVolume();
        this.triangles = region instanceof ConvexPolyhedralRegion
                // triangles lookup is a cheap read operation
                ? ((ConvexPolyhedralRegion) region).getTriangles().size() : 0;
    }

    public Vector3d getMinimum() {
        return this.minimum;
    }

    public Vector3d getMaximum() {
        return this.maximum;
    }

    public Vector3d getOrigin() {
        return this.origin;
    }

    public int getWidth() {
        return this.width;
    }

    public int getLength() {
        return this.length;
    }

    public int getHeight() {
        return this.height;
    }

    public long getVolume() {
        return this.volume;
    }

    public int getTriangles() {
        return this.triangles;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegionInfo)) {
            return false;
        }

        RegionInfo region = (RegionInfo) o;
        return this.width == region.width
                && this.length == region.length
                && this.height == region.height
                && this.volume == region.volume
                && this.triangles == region.triangles
                && this.minimum.equals(region.minimum)
                && this.maximum.equals(region.maximum)
                && this.origin.equals(region.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minimum, this.maximum, this.origin, this.width,
                this.length, this.height, this.volume, this.triangles);
    }

    @Override
    public String toString() {
        return "RegionInfo{" +
                "minimum=" + this.minimum +
                ", maximum=" + this.maximum +
                ", origin=" + this.origin +
                ", width=" + this.width +
                ", length=" + this.length +
                ", height=" + this.height +
                ", volume=" + this.volume +
                ", triangles=" + this.triangles +
                '}';
    }
}
