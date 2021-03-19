package fr.mrmicky.worldeditselectionvisualizer.selection;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegionInfo {

    @NotNull
    private final Vector3d minimum;
    @NotNull
    private final Vector3d maximum;

    private final int width;
    private final int length;
    private final int height;

    private final long volume;
    private final int points;

    public RegionInfo(@NotNull RegionAdapter regionAdapter) {
        Region region = regionAdapter.getRegion();

        minimum = regionAdapter.getMinimumPoint();
        maximum = regionAdapter.getMinimumPoint();

        width = region.getWidth();
        length = region.getLength();
        height = region.getHeight();

        volume = regionAdapter.getVolume();
        points = region instanceof ConvexPolyhedralRegion ? ((ConvexPolyhedralRegion) region).getTriangles().size() : 0;
    }

    public RegionInfo(@NotNull Vector3d minimum, @NotNull Vector3d maximum, int width, int length, int height, int volume, int points) {
        this.minimum = Objects.requireNonNull(minimum, "minimum");
        this.maximum = Objects.requireNonNull(maximum, "maximum");
        this.width = width;
        this.length = length;
        this.height = height;
        this.volume = volume;
        this.points = points;
    }

    @NotNull
    public Vector3d getMinimum() {
        return minimum;
    }

    @NotNull
    public Vector3d getMaximum() {
        return maximum;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public long getVolume() {
        return volume;
    }

    public int getPoints() {
        return points;
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
        return width == region.width
                && length == region.length
                && height == region.height
                && volume == region.volume
                && points == region.points
                && minimum.equals(region.minimum)
                && maximum.equals(region.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum, width, length, height, volume, points);
    }

    @Override
    public String toString() {
        return "RegionInfo{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ", volume=" + volume +
                ", points=" + points +
                '}';
    }
}
