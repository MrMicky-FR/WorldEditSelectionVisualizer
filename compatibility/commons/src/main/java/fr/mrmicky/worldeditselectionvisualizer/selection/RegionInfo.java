package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegionInfo {

    private final @NotNull RegionAdapter regionAdapter;
    private final @NotNull Vector3d minimum;
    private final @NotNull Vector3d maximum;

    private final int width;
    private final int length;
    private final int height;

    private final long volume;
    private final int points;

    public RegionInfo(@NotNull RegionAdapter regionAdapter) {
        this.regionAdapter = regionAdapter;

        Region region = regionAdapter.getRegion();
        this.minimum = regionAdapter.getMinimumPoint();
        this.maximum = regionAdapter.getMinimumPoint();
        this.width = region.getWidth();
        this.length = region.getLength();
        this.height = region.getHeight();
        this.volume = regionAdapter.getVolume();
        this.points = region instanceof ConvexPolyhedralRegion
                ? ((ConvexPolyhedralRegion) region).getTriangles().size() : 0;
    }

    public @NotNull RegionAdapter getRegionAdapter() {
        return this.regionAdapter;
    }

    public @NotNull Vector3d getMinimum() {
        return this.minimum;
    }

    public @NotNull Vector3d getMaximum() {
        return this.maximum;
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

    public int getPoints() {
        return this.points;
    }

    @Override
    public boolean equals(Object o) {
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
                && this.points == region.points
                && this.minimum.equals(region.minimum)
                && this.maximum.equals(region.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minimum, this.maximum, this.width,
                this.length, this.height, this.volume, this.points);
    }

    @Override
    public String toString() {
        return "RegionInfo{" +
                "minimum=" + this.minimum +
                ", maximum=" + this.maximum +
                ", width=" + this.width +
                ", length=" + this.length +
                ", height=" + this.height +
                ", volume=" + this.volume +
                ", points=" + this.points +
                '}';
    }
}
